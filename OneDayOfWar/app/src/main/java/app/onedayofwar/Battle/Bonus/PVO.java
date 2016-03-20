package app.onedayofwar.Battle.Bonus;

import android.graphics.Rect;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Bullet;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 27.03.2015.
 */
public class PVO extends Bonus
{
    Rect bulRect;
    public PVO(boolean isAvailable)
    {
        this.isAvailable = isAvailable;
        reload = 0;
        cost = 150;
        skill = 0;
        info = "Инженера нашей каолиции разработали противо-воздушные ракеты.\nПросто проведите пальцем по ракете, которая\nв этом ходе будет лететь на вас, и она взорвется!\nСтоимость: "+cost+ "\nВремя перезарядки: "+reload;
        bulRect = new Rect();
    }

    public boolean doYourFuckingJob(Vector2 touchPos, Bullet bullet)
    {
        currentReload = reload;
        bulRect.set((int)(bullet.getPos().x - bullet.getWidth()),(int)(bullet.getPos().y - bullet.getHeight()),(int)(bullet.getPos().x + bullet.getWidth()),(int)(bullet.getPos().y + bullet.getHeight()));
        return bulRect.intersect((int) (touchPos.x - 5), (int) (touchPos.y - 5), (int) (touchPos.x + 5), (int) (touchPos.y + 5));
    }

    @Override
    public void doYourVileJob() {

    }

    @Override
    public void doYourUglyJob(int[][] square, Field eField) {

    }
}
