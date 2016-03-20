package app.onedayofwar.Campaign.CharacterControl;

import java.util.ArrayList;

import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;

/**
 * Created by Никита on 06.04.2015.
 */
public abstract class Character
{

    Sprite image;
    MoveBehavior moveBehavior;
    Space space;
    int velocity;
    int pointsToMove;
    int height;
    int width;
    boolean myStep;
    byte[] armyGround;
    byte[] armySpace;
    int[] resources;
    ArrayList<Integer> conqueredPlanets;

    protected Character(Space space, int pointsToMove)
    {
        this.space = space;
        this.pointsToMove = pointsToMove;
        resources = new int[3];
        Initialize();
    }

    protected abstract void Initialize();

    public void Draw(Graphics g)
    {
        g.DrawSprite(image);
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public byte[] getArmyGround()
    {
        return armyGround;
    }

    public byte[] getArmySpace(){return  armySpace;}

    public Sprite getImage(){return image;}

    public void setPointsToMove(int value){moveBehavior.setPointsToMove(value); TechMSG.isStopped = false; myStep = true;}

    public int getPointsToMove(){return moveBehavior.getPointsToMove();}

    public int[] getResources()
    {
        return resources;
    }

    public ArrayList<Integer> getConqueredPlanets(){return conqueredPlanets;}

    public void DBLoad(float x, float y, int movePoints, int[] resources)
    {
        this.resources = resources.clone();
        moveBehavior.setPointsToMove(movePoints);
        getMatrix()[12] = x;
        getMatrix()[13] = y;

    }
    public void InverseMyStep(){myStep = false;}
}
