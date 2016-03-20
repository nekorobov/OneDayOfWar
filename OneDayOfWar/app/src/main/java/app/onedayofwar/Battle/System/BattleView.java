package app.onedayofwar.Battle.System;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Activities.BluetoothActivity;
import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Battle.Bonus.ForBonusEnemy;
import app.onedayofwar.Battle.Mods.Battle;
import app.onedayofwar.Battle.Mods.Battle.BattleState;
import app.onedayofwar.Battle.Mods.BluetoothBattle;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.UI.Button;
import app.onedayofwar.UI.Panel;
import app.onedayofwar.UI.Panel.Type;

public class BattleView implements ScreenView
{
    //region Variables
    public BluetoothController btController;
    public int screenWidth;
    public int screenHeight;
    private GLView glView;
    //private Activity activity;


    public Vector2 touchPos;
    public Vector2 bonusInfoPos;
    private Battle battle;

    public Panel selectingPanel;
    public Panel gateUp;
    public Panel gateDown;
    public Panel bonusPanel;
    public Panel infoBonusPanel;

    //region Buttons Variables
    private Button cancelBtn;
    private Button turnBtn;
    private Button installBtn;
    private Button installationFinishBtn;
    private Button shootBtn;
    private Button flagBtn;
    private Button glareBtn;
    private Button sugBonusBtn;
    private Button pvoBtn;
    private Button reloadBtn;
    private Button cancelBonusBtn;
    private Button infoBonusBtn;
    public boolean isButtonPressed;
    //endregion

    //region Bonus boolean
    private boolean glare;
    private boolean pvo;
    private boolean reloadBonus;
    //endregion

    private Sprite background;
    public boolean pvoStart;
    private String textBonuses;

    private float[] bgMatrix;

    char typeOfGame;
    private boolean isYourTurn;
    public Planet planet;
    private boolean ground;

    //endregion

    //region Constructor
    public BattleView(GLView glView, Planet planet, char typeOfGame, boolean isYourTurn)
    {
        this.glView = glView;
        this.isYourTurn = isYourTurn;
        this.typeOfGame = typeOfGame;
        this.planet = planet;
        screenWidth = glView.getScreenWidth();
        screenHeight = glView.getScreenHeight();
        btController = BluetoothActivity.btController;
        bgMatrix = new float[16];
        Matrix.setIdentityM(bgMatrix, 0);
        Matrix.translateM(bgMatrix, 0, screenWidth/2, screenHeight/2, 0);
        Log.i("BATTLE", "" + isYourTurn);
    }
    //endregion

    @Override
    public void Resume()
    {
        glView.getActivity().gameState = MainActivity.GameState.BATTLE;
    }

    public void Initialize(Graphics graphics)
    {
        if(BattlePlayer.armyType == "Ground")
            ground = true;
        else if(BattlePlayer.armyType == "Space")
            ground = false;

        if(ground)
            background = new Sprite(Assets.groundBackground);
        else
            background = new Sprite(Assets.spaceBackground);

        Log.i("BATTLE", "INITIALIZE");
        background.setPosition(screenWidth/2 ,screenHeight /2);
        background.Scale((float)Assets.bgWidthCoeff, (float)Assets.bgHeightCoeff);

        selectingPanel = new Panel(screenWidth - screenWidth/8, screenHeight/2, screenWidth/4, screenHeight, Type.RIGHT);

        gateUp = new Panel(screenWidth/2, screenHeight/4, screenWidth, screenHeight/2, Type.UP);
        gateDown = new Panel(screenWidth/2, screenHeight - screenHeight/4, screenWidth, screenHeight/2, Type.DOWN);

        bonusPanel = new Panel(7*screenWidth/8,screenHeight/2,screenWidth/4,screenHeight,Type.RIGHT);
        infoBonusPanel = new Panel(screenWidth/2, screenHeight/8, screenWidth, screenHeight/4, Type.UP);


        Matrix.scaleM(bgMatrix, 0, (float)Assets.bgWidthCoeff, -(float)Assets.bgHeightCoeff, 1);

        touchPos = new Vector2();

        isButtonPressed = false;

        switch(typeOfGame)
        {
            case 's':
            case 'c':
                battle = new SingleBattle(this);
                break;
            case 'b':
                battle = new BluetoothBattle(this);
                break;
        }
        battle.isYourTurn = isYourTurn;

        pvo = false;
        glare = false;
        reloadBonus = false;
        pvoStart = false;
        bonusInfoPos = new Vector2();
        textBonuses = "";
        bonusPanel.Move();
        infoBonusPanel.Move();
        ButtonsInitialize();
        MoveGates();
    }
    //endregion

    //region Update
    public void Update(float eTime)
    {
        if(ForBonusEnemy.pvoGet || ForBonusEnemy.pvoSend)
            pvoStart = battle.pvo.doYourFuckingJob(touchPos, battle.bullet);

        if(battle.state == BattleState.Installation && !selectingPanel.isStop)
        {
            selectingPanel.Update(eTime);
            battle.AlignArmyPosition(eTime);
        }

        battle.Update(eTime);

        if (!gateUp.isStop)
        {
            gateUp.Update(eTime);
            gateDown.Update(eTime);
        }
        if(battle.state == BattleState.Attack)
        {
            bonusPanel.Update(eTime);
            infoBonusPanel.Update(eTime);
            bonusInfoPos.SetValue(0, infoBonusPanel.matrix[13] - screenHeight/8);
            glareBtn.getMatrix()[12] = bonusPanel.matrix[12];
            pvoBtn.getMatrix()[12] = bonusPanel.matrix[12];
            reloadBtn.getMatrix()[12] = bonusPanel.matrix[12];
        }
    }

    public void MoveGates()
    {
        gateDown.Move();
        gateUp.Move();
    }

    public boolean IsGatesClose()
    {
        return gateUp.isClose && gateUp.isStop;
    }

    public boolean IsGatesOpen()
    {
        return !gateUp.isClose && gateUp.isStop;
    }

    public void ShootingPrepare()
    {
        shootBtn.SetVisible();
        shootBtn.Unlock();
        flagBtn.SetVisible();
        flagBtn.Unlock();
        installBtn.Lock();
        installBtn.SetInvisible();
        pvoBtn.Unlock();
        pvoBtn.SetVisible();
        glareBtn.Unlock();
        glareBtn.SetVisible();
        reloadBtn.Unlock();
        reloadBtn.SetVisible();
        installBtn.Lock();
        installBtn.SetInvisible();
        sugBonusBtn.Lock();
        sugBonusBtn.SetInvisible();
        cancelBonusBtn.Lock();
        cancelBonusBtn.SetInvisible();
        infoBonusBtn.Lock();
        infoBonusBtn.SetInvisible();
    }

    public void DefendingPrepare()
    {
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn.SetInvisible();
        flagBtn.Lock();
        installBtn.Lock();
        installBtn.SetInvisible();
        pvoBtn.Lock();
        pvoBtn.SetInvisible();
        glareBtn.Lock();
        glareBtn.SetInvisible();
        reloadBtn.Lock();
        reloadBtn.SetInvisible();
        installBtn.Lock();
        installBtn.SetInvisible();
    }

    public void BonusPrepare()
    {
        sugBonusBtn.SetVisible();
        sugBonusBtn.Unlock();
        cancelBonusBtn.Unlock();
        cancelBonusBtn.SetVisible();
        infoBonusBtn.Unlock();
        infoBonusBtn.SetVisible();
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn.Lock();
        flagBtn.SetInvisible();
    }

    public void AttackPrepare()
    {
        installBtn.Unlock();
        installBtn.SetVisible();
    }

    //endregion

    //region onTouch
    public void OnTouch(MotionEvent event)
    {
        //Обновляем позицию касания
        touchPos.SetValue((int)event.getX(),(int)event.getY());

        //Обновляем кнопки
        ButtonsUpdate();
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Пытаемся обработать нажатия кнопок
            CheckButtons();
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //Сбрасываем состояние кнопок
            ButtonsReset();
        }
        battle.OnTouch(event);
    }
    //endregion

    //region Buttons Controller
    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        //Если нажата кнопка установки юнита
        if (installBtn.IsClicked())
        {
            if(battle.state == BattleState.Installation)
            {
                battle.InstallUnit();
            }
            else
            {
                if(battle.IsUnitSelected())
                {
                    MoveGates();
                }
            }
            isButtonPressed = true;
        }
        //Если нажата кнопка поворота юнита
        else if (turnBtn.IsClicked())
        {
            battle.TurnUnit();
            isButtonPressed = true;
        }

        //Если нажата кнопка отмены выбора юнита
        else if (cancelBtn.IsClicked())
        {
            if(battle.CancelSelection() && !selectingPanel.isClose)
                selectingPanel.Move();
            isButtonPressed = true;
        }

        //Если нажата кнопка завершения установки
        else if (installationFinishBtn.IsClicked())
        {
            if (battle.state == BattleState.Installation)
            {
                if (battle.CheckInstallationFinish())
                {
                    MoveGates();
                    installBtn.SetPosition((int)(screenWidth - Assets.btnInstall.getWidth() - 100 * Assets.monitorWidthCoeff), (int)(100 * Assets.monitorHeightCoeff));
                    cancelBtn.Lock();
                    turnBtn.Lock();
                    selectingPanel.CloseBtnLock();
                    installationFinishBtn.Lock();
                }
            }
            isButtonPressed = true;
        }

        else if(selectingPanel.IsCloseBtnPressed() && selectingPanel.isStop)
        {
            selectingPanel.Move();
            isButtonPressed = true;
        }

        else if(shootBtn.IsClicked() && IsGatesOpen())
        {
            battle.PreparePlayerShoot();
        }

        else if(flagBtn.IsClicked() && IsGatesOpen())
        {
            battle.eField.SetFlag();
        }

        else if(bonusPanel.IsCloseBtnPressed() && bonusPanel.isStop)
        {
            bonusPanel.Move();
            isButtonPressed = true;
        }

        else if(glareBtn.IsClicked())
        {
            if(battle.glareBonus.IsReloaded())
            {
                BonusPrepare();
                glare = true;
            }
            else
            {
                infoBonusPanel.Move();
                textBonuses = "До отключения перезарядки осталось " + battle.glareBonus.currentReload;
            }
        }
        else if(pvoBtn.IsClicked())
        {
            if(battle.pvo.IsReloaded())
            {
                BonusPrepare();
                pvo = true;
            }
            else
            {
                infoBonusPanel.Move();
                textBonuses = "До отключения перезарядки осталось " + battle.pvo.currentReload;
            }
        }
        else if(reloadBtn.IsClicked())
        {
            if(battle.reloadBonus.IsReloaded())
            {
                BonusPrepare();
                reloadBonus = true;
            }
            else
            {
                infoBonusPanel.Move();
                textBonuses = "До отключения перезарядки осталось "+battle.reloadBonus.currentReload;
            }
        }
        else if(sugBonusBtn.IsClicked())
        {
            if(glare)
            {
                ForBonusEnemy.socket.SetValue(battle.eField.GetLocalSocketCoord(battle.eField.selectedSocket));
                battle.PrepareToGlare();
                glare = false;
                ShootingPrepare();
            }
            else if(pvo)
            {
                battle.PVOInfoSend();
                pvo = false;
                ShootingPrepare();
            }
            else if(reloadBonus)
            {
                battle.SendReloadInfo();
                reloadBonus = false;
                ShootingPrepare();
            }
            if(infoBonusPanel.isClose)
                infoBonusPanel.Move();
        }
        else if(cancelBonusBtn.IsClicked())
        {
            if(infoBonusPanel.isClose)
                infoBonusPanel.Move();
            ShootingPrepare();
            pvo = false;
            glare = false;
            reloadBonus = false;
        }
        else if(infoBonusBtn.IsClicked())
        {
            infoBonusPanel.Move();
            if(glare)
                textBonuses = battle.glareBonus.info;
            else if(pvo)
                textBonuses = battle.pvo.info;
            else if(reloadBonus)
                textBonuses = battle.reloadBonus.info;
        }
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        selectingPanel.UpdateCloseBtn(touchPos);
        bonusPanel.UpdateCloseBtn(touchPos);
        installationFinishBtn.Update(touchPos);
        shootBtn.Update(touchPos);
        flagBtn.Update(touchPos);
        glareBtn.Update(touchPos);
        pvoBtn.Update(touchPos);
        reloadBtn.Update(touchPos);
        sugBonusBtn.Update(touchPos);
        cancelBonusBtn.Update(touchPos);
        infoBonusBtn.Update(touchPos);

        if(battle.state == BattleState.Installation)
        {
            if (battle.IsUnitSelected())
            {
                installBtn.Update(touchPos);
                cancelBtn.Update(touchPos);
                turnBtn.Update(touchPos);
            }
        }
        else
        {
            installBtn.Update(touchPos);
        }
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        installationFinishBtn.Reset();
        cancelBtn.Reset();
        turnBtn.Reset();
        installBtn.Reset();
        selectingPanel.ResetCloseBtn();
        shootBtn.Reset();
        flagBtn.Reset();
        bonusPanel.ResetCloseBtn();
        glareBtn.Reset();
        reloadBtn.Reset();
        pvoBtn.Reset();
        sugBonusBtn.Reset();
        cancelBonusBtn.Reset();
        infoBonusBtn.Reset();
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        if(battle.state == BattleState.Installation)
        {
            installationFinishBtn.Draw(graphics);
            if (battle.IsUnitSelected())
            {
                cancelBtn.Draw(graphics);
                turnBtn.Draw(graphics);
                installBtn.Draw(graphics);
            }
        }
        else
        {
            installBtn.Draw(graphics);
            shootBtn.Draw(graphics);
            flagBtn.Draw(graphics);
            glareBtn.Draw(graphics);
            pvoBtn.Draw(graphics);
            reloadBtn.Draw(graphics);
            sugBonusBtn.Draw(graphics);
            cancelBonusBtn.Draw(graphics);
            infoBonusBtn.Draw(graphics);
        }
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        installationFinishBtn = new Button(Assets.btnFinishInstallation, (int)(50 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), (int)(50 * Assets.monitorHeightCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
        installationFinishBtn.Scale(Assets.btnCoeff);
        cancelBtn = new Button(Assets.btnCancel, (int)(50 * Assets.monitorWidthCoeff + Assets.btnCancel.getWidth()/2 * Assets.btnCoeff), (int)((50 + 30) * Assets.monitorHeightCoeff  + Assets.btnCancel.getHeight() * Assets.btnCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        cancelBtn.Scale(Assets.btnCoeff);
        turnBtn = new Button(Assets.btnTurn, (int)(50 * Assets.monitorWidthCoeff + Assets.btnTurn.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - 2 * Assets.btnCancel.getHeight() * Assets.btnCoeff - (50 + 30) * Assets.monitorHeightCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        turnBtn.Scale(Assets.btnCoeff);
        installBtn = new Button(Assets.btnInstall, (int)(50 * Assets.monitorWidthCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - Assets.btnCancel.getHeight() * Assets.btnCoeff - 50 * Assets.monitorHeightCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), false);
        installBtn.Scale(Assets.btnCoeff);

        shootBtn = new Button(Assets.btnShoot, (int)(170 * Assets.monitorWidthCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), (int)(390 * Assets.monitorHeightCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), false);
        shootBtn.Scale(Assets.btnCoeff);
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn = new Button(Assets.btnFlag, (int)(170 * Assets.monitorWidthCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), (int)(170 * Assets.monitorHeightCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), false);
        flagBtn.Scale(Assets.btnCoeff);
        flagBtn.SetInvisible();
        flagBtn.Lock();
        sugBonusBtn = new Button(Assets.btnInstall, (int)(170 * Assets.monitorWidthCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), (int)(390 * Assets.monitorHeightCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), false);
        sugBonusBtn.Lock();
        sugBonusBtn.Scale(Assets.btnCoeff);
        sugBonusBtn.SetInvisible();
        reloadBtn = new Button(Assets.btnFinishInstallation, (int)(1000 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), (int)(450 * Assets.monitorHeightCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
        pvoBtn = new Button(Assets.btnFinishInstallation, (int)(1000 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), (int)(250 * Assets.monitorHeightCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
        glareBtn = new Button(Assets.btnFinishInstallation, (int)(1000 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), (int)(50 * Assets.monitorHeightCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
        cancelBonusBtn = new Button(Assets.btnCancel, (int)(170 * Assets.monitorWidthCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), (int)(170 * Assets.monitorHeightCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), false);
        cancelBonusBtn.Lock();
        cancelBonusBtn.SetInvisible();
        infoBonusBtn = new Button(Assets.btnFlag, (int)(170 * Assets.monitorWidthCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), (int)(600 * Assets.monitorHeightCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), false);
        infoBonusBtn.Lock();
        infoBonusBtn.SetInvisible();
        pvoBtn.Lock();
        pvoBtn.SetInvisible();
        glareBtn.Lock();
        glareBtn.SetInvisible();
        reloadBtn.Lock();
        reloadBtn.SetInvisible();
        cancelBonusBtn.Scale(Assets.btnCoeff);
        glareBtn.Scale(Assets.btnCoeff);
        pvoBtn.Scale(Assets.btnCoeff);
        reloadBtn.Scale(Assets.btnCoeff);
        infoBonusBtn.Scale(Assets.btnCoeff);
    }
    //endregion

    //region Draw
    public void Draw(Graphics graphics)
    {
        if (battle.state == BattleState.Attack || battle.state == BattleState.Shoot)
        {
            //Assets.monitor.Draw(graphics);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
        else
        {
            graphics.DrawSprite(background);
        }

        battle.DrawFields(graphics);

        if(battle.state == BattleState.Attack || battle.state == BattleState.Shoot)
        {
            if (bonusPanel.isClose || !bonusPanel.isStop)
                bonusPanel.Draw(graphics);
            bonusPanel.DrawButton(graphics);
        }

            ButtonsDraw(graphics);

        if(battle.state != BattleState.Attack && battle.state != BattleState.Shoot)
            battle.DrawUnits(graphics);

        if (battle.state == BattleState.Installation)
        {
            if (selectingPanel.isClose || !selectingPanel.isStop)
            {
                selectingPanel.Draw(graphics);
                battle.DrawUnitsIcons(graphics);
            }
            selectingPanel.DrawButton(graphics);
        }
        if (gateUp.isClose || !gateUp.isStop)
        {
            gateUp.Draw(graphics);
            gateDown.Draw(graphics);
        }

        if(battle.state == BattleState.Attack)
        {
            if (infoBonusPanel.isClose || !infoBonusPanel.isStop)
                infoBonusPanel.Draw(graphics);
            graphics.DrawText(textBonuses, Assets.arialFont, bonusInfoPos.x, bonusInfoPos.y, screenWidth, Color.WHITE, 40);
        }
    }
    //endregion

    //region Test
    public void GameOver(BattleState state, int reward)
    {
        if(planet != null && state == BattleState.Win)
        {
            if(BattlePlayer.armyType.equals("Space") && !BattleEnemy.haveGround || BattlePlayer.armyType.equals("Ground"))
            {
                planet.ConquerPlanet();
                glView.goBack();
            }
            else if(BattlePlayer.armyType.equals("Space") && BattleEnemy.haveGround)
                glView.goBack();
        }
        else if(planet != null && state == BattleState.Lose)
        {
            glView.goBack();
            glView.goBack();
        }
        if(planet == null)
        {
            glView.changeScreen(new GameOverView(glView, state, reward, planet != null));
            glView.getActivity().gameState = MainActivity.GameState.BRESULT;
        }
    }
    //endregion
}