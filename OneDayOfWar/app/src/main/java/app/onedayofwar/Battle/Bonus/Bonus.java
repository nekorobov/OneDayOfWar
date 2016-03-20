package app.onedayofwar.Battle.Bonus;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Bullet;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 26.03.2015.
 */
abstract public class Bonus
{
    public boolean isAvailable;     //Открыт ли бонус игроком
    public Sprite image;
    public int reload;
    public int currentReload;
    public int cost;
    public int skill;
    public String info;

    public void setAvailable(boolean available){isAvailable = available;}

    public boolean IsAvailable(){return isAvailable;}

    public boolean IsReloaded(){return currentReload == 0;}

    public void NextTurn(){ if(currentReload>0) currentReload--;}

    public abstract void doYourUglyJob(int[][] square, Field eField);

    public abstract boolean doYourFuckingJob(Vector2 touchPos, Bullet bullet);

    public abstract void doYourVileJob();
}

