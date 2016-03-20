package app.onedayofwar.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.MainView;

public class MainActivity extends Activity
{
    private GLView glView;

    public enum GameState {BATTLE, BRESULT, MENU, CAMPAIGN}
    public GameState gameState;

    private BluetoothAdapter btAdapter;
    private BluetoothController btController;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gameState = GameState.MENU;

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        glView = new GLView(this, metrics.widthPixels, metrics.heightPixels);
        Assets.mainFont = Typeface.createFromAsset(getAssets(), "fonts/hollowpoint.ttf");
        setContentView(glView);
    }



    @Override
    public void onBackPressed()
    {
        if(gameState == GameState.BRESULT)
        {
            if(btController != null)
            {
                ResetBTController();
            }
            glView.gotoMainMenu();
            return;
        }

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);

        String title;
        String positive;
        String negative;
        switch(gameState)
        {
            case MENU:
                title = "Выйти?";
                positive = "Да!";
                negative = "нет!";
                break;
            case BATTLE:
                title = "Ваши приказания?";
                positive = "Отступаем!";
                negative = "Ни шагу назад!";
                break;
            case CAMPAIGN:
                title = "Выйти?";
                positive = "Да!";
                negative = "Нет!";
                break;
            default:
                title = "Выйти?";
                positive = "Да!";
                negative = "Нет!";
                break;
        }
        quitDialog.setTitle(title);

        quitDialog.setPositiveButton(positive, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (gameState == GameState.MENU)
                    finish();
                else
                {
                    if(gameState == GameState.BATTLE && btController != null)
                    {
                        ResetBTController();
                    }
                    glView.goBack();
                }
            }
        });

        quitDialog.setNegativeButton(negative, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){}
        });

        quitDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            if (btAdapter.isEnabled())
            {
                Log.i("BT", "BT SWITCHED ON");
                LoadBTController();
            }
        }
        if(requestCode == 17)
        {
            if(resultCode != RESULT_CANCELED)
            {
                MainView.startBTBattle = resultCode == RESULT_FIRST_USER ? (byte)1 : (byte)2;
            }
        }
    }

    public void CheckBT()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            Log.i("BT", "BT UNSUPPORTED");
            return;
        }
        if (btAdapter.isEnabled())
        {
            Log.i("BT", "BT ON");
            LoadBTController();
        }
        else
        {
            Log.i("BT", "BT OFF");
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 100);
        }
    }

    public BluetoothController getBtController()
    {
        return btController;
    }

    public void LoadBTController()
    {
        btController = new BluetoothController(glView);
    }

    public void ResetBTController()
    {
        if(btController == null)
            return;
        btController.Stop();
        btController = null;
    }
}
