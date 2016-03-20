package app.onedayofwar.Battle.Units.Ground;

import android.opengl.Matrix;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Танк
 * Размер 3х2
 */
public class Tank extends Unit {

    public Tank(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = new Sprite(Assets.tankImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.tankIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.tankStroke);
            stroke.Scale((float)Assets.isoGridCoeff);
        }
        this.zoneID = (byte)zoneID;
        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        //для прямоугольника -78
        if(isVisible)
        {
            ResetPosition();
        }

        form = new Vector2[6];
        InitializeFormArray();

        accuracy = 100;
        power = 15000;
        hitPoints = 2000;
        armor = 1000;
        reloadTime = 5;
    }
    //endregion

    @Override
    public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit)
    {
        Vector2 tmp = new Vector2();
        Vector2[] tmpForm = new Vector2[form.length];
        Vector2 sizes = field.GetSocketsSizes();

        for(int i = 0 ; i < form.length; i++)
            tmpForm[i] = new Vector2();

        Vector2 tmpLocal;

        byte num = 0;
        byte toJ = 2;
        byte toI = 3;

        if(isRight)
        {
            toJ = 3;
            toI = 2;
        }

        for(int j = 0; j < toJ; j++)
        {
            for (int i = 0; i < toI; i++)
            {

                if(field.IsIso())
                {
                    tmp.SetValue(startSocket.x - sizes.x / 2 * (i - j), startSocket.y + sizes.y / 2 * (i + j));

                    if (-Math.abs(0.5 * (tmp.x - field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 < tmp.y)
                        return false;
                }
                else
                {
                    tmp.SetValue(startSocket.x + sizes.x * j, startSocket.y + sizes.y *i);

                    if (tmp.y >= field.getMatrix()[13] + field.height/2 || tmp.x >= field.getMatrix()[12] + field.width/2)
                        return false;
                }

                tmpLocal = field.GetLocalSocketCoord(tmp);

                if (field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;

                tmpForm[num].SetValue(tmp);

                num++;
            }
        }

        if(isInstallUnit)
        {
            for (int i = 0; i < form.length; i++)
                form[i].SetValue(tmpForm[i]);
           // if(isVisible && isRight) stroke.horizontalFlip();
        }

        return true;
    }

    @Override
    public byte GetZone()
    {
        return zoneID;
    }

    @Override
    protected void ResetOffset()
    {
        //для прямоугольника -78
        offset.SetValue((int)(18 * Assets.isoGridCoeff), (int)(-30 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-5 * Assets.isoGridCoeff),(int)( -4 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            //для прямоугольника -52
            offset.SetValue((int)(-10 * Assets.isoGridCoeff), (int)(-30 * Assets.isoGridCoeff));
        else
            //для прямоугольника -78
            ResetOffset();
    }

    @Override
    public void Update()
    {

    }
}
