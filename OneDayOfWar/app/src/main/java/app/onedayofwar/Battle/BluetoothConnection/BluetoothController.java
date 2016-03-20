package app.onedayofwar.Battle.BluetoothConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import app.onedayofwar.Activities.BluetoothActivity;
import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.Battle;
import app.onedayofwar.Battle.System.GameOverView;
import app.onedayofwar.R;
import app.onedayofwar.System.GLView;

/**
 * Created by Slava on 09.02.2015.
 */
public class BluetoothController
{
    private static final UUID APP_UUID = UUID.fromString("9f691062-ff6b-4f86-9f6f-8329174d2343");
    private static final String APP_NAME = "ODOWBT";


    private BluetoothAdapter bluetoothAdapter;
    private ListView devicesListView;
    private ArrayList<BluetoothDevice> devices;
    private ArrayAdapter<String> btArrayAdapter;
    private int selectedDevice;
    private BluetoothActivity btActivity;
    private GLView glView;

    private ServerThread serverThread;
    private ClientThread clientThread;
    private ConnectedThread connectedThread;
    private BluetoothSocket enemySocket;
    public static boolean isLoaded;

    public boolean isEnemyConnected;

    public BluetoothController(GLView glView)
    {
        this.glView = glView;

        Log.i("BTC", "CONSTRUCTOR");
        glView.getActivity().startActivityForResult(new Intent(glView.getActivity(), BluetoothActivity.class), 17);
        BluetoothActivity.btController = this;

        isEnemyConnected = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isLoaded = false;

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        glView.getActivity().startActivity(discoverableIntent);
    }

    public void ShowAttackDialog()
    {
        if(!isLoaded)
            return;
        Log.i("BT", "RECEIVE ATTACK");
        AlertDialog.Builder attackRequestDialog = new AlertDialog.Builder(btActivity);
        attackRequestDialog.setTitle(enemySocket.getRemoteDevice().getName() + " вызывает вас на бой!");

        attackRequestDialog.setPositiveButton("В атаку!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                StopClientThread();
                StartConnectedThread(enemySocket);
                connectedThread.write(HandlerMSG.ACCEPT_FIGHT_REQUEST);
                isEnemyConnected = true;
                StartBattle(false);
            }
        });
        attackRequestDialog.setNegativeButton("Отказать", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                StopClientThread();
                StartConnectedThread(enemySocket);
                connectedThread.write(HandlerMSG.REJECT_FIGHT_REQUEST);
                StopConnectedThread();
                StartServerThread();
            }
        });
        attackRequestDialog.show();
    }

    public void ConnectionLost()
    {
        //Toast.makeText(glView.getActivity().getApplicationContext(), "CONNECTION LOST", Toast.LENGTH_SHORT).show();
        if(glView.getActivity().gameState == MainActivity.GameState.BRESULT)
            return;
        Log.i("BT", "CONNECTION LOST");
        Stop();
        glView.gotoMainMenu();
    }

    private void doDiscovery()
    {
        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    public void StartBattle(boolean isYourTurn)
    {
        BattlePlayer.fieldSize = 15;
        BattlePlayer.unitCount = new byte[]{2, 1, 0, 1, 0, 0};
        glView.getActivity().unregisterReceiver(myBluetoothReceiver);
        btActivity.setResult(isYourTurn ? Activity.RESULT_FIRST_USER : Activity.RESULT_OK);
        btActivity.finish();
        btActivity = null;
    }

    public void Load(BluetoothActivity activity)
    {
        btActivity = activity;
        isLoaded = true;
        devices = new ArrayList<>();
        devicesListView = (ListView)activity.findViewById(R.id.listView);
        btArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice);
        glView.getActivity().registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        Scan();
        devicesListView.setAdapter(btArrayAdapter);
        devicesListView.setOnItemClickListener(deviceClickListener);
        selectedDevice = -1;
        StartServerThread();
        Toast.makeText(glView.getActivity().getApplicationContext(), "Server Started", Toast.LENGTH_SHORT).show();
    }

    public void Destroy()
    {
        isLoaded = false;
        Stop();
        glView.getActivity().unregisterReceiver(myBluetoothReceiver);
    }

    public void Scan()
    {
        selectedDevice = -1;
        btArrayAdapter.clear();
        devices.clear();
        doDiscovery();
    }

    public void CancelScan()
    {
        bluetoothAdapter.cancelDiscovery();
    }

    //region Attack Request
    public void SendAttackRequest()
    {
        if(connectedThread == null && devices != null)
        {
            if (selectedDevice != -1)
            {
                StartClientThread(devices.get(selectedDevice));
            }
        }
    }

    public void ReceiveAttackRequest(BluetoothSocket eSocket)
    {
        enemySocket = eSocket;
        StopServerThread();
        btActivity.ShowAttackDialog();
    }
    //endregion

    //region Start Threads
    public void StartServerThread()
    {
        serverThread = new ServerThread(APP_NAME, APP_UUID, this);
        serverThread.start();
    }

    public void StartClientThread(BluetoothDevice targetDevice)
    {
        clientThread = new ClientThread(targetDevice, APP_UUID, this);
        clientThread.start();
    }

    public void StartConnectedThread(BluetoothSocket targetSocket)
    {
        connectedThread = new ConnectedThread(targetSocket, this);
        connectedThread.start();
    }
    //endregion

    //region Stop Threads
    public void StopClientThread()
    {
        if (clientThread != null)
        {
            clientThread = null;
        }
    }

    public void StopServerThread()
    {
        if (serverThread != null)
        {
            serverThread.CloseSocket();
            serverThread = null;
        }
    }

    public void StopConnectedThread()
    {
        if (connectedThread != null)
        {
            connectedThread = null;
        }
    }

    public void Stop()
    {
        CancelScan();
        if (connectedThread != null)
        {
            connectedThread.CloseSocket();
            connectedThread = null;
        }
        if (serverThread != null)
        {
            serverThread.CloseSocket();
            serverThread = null;
        }
        if (clientThread != null)
        {
            clientThread.CloseSocket();
            clientThread = null;
        }
    }
    //endregion

    //region Listeners
    private final AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
        {
            Toast.makeText(btActivity.getApplicationContext(), devices.get(position).getName() + "\n" + devices.get(position).getAddress(), Toast.LENGTH_SHORT).show();
            selectedDevice = position;
        }
    };

    private final BroadcastReceiver myBluetoothReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(int i = 0; i < devices.size(); i++)
                {
                    if(device.getAddress().equals(devices.get(i).getAddress()))
                    {
                        return;
                    }
                }
                devices.add(device);
                btArrayAdapter.add(device.getName());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    //endregion

    public void GameOver()
    {
        BattleEnemy.isLose = true;
    }

    public void SendData(String data)
    {
        connectedThread.write(data);
    }

    public String GetRecievedData()
    {
        return connectedThread.recievedData;
    }
}
