package app.onedayofwar.Battle.BluetoothConnection;

/**
 * Created by Slava on 09.02.2015.
 */

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.Bonus.ForBonusEnemy;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
public class ConnectedThread extends Thread
{
    private final BluetoothController btController;
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    public String recievedData;

    public ConnectedThread(BluetoothSocket socket, BluetoothController btController)
    {
        Log.i("CONNECTED", "CREATE");
        recievedData = "empty";
        this.btController = btController;
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try
        {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e)
        {
            Log.i("CONNECTED.GET_STREAMS", e.getMessage());
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run()
    {
        Log.i("CONNECTED", "START");
        byte[] buffer = new byte[1024];
        int bytes;

        //btController.ShowToast("CONNECT TO" + socket.getRemoteDevice().getName());
        while (true)
        {
            try
            {
                bytes = inputStream.read(buffer);
                read(buffer, bytes);
                Log.i("CONNECTED.READ", "txt - " + recievedData);

                if(!btController.isEnemyConnected)
                {
                    if (recievedData.equals(HandlerMSG.ACCEPT_FIGHT_REQUEST))
                    {
                        //btController.ShowToast("CONNECTED TO " + socket.getRemoteDevice().getName());
                        btController.isEnemyConnected = true;
                        btController.StartBattle(true);
                    }
                    else
                    {
                        btController.StartServerThread();
                        btController.isEnemyConnected = false;
                        CloseSocket();
                        btController.StopConnectedThread();
                        break;
                    }
                }
                else
                {
                    CheckWin();
                    CheckAttack();
                    CheckAttackResult();
                    TakeGlareClaim();
                    TakeGlairResult();
                    GetPVOInfo();
                    GetPVOResult();
                    GetReloadInfo();
                }
            }
            catch (IOException e)
            {
                Log.i("CONNECTED.READ", e.getMessage());
                if(btController.isEnemyConnected)
                {
                    btController.ConnectionLost();
                }
                else
                {
                    btController.StartServerThread();
                    btController.isEnemyConnected = false;
                    CloseSocket();
                    btController.StopConnectedThread();
                }
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     *
     */

    public void write(String data)
    {
        byte[] msg;
        try
        {
            msg = data.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i("CONNECTED.WRITE", e.getMessage());
            return;
        }

        try
        {
            outputStream.write(msg);
        }
        catch (IOException e)
        {
            Log.i("CONNECTED.WRITE", e.getMessage());
        }
    }
    public void read(byte[] msg, int length)
    {
        byte[] tmp = new byte[length];
        for(int i = 0; i < length; i ++)
        {
            tmp[i] = msg[i];
        }
        try
        {
            recievedData = new String(tmp, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            Log.i("CONNECTED.READ", e.getMessage());
        }
    }

    public void CloseSocket()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            Log.i("CONNECTED.CLOSE_SOCKET", e.getMessage());
        }
    }

    public void CheckWin()
    {
        if(recievedData.equals(HandlerMSG.LOSE))
        {
            btController.GameOver();
            Log.i("CONNECTED.READ", "WIN");
        }
    }

    public void CheckAttack()
    {
        if(recievedData.startsWith(HandlerMSG.ATTACK))
        {
            String[] tmp = recievedData.split("\\|");
            BattleEnemy.target.x = Integer.parseInt(tmp[1]);
            Log.i("CONNECTED.ATK_TARGET.X", "" + BattleEnemy.target.x);
            BattleEnemy.target.y = Integer.parseInt(tmp[2]);
            Log.i("CONNECTED.ATK_TARGET.Y", "" + BattleEnemy.target.y);
            BattleEnemy.damage = Integer.parseInt(tmp[3]);
            Log.i("CONNECTED.ATK_DAMAGE", "" + BattleEnemy.damage);
            BattleEnemy.weaponType = Byte.parseByte(tmp[4]);
            Log.i("CONNECTED.ATK_WEAPON_TYPE", "" + BattleEnemy.weaponType);
        }
    }

    public void CheckAttackResult()
    {
        if(recievedData.startsWith(HandlerMSG.ATTACK_RESULT))
        {
            String[] tmp = recievedData.split("\\|");

            if(tmp.length > 3)
                return;
            BattleEnemy.attackResult = Byte.parseByte(tmp[1]);
            Log.i("CONNECTED.ARSLT", "" + BattleEnemy.attackResult);

            if(tmp.length != 3)
                return;
            BattleEnemy.attackResultData = new String(tmp[2]);
            Log.i("CONNECTED.ARSLT_DATA", "" + BattleEnemy.attackResultData);
        }
    }
    //Прием данных для бонуса засвета
    public void TakeGlareClaim()
    {
        if(recievedData.startsWith(HandlerMSG.GLARE_MSG))
        {
            String[] tmp = recievedData.split("\\|");
            ForBonusEnemy.socket.x = (int)Float.parseFloat(tmp[1]);
            ForBonusEnemy.socket.y = (int)Float.parseFloat(tmp[2]);
            ForBonusEnemy.canISendResult = true;
        }
    }
    //Прием результата бонуса засвета
    public void TakeGlairResult()
    {
        if(recievedData.startsWith(HandlerMSG.GLARE_RESULT_MSG))
        {
            int k = 1;
            String[] tmp = recievedData.split("\\|");
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    ForBonusEnemy.glareArr[i][j] = Integer.parseInt(tmp[k]);
                    k++;
                }
            }
            ForBonusEnemy.canITakeResult = true;
        }
    }
    public void GetPVOInfo()
    {
        if(recievedData.startsWith(HandlerMSG.PVO_INFO))
            ForBonusEnemy.pvoSend = true;
    }
    public void GetPVOResult()
    {
        if(recievedData.startsWith(HandlerMSG.PVO_RESULT))
            ForBonusEnemy.pvoGet = true;
    }

    public void GetReloadInfo()
    {
        if(recievedData.startsWith(HandlerMSG.RELOAD_RESULT))
        {
            String[] tmp = recievedData.split("\\|");
            ForBonusEnemy.skill = Integer.parseInt(tmp[1]);
            ForBonusEnemy.reloadGet = true;
        }
    }
}
