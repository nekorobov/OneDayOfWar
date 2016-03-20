package app.onedayofwar.System;

/**
 * Created by Slava on 26.10.2014.
 */

public class Vector2
{
    public float x;
    public float y;


    //region Constructor
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;

    }

    public Vector2(Vector2 sourceVector)
    {
        x = sourceVector.x;
        y = sourceVector.y;
    }

    public Vector2()
    {
        x = 0;
        y = 0;
    }
    //endregion

    public void SetZero()
    {
        x = 0;
        y = 0;
    }

    public void SetFalse()
    {
        x = -1;
        y = -1;
    }

    public void Normalize()
    {
        float length = (float)Math.sqrt(x*x + y*y);
        x /= length;
        y /= length;
    }

    public void SetValue(Vector2 sourceVector)
    {
        x = sourceVector.x;
        y = sourceVector.y;
    }

    public void SetValue(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean IsFalse()
    {
        return x == -1 && y == -1;
    }

    public boolean IsNegative(boolean useAnd)
    {
        if(useAnd)
            return x < 0 && y < 0;
        else
            return x < 0 || y < 0;
    }

    public void ChangeSign()
    {
        x = -x;
        y = -y;
    }

    public boolean Equals(Vector2 vector)
    {
        return Math.abs(x - vector.x) < 0.0001f && Math.abs(y - vector.y) < 0.0001f;
    }

    public void ChangeSign(boolean changeX)
    {
        if(changeX)
            x = -x;
        else
            y = -y;
    }
}
