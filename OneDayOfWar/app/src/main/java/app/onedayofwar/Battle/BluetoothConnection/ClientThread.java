package app.onedayofwar.Battle.BluetoothConnection;

/**
 * Created by Slava on 09.02.2015.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
public class ClientThread extends Thread
{
    private final BluetoothSocket targetSocket;
    private final BluetoothDevice device;
    private final BluetoothController btController;

    public ClientThread(BluetoothDevice device, UUID APP_UUID, BluetoothController btController)
    {
        Log.i("CLIENT", "CREATE");
        this.btController = btController;
        this.device = device;
        BluetoothSocket tmp = null;
        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try
        {
            tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
        }
        catch (IOException e)
        {
            Log.i("CLIENT.GET_SOCKET", e.getMessage());
        }
        targetSocket = tmp;
    }

    public void run()
    {
        Log.i("CLIENT", "START");
        // Always CloseSocket discovery because it will slow down a connection
        btController.CancelScan();
        // Make a connection to the BluetoothSocket
        try
        {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            targetSocket.connect();
        }
        catch (IOException e)
        {
            // Close the socket
            Log.i("CLIENT.CONNECT", e.getMessage());
            btController.StopClientThread();
            return;
        }

        // Reset the ConnectThread because we're done
        synchronized (btController)
        {
            btController.StartConnectedThread(targetSocket);
            btController.StopServerThread();
            btController.StopClientThread();
        }
    }

    public void CloseSocket()
    {
        try
        {
            targetSocket.close();
        }
        catch (IOException e)
        {
            Log.i("CLIENT.CLOSE_SOCKET", e.getMessage());
        }
    }
}
