package app.onedayofwar.Battle.Bonus;

import android.util.Log;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Bullet;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 27.03.2015.
 */
public class GlareBonus extends Bonus
{

    public GlareBonus(boolean isAvailable)
    {
        this.isAvailable = isAvailable;
        reload = 0;
        cost = 100;
        skill = 0;
        info = "Наш шпион на их территории, он расскажет вам\nо положении кораблей в квадрате 3*3.\nСтоимость бонуса: " + cost + "\nВремя перезрядки: " + reload;
    }

    public void doYourUglyJob(int[][] tmp, Field field)
    {
        Log.i("GLARE", "IN");
        Vector2 socket = ForBonusEnemy.socket;
        if(tmp[0][0] == 100)
        {
            if(field.GetShots()[(int)(socket.y - 1)][(int)(socket.x - 1)] != 2 )
                field.setShot((int) (socket.x - 1), (int) (socket.y - 1), (byte) 100);
        }
        else if(tmp[0][0] != -100 && field.GetShots()[(int)(socket.y - 1)][(int)(socket.x - 1)] != 1)
            field.setShot((int) (socket.x - 1), (int) (socket.y - 1), (byte) 3);

        if(tmp[0][1] == 100)
        {
            if(field.GetShots()[(int)(socket.y - 1)][(int)socket.x] != 2 )
                field.setShot((int) (socket.x), (int) (socket.y - 1), (byte) 100);
        }
        else if(tmp[0][1] != -100 && field.GetShots()[(int)(socket.y - 1)][(int)socket.x] != 1)
            field.setShot((int) (socket.x), (int) (socket.y - 1), (byte) 3);

        if(tmp[0][2] == 100)
        {
            if(field.GetShots()[(int)(socket.y - 1)][(int)(socket.x + 1)] != 2 )
                field.setShot((int) (socket.x + 1), (int) (socket.y - 1), (byte) 100);
        }
        else if(tmp[0][2] != -100 && field.GetShots()[(int)(socket.y - 1)][(int)(socket.x + 1)] != 1)
            field.setShot((int) (socket.x + 1), (int) (socket.y - 1), (byte) 3);

        if(tmp[1][0] == 100)
        {
            if(field.GetShots()[(int)(socket.y)][(int)(socket.x - 1)] != 2 )
                field.setShot((int) (socket.x - 1), (int) (socket.y), (byte) 100);
        }
        else if(tmp[1][0] != -100 && field.GetShots()[(int)(socket.y)][(int)(socket.x - 1)] != 1)
            field.setShot((int) (socket.x - 1), (int) (socket.y), (byte) 3);

        if(tmp[1][1] == 100)
        {
            if(field.GetShots()[(int)(socket.y)][(int)socket.x] != 2 )
                field.setShot((int) (socket.x), (int) (socket.y), (byte) 100);
        }
        else if(field.GetShots()[(int)(socket.y)][(int)socket.x] != 1)
            field.setShot((int) (socket.x), (int) (socket.y), (byte) 3);
        if(tmp[1][2] == 100)
        {
            if(field.GetShots()[(int)(socket.y)][(int)(socket.x + 1)] != 2 )
                field.setShot((int) (socket.x + 1), (int) (socket.y), (byte) 100);
        }
        else if(tmp[1][2] != -100 && field.GetShots()[(int)(socket.y)][(int)(socket.x + 1)] != 1)
            field.setShot((int) (socket.x + 1), (int) (socket.y), (byte) 3);

        if(tmp[2][0] == 100)
        {
            if(field.GetShots()[(int)(socket.y + 1)][(int)(socket.x - 1)] != 2 )
                field.setShot((int) (socket.x - 1), (int) (socket.y + 1), (byte) 100);
        }
        else if(tmp[2][0] != -100 && field.GetShots()[(int)(socket.y + 1)][(int)(socket.x - 1)] != 1)
            field.setShot((int) (socket.x - 1), (int) (socket.y + 1), (byte) 3);

        if(tmp[2][1] == 100)
        {
            if(field.GetShots()[(int)(socket.y + 1)][(int)socket.x] != 2 )
                field.setShot((int) (socket.x), (int) (socket.y + 1), (byte) 100);
        }
        else if(tmp[2][1] != -100 && field.GetShots()[(int)(socket.y + 1)][(int)socket.x] != 1)
            field.setShot((int) (socket.x), (int) (socket.y + 1), (byte) 3);
        if(tmp[2][2] == 100)
        {
            if(field.GetShots()[(int)(socket.y + 1)][(int)(socket.x + 1)] != 2 )
                field.setShot((int) (socket.x + 1), (int) (socket.y + 1), (byte) 100);
        }
        else if(tmp[2][2] != -100 && field.GetShots()[(int)(socket.y + 1)][(int)(socket.x + 1)] != 1)
            field.setShot((int) (socket.x + 1), (int) (socket.y + 1), (byte) 3);

        currentReload = reload;
    }

    @Override
    public boolean doYourFuckingJob(Vector2 vector2, Bullet bullet) {
        return true;
    }

    @Override
    public void doYourVileJob() {

    }
}
