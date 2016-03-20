package app.onedayofwar.Battle.Units;

import android.opengl.Matrix;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 31.01.2015.
 */
public class Bullet
{
    private Vector2 destination;
    private Sprite image;
    private float velocity;
    private float velocityY;
    private byte type;
    private float curveAngle;
    private float lastAngle;
    public enum State { FLY, LAUNCH, BOOM}
    public State state;

    public Bullet()
    {
        state = State.LAUNCH;
        destination = new Vector2();
        image = new Sprite(Assets.bullet);
        image.Scale((float)Assets.isoGridCoeff);
        velocity = 300;
    }

    public void Draw(Graphics graphics)
    {
        if(state == State.FLY)
        {
            graphics.DrawSprite(image);
        }
    }

    public void Update(float eTime)
    {
        if(image.matrix[13] + image.getHeight() >= destination.y)
        {
            state = State.BOOM;
            return;
        }
        velocityY = destination.y / destination.x / destination.x * image.matrix[12] * image.matrix[12] - image.matrix[13];
        curveAngle = (float)(Math.atan(2d * destination.y * image.matrix[12] / destination.x / destination.x) * 180 / Math.PI);
        image.Move(velocity * eTime, velocityY);
        Matrix.rotateM(image.matrix, 0, -curveAngle + lastAngle, 0, 0, 1);
        lastAngle = curveAngle;
    }

    public void Launch(float x, float y, byte type)
    {
        state = State.FLY;
        destination.SetValue(x,y);
        this.type = type;
        switch(type)
        {
            case 0:
                image.setTexture(Assets.bullet);
                break;
            case 1:
                image.setTexture(Assets.miniRocket);
                break;
        }
    }

    public void Reload()
    {
        state = State.LAUNCH;
        destination.SetZero();
        lastAngle = 0;
        curveAngle = 0;
        image.ResetMatrix();
        image.Scale((float)Assets.isoGridCoeff);
    }

    public Vector2 getPos(){return new Vector2(image.matrix[12], image.matrix[13]);}

    public int getWidth(){return image.getWidth();}

    public int getHeight(){return image.getHeight();}
}
