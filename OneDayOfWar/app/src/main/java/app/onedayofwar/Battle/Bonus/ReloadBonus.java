package app.onedayofwar.Battle.Bonus;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Bullet;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 28.03.2015.
 */
public class ReloadBonus extends Bonus
{

    public ReloadBonus(boolean isAvailable)
    {
        this.isAvailable = isAvailable;
        cost = 50;
        reload = 5;
        skill = 1;
        info = "Мы раскрутим им все гайки на колесах!\nПерезарядка всех кораблей будет увеличена на "+skill+"\nСтоимость: "+cost+"\nВремя перезарядки: "+reload;
    }
    @Override
    public void doYourUglyJob(int[][] square, Field eField) {

    }

    @Override
    public boolean doYourFuckingJob(Vector2 touchPos, Bullet bullet) {
        return false;
    }

    @Override
    public void doYourVileJob() {
        currentReload = reload;
    }
}
