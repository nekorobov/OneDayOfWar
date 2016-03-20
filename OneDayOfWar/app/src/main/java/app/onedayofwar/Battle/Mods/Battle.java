package app.onedayofwar.Battle.Mods;


import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Bonus.Bonus;
import app.onedayofwar.Battle.Bonus.ForBonusEnemy;
import app.onedayofwar.Battle.Bonus.GlareBonus;
import app.onedayofwar.Battle.Bonus.PVO;
import app.onedayofwar.Battle.Bonus.ReloadBonus;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Battle.Units.Bullet;
import app.onedayofwar.Battle.Units.Ground.Engineer;
import app.onedayofwar.Battle.Units.Ground.IFV;
import app.onedayofwar.Battle.Units.Ground.Robot;
import app.onedayofwar.Battle.Units.Ground.SONDER;
import app.onedayofwar.Battle.Units.Ground.Tank;
import app.onedayofwar.Battle.Units.Ground.Turret;
import app.onedayofwar.Battle.Units.Space.Akira;
import app.onedayofwar.Battle.Units.Space.Battleship;
import app.onedayofwar.Battle.Units.Space.Bioship;
import app.onedayofwar.Battle.Units.Space.BirdOfPrey;
import app.onedayofwar.Battle.Units.Space.Defaint;
import app.onedayofwar.Battle.Units.Space.R2D2;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 24.12.2014.
 */
public abstract class Battle
{
    //region Variables
    public static enum BattleState { Installation, Defence, Attack, AttackPrepare, Shoot, Win, Lose}
    public static BattleState state;
    public Field field;
    public Field eField;
    protected BattleView battleView;
    public boolean isYourTurn;
    public ArrayList<Unit> army;
    public Unit[] drawArmySequence;
    protected int turns;

    //region Unit Installation Variables
    private byte unitNum[];
    protected byte[] unitCount;
    protected byte selectedUnitZone;
    //endregion

    public int preShoot;
    public Bullet bullet;
    //endregion

    protected String testLocalView = "";

    //region Bonuses
    public Bonus glareBonus;
    public Bonus pvo;
    public Bonus reloadBonus;
    public boolean isEnemyShotPrepeared;
    //endregion

    //region Constructor
    protected Battle(BattleView battleView)
    {
        this.battleView = battleView;
        Initialize();
    }
    //endregion

    //region Abstract Methods
    abstract public void LoadEnemy();
    abstract public boolean PreparePlayerShoot();
    abstract public void PlayerShoot();
    abstract public boolean PrepareEnemyShoot();
    abstract public void EnemyShoot();
    abstract public void InstallationFinish();
    abstract public boolean PrepareToGlare();
    abstract public boolean EnemyGlare();
    abstract public void PlayerGlare();
    abstract public void PVOInfoSend();
    abstract public void PVOInfoGet();
    abstract public void PVOSendResult();
    abstract public void SendEnemyResult();
    abstract public void GetReloadInfo();
    abstract public void SendReloadInfo();
    //endregion

    //region Initialization
    public void Initialize()
    {
        state = BattleState.Installation;
        BattleEnemy.target = new Vector2();
        BattleEnemy.target.SetFalse();
        BattleEnemy.weaponType = 0;
        BattleEnemy.isLose = false;
        BattleEnemy.damage = 0;
        BattleEnemy.attackResult = -1;
        BattleEnemy.haveGround = false;
        ForBonusEnemy.glareArr = new int[3][3];
        ForBonusEnemy.socket = new Vector2();
        ForBonusEnemy.canITakeResult = false;
        ForBonusEnemy.canISendResult = false;
        ForBonusEnemy.pvoGet = false;
        ForBonusEnemy.pvoGet = false;
        ForBonusEnemy.reloadGet = false;
        ForBonusEnemy.skill = 0;
        turns = 0;
        army = new ArrayList<>();

        field = new Field(battleView.screenWidth/2, battleView.screenHeight/2, BattlePlayer.fieldSize, true);
        eField = new Field((int)(405 * Assets.monitorWidthCoeff + Assets.grid.getWidth() * Assets.gridCoeff), battleView.screenHeight/2, BattlePlayer.fieldSize, false); //gameView.screenWidth/2 - Assets.grid.getIconWidth()/2, gameView.screenHeight/2 - Assets.grid.getIconHeight()/2, 15, false);
        eField.Move();
        BattlePlayer.fieldSize = 0;
        glareBonus = new GlareBonus(true);
        pvo = new PVO(true);
        reloadBonus = new ReloadBonus(true);

        selectedUnitZone = -1;

        /*unitCount = new byte[6];
        unitCount[0] = 1;//6;//Robot
        unitCount[1] = 1;//4;//IFV
        unitCount[2] = 1;//3;//Engineer
        unitCount[3] = 3;//2;//Tank
        unitCount[4] = 1;//2;//Turret
        unitCount[5] = 1;//1;//SONDER*/
        unitCount = BattlePlayer.unitCount.clone();
        BattlePlayer.unitCount = null;

        unitNum = new byte[unitCount.length];

        byte tmp = 0;
        for(int i = 0; i < unitNum.length; i++)
        {
            if(unitCount[i] != 0)
            {
                unitNum[i] = tmp;
                tmp += unitCount[i];
            }
            else
                unitNum[i] = -1;
        }
        Vector2 startPos = new Vector2();
        int offset = 0;

        if(BattlePlayer.armyType == "Ground")

            for(int j = 0; j < unitCount.length; j++)
            {
                switch(j)
                {
                    case 0:
                        if (unitCount[0] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[0]; i++)
                                army.add(new Robot(startPos, 0, true));

                            offset = (int)army.get(unitNum[0]).GetIconPosition().bottom;
                        }
                        break;
                    case 1:
                        if (unitCount[1] != 0)
                        {

                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[1]; i++)
                                army.add(new IFV(startPos, 1, true));

                            offset = (int)army.get(unitNum[1]).GetIconPosition().bottom;
                        }
                        break;
                    case 2:
                        if (unitCount[2] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10  + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[2]; i++)
                                army.add(new Engineer(startPos, 2, true));

                            offset = (int)army.get(unitNum[2]).GetIconPosition().bottom;
                        }
                        break;
                    case 3:
                        if (unitCount[3] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[3]; i++)
                                army.add(new Tank(startPos, 3, true));

                            offset = (int)army.get(unitNum[3]).GetIconPosition().bottom;
                        }
                        break;
                    case 4:
                        if (unitCount[4] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[4]; i++)
                                army.add(new Turret(startPos, 4, true));

                            offset = (int)army.get(unitNum[4]).GetIconPosition().bottom;
                        }
                        break;
                    case 5:
                        if (unitCount[5] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[5]; i++)
                                army.add(new SONDER(startPos, 5, true));
                        }
                        break;

                }
            }
        else if(BattlePlayer.armyType == "Space")
            for(int j = 0; j < unitCount.length; j++)
            {
                switch(j)
                {
                    case 0:
                        if (unitCount[0] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[0]; i++)
                                army.add(new R2D2(startPos, 0, true));

                            offset = (int)army.get(unitNum[0]).GetIconPosition().bottom;
                        }
                        break;
                    case 1:
                        if (unitCount[1] != 0)
                        {

                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[1]; i++)
                                army.add(new Akira(startPos, 1, true));

                            offset = (int)army.get(unitNum[1]).GetIconPosition().bottom;
                        }
                        break;
                    case 2:
                        if (unitCount[2] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10  + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[2]; i++)
                                army.add(new Defaint(startPos, 2, true));

                            offset = (int)army.get(unitNum[2]).GetIconPosition().bottom;
                        }
                        break;
                    case 3:
                        if (unitCount[3] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[3]; i++)
                                army.add(new Battleship(startPos, 3, true));

                            offset = (int)army.get(unitNum[3]).GetIconPosition().bottom;
                        }
                        break;
                    case 4:
                        if (unitCount[4] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[4]; i++)
                                army.add(new Bioship(startPos, 4, true));

                            offset = (int)army.get(unitNum[4]).GetIconPosition().bottom;
                        }
                        break;
                    case 5:
                        if (unitCount[5] != 0)
                        {
                            startPos.SetValue(battleView.selectingPanel.matrix[12], offset + 10 + (int)(Assets.iconCoeff * Assets.sonderIcon.getHeight()/2));
                            for (int i = 0; i < unitCount[5]; i++)
                                army.add(new BirdOfPrey(startPos, 5, true));
                        }
                        break;

                }
            }

        drawArmySequence = new Unit[army.size()];
        for(int i = 0; i < army.size(); i++)
        {
            drawArmySequence[i] = army.get(i);
        }
        bullet = new Bullet();
        LoadEnemy();
    }
    //endregion

    //region Update
    public void Update(float eTime)
    {
        if(BattleEnemy.isLose && battleView.btController != null)
        {
            state = BattleState.Win;
            GameOver();
            return;
        }

        if(state != BattleState.Installation)
        {
            field.UpdateAnimation(eTime);
            for (int i = 0; i < army.size(); i++)
            {
                army.get(i).UpdateAnimation(eTime);
            }
        }

        /*if(state == BattleState.Installation)
            AlignArmyPosition(eTime);*/

        //Если шторы закрыты
        if(battleView.IsGatesClose())
        {
            //Если идет установка
            if(state == BattleState.Installation)
            {
                InstallationFinish();
            }
            //Если идет подготовка к атаке
            else if(state == BattleState.AttackPrepare)
            {
                SwapFields();
                army.get(selectedUnitZone).Deselect();
                state = BattleState.Attack;
                battleView.ShootingPrepare();
                battleView.MoveGates();
            }
            //Если происходит выстрел
            else if(state == BattleState.Shoot)
            {
                SwapFields();
                state = BattleState.Defence;
                battleView.DefendingPrepare();
                battleView.MoveGates();
            }
        }
        else if(battleView.IsGatesOpen())
        {
            //Если идет защита
            if(state == BattleState.Defence)
            {
                if(PrepareEnemyShoot())
                {
                    if(!BattleEnemy.target.IsFalse())
                    {
                        switch (bullet.state)
                        {
                            case LAUNCH:
                                bullet.Launch(BattleEnemy.target.x, BattleEnemy.target.y, BattleEnemy.weaponType);
                                break;
                            case FLY:
                                if (!battleView.pvoStart)
                                    bullet.Update(eTime);
                                else
                                {
                                    if (ForBonusEnemy.pvoSend)
                                        PVOSendResult();
                                    else if (ForBonusEnemy.pvoGet)
                                        PVOInfoGet();
                                }
                                break;
                            case BOOM:
                                field.explodeAnimation.setPosition((int) (BattleEnemy.target.x), (int) (BattleEnemy.target.y - 25 * Assets.isoGridCoeff));
                                field.explodeAnimation.setTexture(Assets.explosion, 24, 100);
                                field.explodeAnimation.Start();
                                bullet.Reload();
                                BattleEnemy.target.SetFalse();
                                EnemyShoot();

                                Log.i("BATTLE", "ENEMY SHOOT");
                                battleView.AttackPrepare();
                                state = BattleState.AttackPrepare;
                                isEnemyShotPrepeared = false;
                                break;
                        }
                    }
                }
            }
            else if(state == BattleState.Shoot)
            {
                PlayerShoot();
            }
            //Готовимся к ответу на запрос по бонусу засвета
            if(ForBonusEnemy.canISendResult)
            {
                EnemyGlare();
                ForBonusEnemy.canISendResult = false;
            }
            //Принимаем и засвечиваем нужную часть
            if(ForBonusEnemy.canITakeResult)
            {
                PlayerGlare();
                ForBonusEnemy.canITakeResult = false;
            }
            //Принимает информацию о том, что нужна перезарядка
            if(ForBonusEnemy.reloadGet)
            {
                GetReloadInfo();
                ForBonusEnemy.reloadGet = false;
            }
        }
    }
    //endregion

    //region OnTouch
    public void OnTouch(MotionEvent event)
    {
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Если идет расстановка
            if(state == BattleState.Installation)
            {
                //Пытаемся выбрать юнит
                SelectUnit();
            }
            //Если ворота открыты и идет подготовка к атаке
            else if(battleView.IsGatesOpen() && state == BattleState.AttackPrepare)
            {
                //Выделяем клетку поля
                field.SelectSocket(battleView.touchPos, 0);
                //Выбираем юнит
                SelectUnit();
            }
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
        }
        //Если ворота открыты и идет атака
        if(battleView.IsGatesOpen() && state == BattleState.Attack)
        {
            //Выделяем клетку вражеского поля
            eField.SelectSocket(battleView.touchPos, 0);
        }
        //Если выбран юнит и расстановка не закончена
        if (selectedUnitZone > -1 && state == BattleState.Installation)
        {
            //Пытаемся передвигать юнит
            MoveSelectedUnit();
        }
    }
    //endregion

    //region Unit Installation
    public void TurnUnit()
    {
        if (selectedUnitZone > -1)
        {
            //Поворачиваем юнит
            army.get(unitNum[selectedUnitZone]).ChangeDirection();
            //Проверяем помехи
            army.get(unitNum[selectedUnitZone]).CheckPosition(field);
        }
    }

    public boolean IsUnitSelected()
    {
        return selectedUnitZone > -1;
    }

    public boolean CancelSelection()
    {
        if (selectedUnitZone > -1)
        {
            //Обнуляем позицию выбранного юнита
            army.get(unitNum[selectedUnitZone]).ResetPosition();
            //Обнуляем выделенный сокет поля
            field.selectedSocket.SetFalse();
            selectedUnitZone = -1;
            return true;
        }
        return false;
    }

    public boolean CheckInstallationFinish()
    {
        byte c = 0;
        for (int i = 0; i < unitNum.length; i++)
        {
            if (unitNum[i] == -1)
                c++;
        }
        if (c == unitNum.length)
        {
            return true;
        }
        return false;
    }

    public void updateDrawArmySequence()
    {
        for(int i = 0; i < drawArmySequence.length; i++)
        {
            for(int j = 0; j < drawArmySequence.length - 1 - i; j++)
            {
                if(drawArmySequence[j].GetForm()[0].y > drawArmySequence[j + 1].GetForm()[0].y)
                {
                    Unit tmp = drawArmySequence[j];
                    drawArmySequence[j] = drawArmySequence[j+1];
                    drawArmySequence[j+1] = tmp;
                }
            }
        }
    }
    /**
     * Передвигает выбраный юнит
     */
    public void MoveSelectedUnit()
    {
        //Если касание было не по кнопкам
        if (!battleView.isButtonPressed && (battleView.touchPos.x < battleView.selectingPanel.matrix[12] - battleView.selectingPanel.width/2 - 5))
        {
            //Если юнит выбран
            if (selectedUnitZone > -1)
            {
                //Вектор касания смещаем на определенную величину, для удобства
                Vector2 tmp = new Vector2(battleView.touchPos.x - army.get(unitNum[selectedUnitZone]).GetIconPosition().width() - 50 - army.get(unitNum[selectedUnitZone]).offset.x, battleView.touchPos.y - army.get(unitNum[selectedUnitZone]).GetIconPosition().height()/2);

                //Если касанемся в пределах поля
                if(field.IsVectorInField(tmp))
                {
                    //Выделяем ячейку на поле
                    field.SelectSocket(tmp, 0);

                    //Перемещаем юнит по ячейкам
                    army.get(unitNum[selectedUnitZone]).SetPosition(field.selectedSocket);

                    //Проверяем помехи
                    army.get(unitNum[selectedUnitZone]).CheckPosition(field);
                }
            }
        }
    }

    /**
     * Выбор юнита
     */
    public void SelectUnit()
    {
        if (state == BattleState.AttackPrepare)
        {
            if(field.selectedSocket.IsFalse())
                return;
            byte tmpID = field.GetSelectedSocketInfo();
            //Если в клетке стоит юнит
            if (tmpID > -1)
            {
                if (!army.get(tmpID).IsDead() && !army.get(tmpID).IsReloading())
                {
                    if (selectedUnitZone > -1)
                        army.get(selectedUnitZone).Deselect();
                    selectedUnitZone = tmpID;
                    army.get(tmpID).Select();
                }
            }
            else
            {
                RectF touchRect = new RectF(battleView.touchPos.x - 3, battleView.touchPos.y - 3, battleView.touchPos.x + 3, battleView.touchPos.y + 3);
                for(byte i = 0; i < army.size(); i++)
                {
                    if (touchRect.intersect(army.get(i).GetBounds()))
                    {
                        if (!army.get(i).IsDead() && !army.get(i).IsReloading())
                        {
                            if (selectedUnitZone > -1)
                                army.get(selectedUnitZone).Deselect();
                            selectedUnitZone = i;
                            army.get(i).Select();
                        }
                        break;
                    }
                }
            }
        }
        else if(state == BattleState.Installation)
        {
            battleView.isButtonPressed = false;
            //Если юнит не выбран
            if (selectedUnitZone < 0)
            {
                //Получаем прямоугольник касания
                RectF touchRect = new RectF(battleView.touchPos.x - 3, battleView.touchPos.y - 3, battleView.touchPos.x + 3, battleView.touchPos.y + 3);
                //Пробегаем по всем текущим идам разных типов кораблей
                for (byte i = 0; i < unitNum.length; i++)
                {
                    //Если остались не выбранные корабли определенного типа и прямоугольник касания пересекает прямоугольник стартовой зоны кораблей этого типа
                    if (battleView.selectingPanel.isClose && unitNum[i] > -1 && touchRect.intersect(army.get(unitNum[i]).GetIconPosition()))
                    {
                        //Выделенному типу присваиваем ид этой зоны
                        selectedUnitZone = i;
                        //Перемещаем в конец, чтоб перекрывал остальные юниты
                        for(byte u = 0; u < drawArmySequence.length; u++)
                        {
                            if(army.get(unitNum[i]).equals(drawArmySequence[u]))
                            {
                                Unit tmpUnit;
                                for(int n = u; n < drawArmySequence.length - 1; n++)
                                {
                                    tmpUnit = drawArmySequence[n];
                                    drawArmySequence[n] = drawArmySequence[n + 1];
                                    drawArmySequence[n + 1] = tmpUnit;
                                }
                                break;
                            }
                        }
                        //Задвигаем панель выбора юнитов
                        battleView.selectingPanel.Move();
                        //Выделяем ячейку на поле
                        field.selectedSocket.SetValue(field.GetGlobalSocketCoord(field.size / 2, field.size / 2));
                        //Устанавливаем позицию по центру поля
                        army.get(unitNum[selectedUnitZone]).SetPosition(field.selectedSocket);
                        //Подсвечиваем юнит
                        army.get(unitNum[selectedUnitZone]).Select();
                        //Проверяем помехи
                        army.get(unitNum[selectedUnitZone]).CheckPosition(field);
                        //Пока текущий ид выделенного типа указывает на установленный юнит
                        while (army.get(unitNum[selectedUnitZone]).isInstalled)
                            //Увеличиваем текущий ид
                            unitNum[selectedUnitZone]++;

                        break;
                    }
                }
                //Если юнит так и не выбран
                if (selectedUnitZone < 0)
                {
                    //Если касание было в пределах поля
                    if (field.IsVectorInField(battleView.touchPos) && battleView.touchPos.x < battleView.selectingPanel.matrix[12] - battleView.selectingPanel.width/2 - 5)
                    {
                        //Выделяем клетку поля
                        field.SelectSocket(battleView.touchPos, 0);
                        //Получаем локальные координаты клетки поля
                        Vector2 tmp = field.GetLocalSocketCoord(field.selectedSocket);
                        //Получаем инфу клетки поля
                        byte tmpID = field.GetFieldInfo()[(int)tmp.y][(int)tmp.x];
                        //Если в клетке стоит юнит
                        if (tmpID > -1)
                        {
                            //Если меню выбора закрыто
                            if (!battleView.selectingPanel.isClose)
                                //Открываем меню выбора
                                battleView.selectingPanel.Move();
                            //Удаляем информацию о нем с поля
                            field.DeleteUnit(army.get(tmpID));
                            //Удаляем выделение
                            army.get(tmpID).Deselect();
                            //Обнуляем его позицию
                            army.get(tmpID).ResetPosition();
                            //Помечаем его как не установленный
                            army.get(tmpID).isInstalled = false;
                            //Обновляем границы выделения
                            army.get(tmpID).UpdateBounds();
                            //Выравниваем иконку
                            army.get(tmpID).getIconMatrix()[12] = battleView.selectingPanel.matrix[12];
                            //Если его ид меньше текущего ида кораблей определенного типа или установлены все корабли данного типа
                            if (tmpID < unitNum[army.get(tmpID).GetZone()] || unitNum[army.get(tmpID).GetZone()] == -1)
                                //записываем в текущий ид кораблей определенного типа значение ида юнита
                                unitNum[army.get(tmpID).GetZone()] = tmpID;
                        }
                        else
                        {
                            for(byte i = 0; i < army.size(); i++)
                            {
                                //Проверяем на касание в границах выделения юнитов
                                if (touchRect.intersect(army.get(i).GetBounds()))
                                {
                                    //Если меню выбора закрыто
                                    if (!battleView.selectingPanel.isClose)
                                        //Открываем меню выбора
                                        battleView.selectingPanel.Move();
                                    //Удаляем информацию о нем с поля
                                    field.DeleteUnit(army.get(i));
                                    //Удаляем выделение
                                    army.get(i).Deselect();
                                    //Обнуляем его позицию
                                    army.get(i).ResetPosition();
                                    //Помечаем его как не установленный
                                    army.get(i).isInstalled = false;
                                    //Обновляем границы выделения
                                    army.get(i).UpdateBounds();
                                    //Выравниваем иконку
                                    army.get(i).getIconMatrix()[12] = battleView.selectingPanel.matrix[12];
                                    //Если его ид меньше текущего ида кораблей определенного типа или установлены все корабли данного типа
                                    if (i < unitNum[army.get(i).GetZone()] || unitNum[army.get(i).GetZone()] == -1)
                                        //записываем в текущий ид кораблей определенного типа значение ида юнита
                                        unitNum[army.get(i).GetZone()] = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void AlignArmyPosition(float eTime)
    {
        if(state != BattleState.Installation)
        {
            for (Unit unit : army)
            {
                if (unit.getMatrix()[12] < 0)
                    unit.getMatrix()[12] += battleView.screenWidth;
                else
                    unit.getMatrix()[12] -= battleView.screenWidth;
            }
        }
        else
        {
            for (int i = 0; i < unitCount.length; i++)
            {
                if (unitNum[i] != -1)
                {
                    army.get(unitNum[i]).getIconMatrix()[12] = battleView.selectingPanel.matrix[12];
                }
            }
        }
    }

    public void SwapFields()
    {
        field.Move();
        eField.Move();
        AlignArmyPosition(0);
    }

    /**
     * Устанавливает юнит на поле
     */
    public void InstallUnit()
    {
        //Если выбран юнит
        if(selectedUnitZone > -1)
        {
            //Если выделена ячейка на поле
            if (!field.selectedSocket.IsFalse())
            {
                //Выравниваем позицию юнита по выделеной ячейке
                army.get(unitNum[selectedUnitZone]).SetPosition(field.selectedSocket);

                //Если юнит не выходит за границы поля
                if (army.get(unitNum[selectedUnitZone]).SetForm(field.selectedSocket, field, true))
                {
                    //Помещаем юнит на поле
                    field.PlaceUnit(army.get(unitNum[selectedUnitZone]).GetForm(), unitNum[selectedUnitZone]);

                    //Обнуляем выделенную ячейку поля
                    field.selectedSocket.SetFalse();

                    //Помечаем юнит как установленный
                    army.get(unitNum[selectedUnitZone]).isInstalled = true;

                    army.get(unitNum[selectedUnitZone]).UpdateBounds();

                    army.get(unitNum[selectedUnitZone]).Deselect();

                    //Увеличиваем текущий ид данного типа юнитов
                    unitNum[selectedUnitZone]++;

                    byte startNum = 0;
                    //Расчитываем начальный ид юнитов данного типа
                    for (int i = 0; i < selectedUnitZone; i++)
                        startNum += unitCount[i];

                    //Если установлены все юниты
                    byte installCount = 0;
                    for(int i = startNum; i < startNum + unitCount[selectedUnitZone]; i++)
                    {
                        if(army.get(i).isInstalled)
                            installCount++;
                    }

                    if (installCount == unitCount[selectedUnitZone])
                        //Помечаем тип как установленный
                        unitNum[selectedUnitZone] = -1;
                    //else
                        //army.get(unitNum[selectedUnitZone]).iconMatrix[12] = army.get(unitNum[selectedUnitZone] - 1).iconMatrix[12];

                    updateDrawArmySequence();

                    if(!battleView.selectingPanel.isClose)
                        battleView.selectingPanel.Move();
                    //Обнуляем выделенный тип юнитов
                    selectedUnitZone = -1;
                }
            }
        }
    }
    //endregion

    public void DrawUnits(Graphics graphics)
    {
        boolean isExplodeShowed = false;
        for(Unit unit : drawArmySequence)
        {
            if(unit.getMatrix()[13] > 0)
            {
                if(field.explodeAnimation.IsStart() && !isExplodeShowed)
                {
                    Vector2 tmp = new Vector2();
                    fin:for(int i = 0; i < unit.GetForm().length; i++)
                    {
                        tmp.SetValue(unit.GetForm()[i].x - field.socketSizeX, unit.GetForm()[i].y - field.socketSizeY);
                        byte sign = 1;
                        for(int j = 0; j < 3; j++)
                        {

                            tmp.x += field.socketSizeX/2;
                            tmp.y += sign * field.socketSizeY/2;
                            sign = (byte)-sign;
                            if(tmp.Equals(BattleEnemy.target))
                            {
                                boolean isOnForm = false;
                                for(int k = 0; k < unit.GetForm().length; k++)
                                {
                                    if(tmp.Equals(unit.GetForm()[k]))
                                    {
                                        isOnForm = true;
                                        break;
                                    }
                                }

                                if(!isOnForm)
                                {
                                    graphics.DrawAnimation(field.explodeAnimation);
                                    isExplodeShowed = true;
                                    break fin;
                                }
                            }
                        }
                    }
                }
                unit.Draw(graphics);
            }
        }
        if(state != BattleState.Installation && field.getMatrix()[12] >= 0)
        {
            //battleView.graphics.drawText(testLocalView, 24, 50, 150, army.get(0).strokePaint.getColor());
        }
        bullet.Draw(graphics);
        if(field.explodeAnimation.IsStart() && !isExplodeShowed)
        {
            graphics.DrawAnimation(field.explodeAnimation);
        }
    }

    public void DrawFields(Graphics graphics)
    {
        if(state != BattleState.Installation)
        {
            if (eField.getMatrix()[12] >= 0)
                eField.Draw(graphics);
            else
                field.DrawFieldInfo(graphics);
        }
        else
        {
            field.Draw(graphics);
        }
    }

    public void DrawUnitsIcons(Graphics graphics)
    {
        for(int i = 0; i < unitCount.length; i++)
        {
            if(unitNum[i] != -1)
                army.get(unitNum[i]).DrawIcon(graphics);
        }
    }

    public void GameOver()
    {
        byte playerShots[][] = eField.GetShots();
        byte goodShots = 0;
        int reward = 0;

        for(int i = 0 ; i < playerShots.length; i++)
        {
            for (int j = 0; j < playerShots[i].length; j++)
            {
                if(playerShots[i][j] == 2)
                {
                    goodShots++;
                }
            }
        }

        reward += goodShots * 5;
        if(state == BattleState.Win)
        {
            reward += 100;
        }

        battleView.GameOver(state, reward);
    }
    //endregion
}
