package app.onedayofwar.Campaign.CharacterControl;

import android.util.Log;

import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 05.04.2015.
 */
public class MoveBehavior
{
    private Vector2 dir;
    private Vector2 lastV;
    private Vector2 angleVector;
    private Vector2 toMove;
    private float angle;
    private float rightAngle;
    private float leftAngle;
    private boolean needRotate;
    private boolean needLand;
    private boolean needTakeOff;
    private boolean isLand;
    private int velocity;
    private int currentVelocity;
    private int counter;
    private int counterLand;
    private int pointsToMove;

    private Sprite image;

    public MoveBehavior(Sprite image, int velocity, int pointsToMove)
    {
        this.velocity = velocity;
        this.image = image;
        this.pointsToMove = pointsToMove;
        Initialize();
    }

    public void Initialize()
    {
        dir = new Vector2();
        dir.SetFalse();
        lastV = new Vector2();
        angleVector = new Vector2();
        toMove = new Vector2();
        toMove.SetFalse();

        currentVelocity = velocity;
        needLand = false;
        needTakeOff = false;
        needRotate = false;
        isLand = false;
    }

    public void move(float eTime)
    {
        if(!toMove.IsFalse())
        {
            if(Math.abs(toMove.x - image.matrix[12]) < Math.abs((int)(currentVelocity*eTime*dir.x))  || Math.abs(image.matrix[13] - toMove.y) < Math.abs((int)(currentVelocity*eTime*dir.y)))
            {
                TechMSG.isFinishedWay = true;
                image.matrix[12] = toMove.x;
                image.matrix[13] = toMove.y;
                toMove.SetFalse();
            }
            if(!toMove.IsFalse() && pointsToMove > 0 && !needRotate)
            {
                image.Move(currentVelocity*dir.x*eTime, currentVelocity*dir.y*eTime);
                if(!isLand)
                    pointsToMove--;
                Log.i("POINTS",""+pointsToMove);
            }
        }
    }


    public void turn()
    {
        if(needRotate)
        {
            image.Rotate(angle/10, 0, 0, 1);
            counter++;
            if(counter == 10)
            {
                needRotate = false;
                counter = 0;
            }
        }
    }

    public boolean letsLending(float eTime)
    {
        if(needLand)
        {
            if(Math.abs(image.matrix[12] - toMove.x) < Math.abs(currentVelocity*eTime*dir.x) && Math.abs(image.matrix[13] - toMove.y) < Math.abs(currentVelocity*eTime*dir.y))
            {
                isLand = true;
                return true;
            }
        }
        return false;
    }

    public void calculateAngle()
    {
        angle = (float)Math.acos((lastV.x*dir.x + lastV.y*dir.y)/(Math.sqrt(lastV.x*lastV.x + lastV.y*lastV.y)*Math.sqrt(dir.x*dir.x+dir.y*dir.y)));
        angle *= 180f/Math.PI;
        needRotate = true;
        angleVector.SetValue(lastV.y, -lastV.x);
        rightAngle = (float)(Math.acos((angleVector.x*dir.x + angleVector.y*dir.y)/(Math.sqrt(angleVector.x*angleVector.x + angleVector.y*angleVector.y)*Math.sqrt(dir.x*dir.x + dir.y*dir.y))));
        angleVector.SetValue(-lastV.y, lastV.x);
        leftAngle = (float)(Math.acos((angleVector.x*dir.x + angleVector.y*dir.y)/(Math.sqrt(angleVector.x*angleVector.x + angleVector.y*angleVector.y)*Math.sqrt(dir.x*dir.x + dir.y*dir.y))));
        if(leftAngle < rightAngle)
        {
            angle *= -1;
        }
        dir.Normalize();
    }

    public boolean startLending(Space space)
    {
        if(isLand)
        {
            pointsToMove++;
            toMove.SetFalse();
            currentVelocity *= 0.75;
            image.Scale(0.7f);
            counterLand++;
            needLand = false;
            if(counterLand == 5)
            {
                isLand = false;
                counterLand = 0;
                if(TechMSG.isAttack || TechMSG.isPlayerLand)
                    space.GotoPlanet();
                return true;
            }
        }
        return false;
    }

    public void startTakeOff()
    {
        if (needTakeOff)
        {
            counterLand++;
            if(counterLand == 5)
            {
                needTakeOff = false;
                counterLand = 0;
                TechMSG.isAILand = false;
                TechMSG.isPlayerLand = false;
                TechMSG.isFinishedWay = true;
                TechMSG.isReadyForUpdate = false;
                /*TechMSG.isAttacked = false;
                TechMSG.attackedPlanet = -1;*/
            }
            image.Scale(1.43f);
            currentVelocity = velocity;
        }
    }

    public void stop()
    {
        if(pointsToMove == 0)
        {
            if(TechMSG.playerMove)
            {
                toMove.SetFalse();
                TechMSG.isFinishedWay = true;
                TechMSG.isPlayerLand = false;
                needLand = false;
                isLand = false;
            }
            else
            {
                if(TechMSG.isReadyForUpdate)
                    currentVelocity = 0;
            }
        }
    }


    public void letsTakeOff()
    {
        needTakeOff = true;
    }

    public void prepareToMove(Vector2 touchPos, Vector2 forLand, int x, int y)
    {
        if(!forLand.IsFalse())
        {
            toMove.SetValue(forLand);
            needLand = true;
            Log.i("LAND","in");

        }
        else
        {
            toMove.SetValue(touchPos.x - x, touchPos.y - y);
            needLand = false;
        }


        if(!toMove.IsFalse())
        {
            if(!dir.IsFalse())
                lastV.SetValue(dir.x, dir.y);
            else
                lastV.SetValue(0, velocity);
            dir.SetValue((toMove.x - image.matrix[12]), (toMove.y - image.matrix[13]));
        }
        calculateAngle();
    }

    public int getPointsToMove(){return pointsToMove;}

    public void setPointsToMove(int value){pointsToMove = value; currentVelocity = velocity;}
}
