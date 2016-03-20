package app.onedayofwar.Battle.Units.Ground;

import android.opengl.Matrix;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Экзо-скелет
 * Размер 1х1
 */
public class Robot extends Unit {

    public Robot(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if (isVisible)
        {
            image = new Sprite(Assets.robotImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.robotIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.robotStroke);
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

        form = new Vector2[1];
        InitializeFormArray();

        accuracy = 100;
        power = 2500;
        hitPoints = 500;
        armor = 0;
        reloadTime = 1;
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
                tmp.SetValue(startSocket.x - sizes.x * i/2 , startSocket.y + sizes.y * i/2);
            else
                tmp.SetValue(startSocket.x, startSocket.y);

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
        offset.SetValue((int)(3 * Assets.isoGridCoeff),(int)(0 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-5 * Assets.isoGridCoeff),(int)( -5 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
    }

    @Override
    public void Update()
    {

    }

}
