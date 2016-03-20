package app.onedayofwar.Battle.BattleElements;

import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 01.02.2015.
 */
public class BattleEnemy
{
    public static Vector2 target;
    public static byte weaponType;
    public static byte attacker;
    public static int damage;
    public static byte attackResult; //-1 - нет результата; 0 - попал в ту, которую уже стрелял; 1 - мимо; 2 - попал; 3 - взорвал
    public static String attackResultData;
    public static boolean isLose;
    public static boolean haveGround;
}
