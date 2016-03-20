package app.onedayofwar.Battle.Units.Space;

import android.opengl.Matrix;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 23.03.2015.
 */

/*Корабль без экипажа
Форма ХХХ
 */
public class Bioship extends Unit {
    public Bioship(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = new Sprite(Assets.bioshipImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.sonderIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.sonderStroke);
            stroke.Scale((float)Assets.isoGridCoeff);
        }

        this.zoneID = (byte)zoneID;
        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        if(isVisible)
        {
            ResetPosition();
        }

        form = new Vector2[3];
        InitializeFormArray();

        accuracy = 100;
        power = 7500;
        hitPoints = 1000;
        armor = 500;
        reloadTime = 4;
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

        for(int i = 0; i < form.length; i++)
        {
            if(field.IsIso())
            {
                if (isRight)
                    tmp.SetValue(startSocket.x + sizes.x * i / 2, startSocket.y + sizes.y * i / 2);
                else
                    tmp.SetValue(startSocket.x - sizes.x * i / 2, startSocket.y + sizes.y * i / 2);

                if (-Math.abs(0.5 * (tmp.x- field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 < tmp.y)
                    return false;
            }
            else
            {
                if (isRight)
                    tmp.SetValue(startSocket.x + sizes.x * i, startSocket.y);
                else
                    tmp.SetValue(startSocket.x, startSocket.y + sizes.y * i);

                if (tmp.y >= field.getMatrix()[13] + field.height/2 || tmp.x >= field.getMatrix()[12] + field.width/2)
                    return false;
            }

            tmpLocal = field.GetLocalSocketCoord(tmp);

            if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                return false;

            tmpForm[i].SetValue(tmp);
        }

        if(isInstallUnit)
        {
            for (int i = 0; i < form.length; i++)
                form[i].SetValue(tmpForm[i]);
            //if(isVisible && isRight) stroke.horizontalFlip();
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
        offset.SetValue((int)(30 * Assets.isoGridCoeff), (int)(-24 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-4 * Assets.isoGridCoeff),(int)(-4 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-27 * Assets.isoGridCoeff),(int)(-22 * Assets.isoGridCoeff));
        else
            ResetOffset();
    }

    @Override
    public void Update()
    {

    }
}
