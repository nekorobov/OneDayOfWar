package app.onedayofwar.Campaign.CharacterControl;

import java.util.ArrayList;

import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 06.03.2015.
 */
public class Player extends Character
{
    public Player(Space space, int pointsToMove)
    {
        super(space, pointsToMove);
    }

    public void Initialize()
    {
        armyGround = new byte[]{1,1,1,1,1,1};
        armySpace = new byte[]{0,0,0,1,1,1};
        image = new Sprite(Assets.player);
        velocity = 1000;
        conqueredPlanets = new ArrayList<>();
        myStep = true;

        image.setPosition(width/2 - 100, height/2 + 50);
        moveBehavior = new MoveBehavior(image, velocity, pointsToMove);
        UpdateConqueredPlanets();
    }


    public void followToTap(Vector2 touchPos, Vector2 forLand, int x, int y)
    {
        //Вычисляем напрвление и угол поворота
        moveBehavior.prepareToMove(touchPos, forLand, x, y);
    }



    public void Update(float eTime)
    {
        //Разворот если требуется
        moveBehavior.turn();
        //Требуется ли посадка
        if(moveBehavior.getPointsToMove() != 0)
            if(moveBehavior.letsLending(eTime))
                TechMSG.isPlayerLand = true;
        //Ночало посадки если требуется
        moveBehavior.startLending(space);
        //Начало взлета если требуется
        moveBehavior.startTakeOff();
        if(myStep)
        {
            //Передвигаем игрока
            moveBehavior.move(eTime);
        }

        if(myStep)
            moveBehavior.stop();

        if(moveBehavior.getPointsToMove() == 0 && myStep)
        {
            /*if(TechMSG.isAutoPilot)
            {
                TechMSG.isFinishedWay = false;
                space.getAI().setPointsToMove(pointsToMove);
                TechMSG.playerMove = false;
                TechMSG.isNeedMove = false;
                space.PrepareToAIStep();
            }*/
        }
    }

    public void lestTakeOff()
    {
        moveBehavior.letsTakeOff();
        UpdateConqueredPlanets();
        TechMSG.attackedPlanet = -1;
    }

    public void UpdateConqueredPlanets()
    {
        conqueredPlanets.clear();
        for(int i = 0; i < space.getPlanetController().getPlanets().size(); i++)
        {
            if(space.getPlanetController().getPlanet(i).IsConquered())
                conqueredPlanets.add(i);
        }
        if(conqueredPlanets.size() > 3)
            TechMSG.isFirstPlanetConquered = true;

    }

    public void Attacked(){}



}