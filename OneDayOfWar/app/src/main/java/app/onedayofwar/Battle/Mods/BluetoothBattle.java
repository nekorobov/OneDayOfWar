package app.onedayofwar.Battle.Mods;

import android.util.Log;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BluetoothConnection.HandlerMSG;
import app.onedayofwar.Battle.Bonus.ForBonusEnemy;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 06.02.2015.
 */
public class BluetoothBattle extends Battle
{
    private boolean isEnemyInstallationFinish;
    private boolean isResultSend;
    private boolean isBonusResultSend;
    public BluetoothBattle(BattleView battleView)
    {
        super(battleView);
        isEnemyInstallationFinish = false;
        isResultSend = false;
    }

    @Override
    public void InstallationFinish()
    {
        if(!isEnemyInstallationFinish)
        {
            battleView.btController.SendData(HandlerMSG.INSTALLATION_FINISH);
            if(battleView.btController.GetRecievedData().equals(HandlerMSG.INSTALLATION_FINISH))
            {
                isEnemyInstallationFinish = true;
                Log.i("TESTBT", "BattleEnemy installed");
            }
        }
        else
        {
            if(isYourTurn)
            {
                state = BattleState.AttackPrepare;
                battleView.AttackPrepare();
            }
            else
            {
                state = BattleState.Defence;
                battleView.DefendingPrepare();
            }

            battleView.MoveGates();
        }
    }

    @Override
    public boolean PrepareToGlare()
    {
        if (eField.selectedSocket.IsFalse() || eField.GetSelectedSocketInfo() == 0)
            return false;

        Vector2 tmp = new Vector2(eField.GetLocalSocketCoord(eField.selectedSocket));

        Log.i("SELECTED_SOCKET", "x: " + tmp.x + " y: " + tmp.y);
        battleView.btController.SendData(HandlerMSG.GLARE_MSG + '|' + (int)tmp.x + '|' + (int)tmp.y);
        return true;
    }

    @Override
    public boolean EnemyGlare()
    {
        if (ForBonusEnemy.socket.IsFalse())
            return false;

        if(isBonusResultSend)
            return true;

        String tmp = "";
        tmp += HandlerMSG.GLARE_RESULT_MSG;
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                tmp += "|" + field.getSquare()[i][j];
            }
        }
        battleView.btController.SendData(tmp);
        isBonusResultSend = true;
        return true;
    }

    @Override
    public void PlayerGlare()
    {
        glareBonus.doYourUglyJob(ForBonusEnemy.glareArr, eField);
        ForBonusEnemy.socket.SetFalse();
        isBonusResultSend = false;
    }

    @Override
    public void PVOInfoSend()
    {
        String tmp = "";
        tmp += HandlerMSG.PVO_INFO;
        battleView.btController.SendData(tmp);
    }

    @Override
    public void PVOInfoGet()
    {
        eField.GetShots()[(int)eField.selectedSocket.y][(int)eField.selectedSocket.x] = 101;
        ForBonusEnemy.pvoGet = false;
    }

    @Override
    public void PVOSendResult()
    {
        String tmp = "";
        tmp += HandlerMSG.PVO_RESULT;
        battleView.btController.SendData(tmp);
        field.explodeAnimation.setPosition((int) (bullet.getPos().x), (int) (bullet.getPos().y));
        field.explodeAnimation.setTexture(Assets.airExplosion, 49, 10);
        field.explodeAnimation.Start();
        bullet.Reload();
        preShoot = field.GetSelectedSocketInfo();
        Vector2 localCoord = field.GetLocalSocketCoord(BattleEnemy.target);
        field.setShot((int) localCoord.x, (int) localCoord.y, (byte) preShoot);
        BattleEnemy.target.SetFalse();
        battleView.AttackPrepare();
        state = BattleState.AttackPrepare;
        battleView.pvoStart = false;
        ForBonusEnemy.pvoSend = false;
        CheckPlayerArmy();
    }


    @Override
    public void LoadEnemy()
    {

    }

    @Override
    public boolean PreparePlayerShoot()
    {
        if (eField.selectedSocket.IsFalse() || eField.GetSelectedSocketInfo() == 0)
            return false;

        Vector2 tmp = new Vector2(eField.GetLocalSocketCoord(eField.selectedSocket));

        Log.i("SELECTED_SOCKET", "x: " + tmp.x + " y: " + tmp.y);

        battleView.btController.SendData(HandlerMSG.ATTACK + '|' + (int)tmp.x + '|' + (int)tmp.y + '|' + army.get(selectedUnitZone).GetPower() + '|' + 1);
        eField.GetFieldInfo()[(int)tmp.y][(int)tmp.x] = 0;
        state = BattleState.Shoot;
        return true;
    }

    @Override
    public void PlayerShoot()
    {
        if(BattleEnemy.attackResult != -1)
        {
            Log.i("PLAYER SHOOT", "ATK RSL " + BattleEnemy.attackResult);
            army.get(selectedUnitZone).Reload();
            army.get(selectedUnitZone).Deselect();
            if(BattleEnemy.attackResult == 3)
            {
                String[] tmp = BattleEnemy.attackResultData.split("\\.");
                Vector2 tmpCoord = new Vector2();
                for(int i = 0; i < tmp.length - 1; i+=2)
                {
                    tmpCoord.SetValue(Integer.parseInt(tmp[i]), Integer.parseInt(tmp[i + 1]));
                    eField.GetShots()[(int)tmpCoord.y][(int)tmpCoord.x] = 2;
                }
            }
            else
            {
                eField.SetShot(BattleEnemy.attackResult == 2);
            }

            selectedUnitZone = -1;
            BattleEnemy.attackResult = -1;
            battleView.MoveGates();
        }
    }

    @Override
    public boolean PrepareEnemyShoot()
    {
        if (BattleEnemy.target.IsFalse())
            return false;

        if(isResultSend)
            return true;

        BattleEnemy.target.SetValue(field.GetGlobalSocketCoord(BattleEnemy.target));
        field.selectedSocket.SetValue(BattleEnemy.target);
        SendEnemyResult();
        isResultSend = true;
        return true;
    }

    @Override
    public void SendEnemyResult()
    {
        byte target = field.GetSelectedSocketInfo();
        int result;
        String resultData = "";

        if (field.GetSelectedSocketShot() != 0)
        {
            BattleEnemy.target.SetFalse();
            result = 0;
        }
        else
        {
            if(target >= 0)
            {
                if(army.get(target).SetDamage(BattleEnemy.damage))
                {
                    result = 3;
                    Vector2 tmp = new Vector2();
                    for (int i = 0; i < army.get(target).GetForm().length; i++)
                    {
                        tmp.SetValue(field.GetLocalSocketCoord(army.get(target).GetForm()[i]));
                        resultData += "" + (int)tmp.x + '.' + (int)tmp.y + '.';
                    }
                    resultData = resultData.substring(0, resultData.length() - 1);
                }
                else
                {
                    result = 2;
                }
            }
            else
            {
                result = 1;
            }
            if(battleView.pvoStart)
                result = 101;
        }
        battleView.btController.SendData(HandlerMSG.ATTACK_RESULT + '|' + result + '|' + resultData);
        Log.i("ENEMY", "SEND_RESULT");
    }

    @Override
    public void GetReloadInfo()
    {
        for(int i = 0; i < army.size(); i++)
        {
            army.get(i).IncreaseReload(ForBonusEnemy.skill);
        }
    }

    @Override
    public void SendReloadInfo()
    {
        battleView.btController.SendData(HandlerMSG.RELOAD_RESULT + "|" + reloadBonus.skill);
    }

    @Override
    public void EnemyShoot()
    {
        isResultSend = false;
        byte target = field.GetSelectedSocketInfo();
        if(target >= 0)
        {
            army.get(target).UpdateDamagedZones(field.selectedSocket);
            if(army.get(target).IsDead())
            {
                Vector2 tmp = new Vector2();
                for (int i = 0; i < army.get(target).GetForm().length; i++)
                {
                    tmp.SetValue(field.GetLocalSocketCoord(army.get(target).GetForm()[i]));
                    field.GetShots()[(int)tmp.y][(int)tmp.x] = 2;
                }
            }
            else
            {
                field.SetShot(true);
            }
        }
        else
        {
            field.SetShot(false);
        }
        CheckPlayerArmy();
    }

    public void CheckPlayerArmy()
    {
        //region army
        boolean isGood = false;
        boolean isGameOver = true;
        for(Unit unit : army)
        {
            if(!unit.IsDead())
            {
                unit.NextTurn();
                isGameOver = false;
                if (!unit.IsReloading())
                    isGood = true;
            }
        }
        if(isGameOver)
        {
            testLocalView = "YOU LOSE!";
            state = BattleState.Lose;
            battleView.btController.SendData(HandlerMSG.LOSE);
            GameOver();
            return;
        }
        else if(!isGood)
        {
            for(Unit unit : army)
            {
                if(!unit.IsDead())
                {
                    unit.ResetReload();
                    break;
                }
            }
        }
        //endregion
    }
}
