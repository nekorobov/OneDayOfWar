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
public class R2D2 extends Unit {

    public R2D2(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {

            image = new Sprite(Assets.r2d2Image);
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

        form = new Vector2[1];
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
        form[0].SetValue(startSocket.x, startSocket.y);
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
