package app.onedayofwar.Campaign.CharacterControl;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Campaign.System.UpgradeSystem;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 05.04.2015.
 */
public class AI extends Character
{
    Character enemy;
    private float[] profibilityTable;
    private int selectedPlanet;
    private int needForUpdate;
    private boolean isUpgraded;
    private int upgradedPlanet;
    private int previousSize;
    private int delta;
    private int dangerousZone;
    private int level;

    private UpgradeSystem upgradeSystem;

    public AI(Space space, int pointsToMove, Character enemy)
    {
        super(space, pointsToMove);
        this.enemy = enemy;
    }

    public void Initialize()
    {
        image = new Sprite(Assets.player);
        image.setColorFilter(Color.RED);
        velocity = 1000;
        selectedPlanet = -1;
        image.setPosition(width/2 + 500, height/2+350);
        armyGround = new byte[]{1,1,1,1,1,1};
        armySpace = new byte[]{0,0,0,1,1,1};
        moveBehavior = new MoveBehavior(image, velocity, 0);
        conqueredPlanets = new ArrayList<>();
        upgradedPlanet = 0;
        myStep = false;
        dangerousZone = 1;


        profibilityTable = new float[space.getPlanetController().getPlanets().size()];

        isUpgraded = false;
        UpdateConqueredPlanets();
        previousSize = conqueredPlanets.size();
        delta = 0;
    }

    public void behaviorControl()
    {
        if(myStep)
        {
            Log.i("DELTA",""+delta);
            if(TechMSG.isAttacked)
                if(TechMSG.attackedPlanet != -1)
                    moveBehavior.prepareToMove(null, new Vector2(space.getPlanetController().getPlanet(TechMSG.attackedPlanet).getMatrix()[12],space.getPlanetController().getPlanet(TechMSG.attackedPlanet).getMatrix()[13]),0,0);
            if(delta > dangerousZone)
                selectPlanetForAttack();
            else if (selectedPlanet == -1)
                selectPlanetForUpdate();
        }
    }

    public void Update(float eTime)
    {

            //Разворот если требуется
            moveBehavior.turn();
            //Требуется ли посадка
            if(moveBehavior.getPointsToMove() != 0)
                if(moveBehavior.letsLending(eTime))
                    TechMSG.isAILand = true;
            //Ночало посадки если требуется
            if(moveBehavior.startLending(space))
            {
                UpdateOnPlanet();
                if(TechMSG.isAttack)
                    space.startBattle();
            }

            if(isUpgraded)
            {
                //Начало взлета если требуется
                lestTakeOff();
                selectedPlanet = -1;
                isUpgraded = false;
                behaviorControl();
            }


            moveBehavior.startTakeOff();

            if(!TechMSG.playerMove && !TechMSG.isAILand)
                //Передвигаем игрока
                moveBehavior.move(eTime);

            if(myStep)
                moveBehavior.stop();

            if(moveBehavior.getPointsToMove() == pointsToMove)
                behaviorControl();


            if(moveBehavior.getPointsToMove() == 0 && !TechMSG.playerMove)
                space.NextTurn();
    }

    public void calculateLevel()
    {
        Planet planet;
        int buildings = 0;
        int army = 0;
        if(TechMSG.isAttack)
        {
            planet = space.getPlanetController().getPlanet(TechMSG.selectedPlanet);
            for(byte j = 0; j < planet.getGroundGuards().length; j++)
            {
                army += planet.getGroundGuards()[j];
            }
            for(byte j = 0; j < planet.getSpaceGuards().length; j++)
            {
                army += planet.getSpaceGuards()[j];
            }
            if(army > 1 && army < 10)
                level = army*2;
            else if(army > 10 && army < 20)
                level = (int)(army*1.5);
            else if(army > 20 && army < 40)
                level = army;
            else if(army > 40)
                level = 45;
        }
        else if(TechMSG.isAttacked)
        {
            planet = space.getPlanetController().getPlanet(TechMSG.attackedPlanet);
            for(byte j = 0; j < planet.getBuildings().length; j++)
            {
                buildings += planet.getBuildings()[j];
            }
            for(byte j = 0; j < planet.getGroundGuards().length; j++)
            {
                army += planet.getGroundGuards()[j];
            }
            for(byte j = 0; j < planet.getSpaceGuards().length; j++)
            {
                army += planet.getSpaceGuards()[j];
            }
            if(army + buildings > 1 && army + buildings < 10)
                level = (army+buildings)*2;
            else if(army+buildings > 10 && army+buildings < 20)
                level = (int)((army+buildings)*1.5);
            else if(army + buildings > 20 && army+buildings < 40)
                level = army + buildings;
            else if(army + buildings > 40)
                level = 45;
        }
        BattlePlayer.level = level;
    }



    public void lestTakeOff()
    {
        moveBehavior.letsTakeOff();
        UpdateConqueredPlanets();
        TechMSG.isAttack = false;
        TechMSG.selectedPlanet = -1;
        selectedPlanet = -1;
    }

    public void doProfitabilityTable()
    {
        float rang;
        Planet planet;
        float enemyDir;
        float myDir;
        int buildings = 0;
        int army = 0;
        for(int i = 0; i < enemy.conqueredPlanets.size(); i++)
        {
            planet = space.getPlanetController().getPlanet(enemy.conqueredPlanets.get(i));
            enemyDir = (float)Math.sqrt(Math.pow(enemy.image.matrix[12]-planet.getMatrix()[12], 2) + Math.pow(enemy.image.matrix[13] - planet.getMatrix()[13],2));
            myDir = (float)Math.sqrt(Math.pow(image.matrix[12]-planet.getMatrix()[12], 2) + Math.pow(image.matrix[13] - planet.getMatrix()[13],2));
            for(byte j = 0; j < planet.getBuildings().length; j++)
            {
                buildings += planet.getBuildings()[j];
            }
            for(byte j = 0; j < planet.getGroundGuards().length; j++)
            {
                army += planet.getGroundGuards()[j];
            }
            for(byte j = 0; j < planet.getSpaceGuards().length; j++)
            {
                army += planet.getSpaceGuards()[j];
            }
            rang = (50*buildings + enemyDir)/(100*army + myDir);
            profibilityTable[enemy.conqueredPlanets.get(i)] =  rang;
        }
    }

    public void selectPlanetForAttack()
    {
        doProfitabilityTable();
        float max = 0;
        for(int i = 0; i < profibilityTable.length; i++)
        {
            if(max < profibilityTable[i])
            {
                max = profibilityTable[i];
                selectedPlanet = i;
            }
        }

        if(selectedPlanet != -1)
            startAttack();
    }

    public void selectPlanetForUpdate()
    {
        needForUpdate = getMin(space.getPlanetController().getPlanet(upgradedPlanet).getBuildings());
        TechMSG.isReadyForUpdate = true;
        moveBehavior.prepareToMove(null, new Vector2(space.getPlanetController().getPlanet(upgradedPlanet).getMatrix()[12], space.getPlanetController().getPlanet(upgradedPlanet).getMatrix()[13]),0,0);
        selectedPlanet = upgradedPlanet;
    }

    public void UpdateOnPlanet()
    {
        upgradeSystem = new UpgradeSystem(space.getPlanetController().getPlanets().get(selectedPlanet));
        upgradeSystem.UpgradeBuild(needForUpdate);
        isUpgraded = true;
        Log.i("UP",""+isUpgraded);
    }

    public int getMin(byte[] a)
    {
        int min = a[0];
        int index = 0;
        for(int i = 1; i<a.length; i++)
        {
            if(min > a[i])
            {
                min = a[i];
                index = i;
            }
        }
        return index;
    }

    public void startAttack()
    {
        TechMSG.isAttack = true;
        TechMSG.selectedPlanet = selectedPlanet;
        calculateLevel();
        moveBehavior.prepareToMove(null, new Vector2(space.getPlanetController().getPlanet(selectedPlanet).getMatrix()[12], space.getPlanetController().getPlanet(selectedPlanet).getMatrix()[13]), 0,0);
    }

    public void UpdateConqueredPlanets()
    {
        conqueredPlanets.clear();
        for(int i = 0; i < space.getPlanetController().getPlanets().size(); i++)
        {
            if(!space.getPlanetController().getPlanet(i).IsConquered())
                conqueredPlanets.add(i);
        }
        delta =  -conqueredPlanets.size() + previousSize;
        Log.i("SIZE",""+conqueredPlanets.size());
        previousSize = conqueredPlanets.size();
        while(true)
        {
            upgradedPlanet = (int)(Math.random()*(conqueredPlanets.size()-1));
            for(int i = 0; i < conqueredPlanets.size(); i++)
            {
                if(upgradedPlanet == conqueredPlanets.get(i))
                    return;
            }
        }

    }

    public void infoPlanetsDraw(Graphics g)
    {
        for(int i = 0; i < profibilityTable.length; i++)
            g.DrawText(""+profibilityTable[i], Assets.arialFont, space.getPlanetController().getPlanet(i).getMatrix()[12], space.getPlanetController().getPlanet(i).getMatrix()[13], space.getScreenWidth(), Color.WHITE, 40);
    }
}
