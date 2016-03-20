package app.onedayofwar.Battle.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Slava on 09.02.2015.
 */
public class ServerThread extends Thread
{
    private final BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private final BluetoothController btController;

    public ServerThread(String NAME, UUID APP_UUID, BluetoothController btController)
    {
        Log.i("SERVER", "CREATE");
        this.btController = btController;
        // Use a temporary object that is later assigned to serverSocket,
        // because serverSocket is final
        BluetoothServerSocket tmp = null;
        try
        {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, APP_UUID);
        }
        catch (IOException e)
        {
            Log.i("SERVER.GET_SERVER_SOCKET", e.getMessage());
        }
        serverSocket = tmp;
    }

    public void run()
    {
        Log.i("SERVER", "START");
        // Keep listening until exception occurs or a socket is returned
        while (true)
        {
            try
            {
                socket = serverSocket.accept();
            }
            catch (IOException e)
            {
                Log.i("SERVER.ACCEPT", e.getMessage());
                break;
            }
            // If a connection was accepted
            if (socket != null)
            {
                //Do work to manage the connection (in a separate thread)
                CloseSocket();
                btController.ReceiveAttackRequest(socket);
                break;
            }
        }
    }

    /** Will CloseSocket the listening socket, and cause the thread to finish */
    public void CloseSocket()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            Log.i("SERVER.CLOSE_SOCKET", e.getMessage());
        }
    }
}
