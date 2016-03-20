package app.onedayofwar.Battle.Units.Ground;

import android.opengl.Matrix;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Турель
 * Размер 5
 *
 *           Х
 * Форма    ХХХ
 *           Х
 */
public class Turret extends Unit {

    public Turret(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = new Sprite(Assets.turretImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.turretIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.turretStroke);
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
            //для прямоугольника -76
            ResetPosition();
        }

        form = new Vector2[5];
        InitializeFormArray();

        accuracy = 100;
        power = 10000;
        hitPoints = 1500;
        armor = 1000;
        reloadTime = 4;
    }
    //endregion

    //region Set Form
    @Override
    public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit)
    {
        Vector2 tmp = new Vector2();
        Vector2 startPos = new Vector2(startSocket);
        Vector2[] tmpForm = new Vector2[form.length];
        Vector2 sizes = field.GetSocketsSizes();

        for(int i = 0 ; i < form.length; i++)
            tmpForm[i] = new Vector2();

        Vector2 tmpLocal;

        for(int i = 0; i < 3; i++)
        {
            if(field.IsIso())
            {
                tmp.SetValue(startPos.x - sizes.x * i/2 , startPos.y + sizes.y * i/2);

                if(0.5 * (tmp.x - field.getMatrix()[12]) + field.height/2 + field.getMatrix()[13] - 3 < tmp.y)
                    return false;
            }
            else
            {
                tmp.SetValue(startPos.x, startPos.y + sizes.y * i);

                if (tmp.y >= field.getMatrix()[13] + field.height/2)
                    return false;
            }
            tmpLocal = field.GetLocalSocketCoord(tmp);

            if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                return false;

            tmpForm[i].SetValue(tmp);
        }

        startPos.SetValue(startPos.x - sizes.x, startPos.y);

        for(int i = 3; i < form.length; i++)
        {
            if(field.IsIso())
            {
                tmp.SetValue(startPos.x + sizes.x * (i - 3), startPos.y + sizes.y * (i - 3));

                if (-0.5 * (tmp.x - field.getMatrix()[12]) + field.height/2 + field.getMatrix()[13] - 3 < tmp.y || -0.5 * (tmp.x - field.getMatrix()[12]) + field.getMatrix()[13] - field.height/2 - 3 > tmp.y)
                    return false;
            }
            else
            {
                tmp.SetValue(startPos.x + sizes.x * 2 * (i - 3), startPos.y + sizes.y);

                if (tmp.x < field.getMatrix()[12]  - field.width/2 || tmp.x >= field.getMatrix()[12] + field.width/2)
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
           // if(isVisible && isRight) stroke.horizontalFlip();
        }

        return true;
    }
    //endregion

    @Override
    public byte GetZone()
    {
        return zoneID;
    }

    @Override
    protected void ResetOffset()
    {
        // test (-76,0)
        offset.SetValue ((int)(30 * Assets.isoGridCoeff), (int)(-25 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-4 * Assets.isoGridCoeff),(int)( -4 * Assets.isoGridCoeff));
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
