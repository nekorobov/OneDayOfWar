package app.onedayofwar.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Battle.BluetoothConnection.HandlerMSG;
import app.onedayofwar.R;

public class BluetoothActivity extends Activity
{
    public static BluetoothController btController;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        btController.Load(this);
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case HandlerMSG.SHOW_ATTACK_REQUEST_DIALOG:
                        btController.ShowAttackDialog();
                        break;
                }
            }
        };
    }

    public void scanBtnClick(View view)
    {
        btController.Scan();
    }

    public void attackBtnClick(View view)
    {
        btController.SendAttackRequest();
    }

    public void ShowAttackDialog()
    {
        handler.obtainMessage(HandlerMSG.SHOW_ATTACK_REQUEST_DIALOG).sendToTarget();
    }

    @Override
    public void onBackPressed()
    {
        btController.Destroy();
        finish();
    }
}

