package app.onedayofwar.Campaign.System;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Campaign.CharacterControl.TechMSG;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.UI.Button;

/**
 * Created by Slava on 30.03.2015.
 */
public class PlanetView implements ScreenView
{
    private enum State {MAIN, BUILD, FACTORY, WORKSHOP, UNIT_TRANSACTION, RESOURCES_TRANSACTION, CHOISE_ARMY_TYPE}
    private State state;
    private GLView glView;
    private Space space;
    private Planet planet;
    private Button attackBtn;
    private Button exitBtn;
    private Button buildInfo;
    private Button showFactory;
    private Button resourcesMenuBtn;
    private Button groundArmyBtn;
    private Button spaceArmyBtn;
    private Button[] upgradeBuilding;
    private Button[] createUnit;
    private Vector2 touchPos;
    private Sprite unit;
    private UpgradeSystem upgradeSystem;
    private float buildInfoTextSize;
    private int createType;

    public PlanetView(GLView glView, Space space, Planet planet)
    {
        this.glView = glView;
        this.planet = planet;
        this.space = space;
    }

    @Override
    public void Initialize(Graphics graphics)
    {
        state = State.MAIN;
        boolean isClear = true;
        if(!planet.IsConquered())
        {
            for(int i = 0; i < planet.getGroundGuards().length; i++)
            {
                if(planet.getGroundGuards()[i] > 0)
                    isClear = false;
            }
            if(isClear)
            {
                if(TechMSG.isAILand)
                    planet.AntiConquerPlanet();
                else if (TechMSG.isPlayerLand)
                {
                    planet.ConquerPlanet();
                }

            }
            else if(planet.isSpaceArmyHere())
            {
                Log.i("BATTLE", "START");
                BattlePlayer.fieldSize = 15;
                BattlePlayer.armyType = "Space";
                BattlePlayer.unitCount = space.getPlayer().getArmySpace().clone();
                SingleBattle.difficulty = (byte)(BattlePlayer.level);
                glView.StartBattle(planet, 'c', Math.random() < 0.5);
                BattleEnemy.haveGround = planet.isGroundArmyHere();
            }
        }


        createUnit = new Button[6]; //Сначала космические потом земные
        upgradeBuilding = new Button[5];
        touchPos = new Vector2();
        float unitCoeff = glView.getScreenWidth() * 0.9f /(Assets.robotImage.getWidth() + Assets.ifvImage.getWidth() + Assets.engineerImage.getWidth() + Assets.tankImage.getWidth() + Assets.turretImage.getWidth() + Assets.sonderImage.getWidth() + 5 * 30);
        unit = new Sprite(Assets.robotImage);
        unit.Scale(unitCoeff);
        buildInfoTextSize = 0.04f * glView.getScreenHeight();
        upgradeSystem = new UpgradeSystem(planet);
        createType = 0;
        ButtonsInitialize();
    }

    @Override
    public void Update(float eTime)
    {

    }

    @Override
    public void Draw(Graphics graphics)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        switch(state)
        {
            case MAIN:
                graphics.DrawText("Credits: " + planet.credits + "\nOil: " + planet.oil + "\nNanosteel: " + planet.nanosteel, Assets.arialFont, 0, 0, 0, Color.GREEN, 50);

                unit.setTexture(Assets.robotImage);
                unit.setPosition(unit.getWidth() / 2 + 30, 200 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.ifvImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.engineerImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.tankImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.turretImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.sonderImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.robotImage);
                unit.setPosition(unit.getWidth() / 2 + 10, 300 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, unit.getHeight()/2);

                unit.setTexture(Assets.akiraImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.defaintImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.battleshipImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2, 0);

                unit.setTexture(Assets.bioshipImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2,0);

                unit.setTexture(Assets.birdOfPreyImage);
                unit.Move(unit.getWidth() / 2 + 30 , 0);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getSpaceGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(unit.getWidth()/2 ,0);
                break;

            case BUILD:
                graphics.DrawText("Market: " + planet.getBuildings()[0], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[0].getMatrix()[13] - upgradeBuilding[0].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Oil Drill: " + planet.getBuildings()[1], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[1].getMatrix()[13] - upgradeBuilding[1].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Nanosteel Mines: " + planet.getBuildings()[2], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[2].getMatrix()[13] - upgradeBuilding[2].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Factory: " + planet.getBuildings()[3], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[3].getMatrix()[13] - upgradeBuilding[3].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Workshop: " + planet.getBuildings()[4], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[4].getMatrix()[13] - upgradeBuilding[4].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);

                if(!planet.buildingUpgrade.IsFalse())
                {
                    graphics.DrawText("UPGRADE IN PROGRESS:\n" + planet.upgradeName + " " + (planet.getBuildings()[(int)planet.buildingUpgrade.x] + 1) + " LEVEL", Assets.arialFont, glView.getScreenWidth()/2 - 100, glView.getScreenHeight()/2, 0, Color.GREEN, 40);
                }
                break;

            case FACTORY:
                if(createType == 1)
                {
                    unit.setTexture(Assets.robotImage);
                    unit.setPosition(unit.getWidth() / 2 + 30, 200 + unit.getHeight() / 2);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.ifvImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.engineerImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.tankImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.turretImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.sonderImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);
                }
                else if(createType == 2)
                {
                    unit.setTexture(Assets.robotImage);
                    unit.setPosition(unit.getWidth() / 2 + 10, 200 + unit.getHeight() / 2);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, unit.getHeight()/2);

                    unit.setTexture(Assets.akiraImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.defaintImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.battleshipImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.bioshipImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2,0);

                    unit.setTexture(Assets.birdOfPreyImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                    unit.Move(unit.getWidth()/2 ,0);
                }
                break;

            case WORKSHOP:

                break;

            case UNIT_TRANSACTION:

                break;

            case CHOISE_ARMY_TYPE:

                break;
        }
        ButtonsDraw(graphics);
    }

    @Override
    public void OnTouch(MotionEvent event)
    {
        touchPos.SetValue(event.getX(), event.getY());
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
    }

    @Override
    public void Resume()
    {
        if(planet.IsConquered())
        {
            attackBtn.Lock();
            attackBtn.SetInvisible();

            showFactory.SetVisible();
            showFactory.Unlock();

            buildInfo.SetVisible();
            buildInfo.Unlock();
        }
    }

    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        if(attackBtn.IsClicked())
        {
            Log.i("BATTLE", "START");
            BattlePlayer.fieldSize = 15;
            BattlePlayer.armyType = "Ground";
            BattlePlayer.unitCount = space.getPlayer().getArmyGround().clone();
            SingleBattle.difficulty = (byte)(BattlePlayer.level);
            glView.StartBattle(planet, 'c', Math.random() < 0.5);
        }
        else if(exitBtn.IsClicked())
        {
            switch(state)
            {
                case MAIN:
                    if(TechMSG.isPlayerLand)
                    {
                        TechMSG.isPlayerLand = false;
                        TechMSG.playerMove = false;
                        space.getPlayer().lestTakeOff();
                    }
                    if(TechMSG.isAILand)
                    {
                        TechMSG.isAILand = false;
                        TechMSG.playerMove = true;
                        space.getAI().lestTakeOff();
                    }
                    glView.goBack();
                    break;

                case BUILD:
                    state = State.MAIN;
                    buildInfo.SetVisible();
                    buildInfo.Unlock();
                    for(int i = 0; i < upgradeBuilding.length; i++)
                    {
                        upgradeBuilding[i].SetInvisible();
                        upgradeBuilding[i].Lock();
                    }
                    if(planet.getBuildings()[3] > 0)
                    {
                        showFactory.SetVisible();
                        showFactory.Unlock();
                    }
                    break;

                case FACTORY:
                    state = State.CHOISE_ARMY_TYPE;
                    for(int i = 0; i < createUnit.length; i++)
                    {
                        createUnit[i].SetInvisible();
                        createUnit[i].Lock();
                    }
                    spaceArmyBtn.Unlock();
                    spaceArmyBtn.SetVisible();
                    groundArmyBtn.Unlock();
                    groundArmyBtn.SetVisible();
                    break;

                case WORKSHOP:

                    break;

                case UNIT_TRANSACTION:

                    break;

                case RESOURCES_TRANSACTION:
                    state = State.MAIN;
                    buildInfo.SetVisible();
                    buildInfo.Unlock();
                    resourcesMenuBtn.SetVisible();
                    resourcesMenuBtn.Unlock();
                    if(planet.getBuildings()[3] > 0)
                    {
                        showFactory.SetVisible();
                        showFactory.Unlock();
                    }
                    break;

                case CHOISE_ARMY_TYPE:
                    state = State.MAIN;
                    buildInfo.SetVisible();
                    buildInfo.Unlock();
                    resourcesMenuBtn.SetVisible();
                    resourcesMenuBtn.Unlock();
                    if(planet.getBuildings()[3] > 0)
                    {
                        showFactory.SetVisible();
                        showFactory.Unlock();
                    }
                    groundArmyBtn.SetInvisible();
                    groundArmyBtn.Lock();
                    spaceArmyBtn.Lock();
                    spaceArmyBtn.SetInvisible();
            }
        }
        else if(buildInfo.IsClicked())
        {
            state = State.BUILD;
            buildInfo.SetInvisible();
            buildInfo.Lock();
            for(int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].SetVisible();
                upgradeBuilding[i].Unlock();
            }
            if(planet.getBuildings()[3] > 0)
            {
                showFactory.SetInvisible();
                showFactory.Lock();
            }
        }
        else if(showFactory.IsClicked())
        {
            state = State.CHOISE_ARMY_TYPE;
            showFactory.Lock();
            showFactory.SetInvisible();
            buildInfo.SetInvisible();
            buildInfo.Lock();
            spaceArmyBtn.Unlock();
            spaceArmyBtn.SetVisible();
            groundArmyBtn.Unlock();
            groundArmyBtn.SetVisible();
        }
        else if(resourcesMenuBtn.IsClicked())
        {
            state = State.RESOURCES_TRANSACTION;
            showFactory.Lock();
            showFactory.SetInvisible();
            resourcesMenuBtn.Lock();
            resourcesMenuBtn.SetInvisible();
            buildInfo.Lock();
            buildInfo.SetInvisible();
        }
        else if(groundArmyBtn.IsClicked())
        {
            createType = 1;
            state = State.FACTORY;
            groundArmyBtn.SetInvisible();
            groundArmyBtn.Lock();
            spaceArmyBtn.SetInvisible();
            spaceArmyBtn.Lock();
            for(int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].SetVisible();
                createUnit[i].Unlock();
            }
        }
        else if(spaceArmyBtn.IsClicked())
        {
            createType = 2;
            state = State.FACTORY;
            groundArmyBtn.SetInvisible();
            groundArmyBtn.Lock();
            spaceArmyBtn.SetInvisible();
            spaceArmyBtn.Lock();

            for(int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].SetVisible();
                createUnit[i].Unlock();
            }
        }
        else
        {
            for(int i = 0; i < upgradeBuilding.length; i++)
            {
                if (upgradeBuilding[i].IsClicked())
                {
                    upgradeSystem.UpgradeBuild(i);
                    return;
                }
            }
            for(int i = 0; i < createUnit.length; i++)
            {

                if(createUnit[i].IsClicked())
                {
                    Log.i("I",""+i);
                    if(createType == 1)
                    {
                        upgradeSystem.CreateUnit(i+6);
                        return;
                    }
                    else if(createType == 2)
                    {
                        upgradeSystem.CreateUnit(i);
                        return;
                    }
                }
            }
        }
    }



    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        if (planet.IsConquered())
        {
            buildInfo.Update(touchPos);
            showFactory.Update(touchPos);
            resourcesMenuBtn.Update(touchPos);
            groundArmyBtn.Update(touchPos);
            spaceArmyBtn.Update(touchPos);
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].Update(touchPos);
            }
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                createUnit[i].Update(touchPos);
            }
        }
        else
        {
            attackBtn.Update(touchPos);
        }
        exitBtn.Update(touchPos);
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        attackBtn.Reset();
        exitBtn.Reset();
        buildInfo.Reset();
        showFactory.Reset();
        resourcesMenuBtn.Reset();
        spaceArmyBtn.Reset();
        groundArmyBtn.Reset();
        for(int i = 0; i < upgradeBuilding.length; i++)
        {
            upgradeBuilding[i].Reset();
        }
        for(int i = 0; i < createUnit.length; i++)
        {
            createUnit[i].Reset();
        }
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        if(planet.IsConquered())
        {
            buildInfo.Draw(graphics);
            showFactory.Draw(graphics);
            resourcesMenuBtn.Draw(graphics);
            groundArmyBtn.Draw(graphics);
            spaceArmyBtn.Draw(graphics);
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].Draw(graphics);
            }
            for (int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].Draw(graphics);
            }
        }
        else
        {
            attackBtn.Draw(graphics);
        }
        exitBtn.Draw(graphics);
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        float upgradeBtnCoeff = glView.getScreenHeight() * 0.9f / (planet.getBuildings().length * (Assets.btnInstall.getHeight() + buildInfoTextSize));

        exitBtn = new Button(Assets.btnCancel, 0, 0, false);
        exitBtn.Scale(Assets.btnCoeff);
        exitBtn.SetPosition(glView.getScreenWidth() - exitBtn.width/2 - 50, glView.getScreenHeight() - exitBtn.height/2 - 50);

        attackBtn = new Button(Assets.btnFinishInstallation, 0, 0, false);
        attackBtn.Scale(Assets.btnCoeff);
        attackBtn.SetPosition(exitBtn.getMatrix()[12], exitBtn.getMatrix()[13] - attackBtn.height - 10);

        buildInfo = new Button(Assets.btnTurn, 0, 0, false);
        buildInfo.Scale(Assets.btnCoeff);
        buildInfo.SetPosition(exitBtn.getMatrix()[12], exitBtn.getMatrix()[13] - attackBtn.height - 10);
        if(!planet.IsConquered())
        {
            buildInfo.SetInvisible();
            buildInfo.Lock();
        }

        showFactory = new Button(Assets.btnInstall, 0, 0, false);
        showFactory.Scale(Assets.btnCoeff);
        showFactory.SetPosition(attackBtn.getMatrix()[12], buildInfo.getMatrix()[13] - showFactory.height - 10);
        if(!planet.IsConquered() || planet.getBuildings()[3] == 0)
        {
            showFactory.SetInvisible();
            showFactory.Lock();
        }

        for(int i = 0; i < upgradeBuilding.length; i++)
        {
            upgradeBuilding[i] = new Button(Assets.btnInstall, 0, 0, false);
            upgradeBuilding[i].Scale(upgradeBtnCoeff);
            upgradeBuilding[i].SetPosition(upgradeBuilding[i].width/2 + 50, upgradeBuilding[i].height/2 + buildInfoTextSize +  i * (upgradeBuilding[i].height + buildInfoTextSize));
            upgradeBuilding[i].SetInvisible();
            upgradeBuilding[i].Lock();
        }

        for(int i = 0; i < createUnit.length; i++)
        {
            createUnit[i] = new Button(Assets.btnInstall, 0, 0, false);
            createUnit[i].Scale(Assets.btnCoeff);
            createUnit[i].SetPosition(createUnit[i].width/2 + i * (createUnit[i].width + 100), 100);
            createUnit[i].SetInvisible();
            createUnit[i].Lock();
        }

        resourcesMenuBtn = new Button(Assets.btnShoot, 0, 0, false);
        resourcesMenuBtn.Scale(upgradeBtnCoeff);
        resourcesMenuBtn.SetPosition(glView.getScreenWidth() - exitBtn.width/2 - 50 - 50 - resourcesMenuBtn.width, glView.getScreenHeight() - exitBtn.height/2 - 50);
        if(!planet.IsConquered())
        {
            buildInfo.SetInvisible();
            buildInfo.Lock();
        }

        groundArmyBtn = new Button(Assets.btnInstall, glView.getScreenWidth()/2 - Assets.btnInstall.getWidth()/2*upgradeBtnCoeff - 10, glView.getScreenHeight()/2, false);
        groundArmyBtn.Scale(upgradeBtnCoeff);
        spaceArmyBtn = new Button(Assets.btnInstall,  glView.getScreenWidth()/2 + Assets.btnInstall.getWidth()/2*upgradeBtnCoeff + 10, glView.getScreenHeight()/2, false);
        spaceArmyBtn.Scale(upgradeBtnCoeff);
        groundArmyBtn.SetInvisible();
        spaceArmyBtn.SetInvisible();
        groundArmyBtn.Lock();
        spaceArmyBtn.Lock();
    }
}
