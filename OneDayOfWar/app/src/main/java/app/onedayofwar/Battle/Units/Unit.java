package app.onedayofwar.Battle.Units;

import android.graphics.Color;
import android.graphics.RectF;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Graphics.Animation;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

abstract public class Unit
{
    //region Variables
    public Vector2 offset;
    public boolean isInstalled;
    protected byte zoneID;

    //region Images
    protected Sprite stroke;
    protected Sprite icon;
    protected Sprite image;

    public boolean isSelected;
    protected Vector2 strokeOffset;
    public boolean isRight;
    //endregion

    protected int accuracy;
    protected int power;
    protected int hitPoints;
    protected int reloadTime;
    protected int armor;
    public int reload;

    protected Vector2[] form;
    boolean[] damagedForm;
    byte damagedZones;
    boolean isDead;
    protected boolean isVisible;
    protected RectF bounds;

    private Animation fire;
    //endregion

    //region Constructor
    public Unit(boolean isVisible)
    {
        this.isVisible = isVisible;
        isInstalled = false;
        isRight = false;
        isDead = false;
        isSelected = false;
        if(isVisible)
        {
            strokeOffset = new Vector2();
            offset = new Vector2();

            bounds = new RectF();
            fire = new Animation(Assets.fire, 16, 100, 4, true);
            fire.Scale((float)Assets.isoGridCoeff);
        }

        damagedZones = 0;
        reload = 0;
    }
    //endregion

    //region Abstract Methods
    abstract public void Update();
    abstract public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit);
    abstract protected void ChangeOffset();
    abstract protected void ResetOffset();
    abstract public byte GetZone();
    //endregion

    //region Draw
    public void Draw(Graphics graphics)
    {
        DrawStroke(graphics);

        image.matrix[12] -= offset.x;
        image.matrix[13] -= offset.y;
        graphics.DrawSprite(image);
        image.matrix[12] += offset.x;
        image.matrix[13] += offset.y;

        DrawDamagedZones(graphics);
        if (reload > 0 && !isDead)
            DrawReload(graphics);
    }

    public void DrawReload(Graphics graphics)
    {
        graphics.DrawText("" + reload, Assets.arialFont, image.matrix[12], image.matrix[13], 0, Color.YELLOW, 50);
    }

    public void DrawStroke(Graphics graphics)
    {
        if(isSelected)
        {
             stroke.matrix[12] -= offset.x;
             stroke.matrix[13] -= offset.y;
             graphics.DrawSprite(stroke);
             stroke.matrix[12] += offset.x;
             stroke.matrix[13] += offset.y;
        }
    }

    public void DrawIcon(Graphics graphics)
    {
        graphics.DrawSprite(icon);

    }

    public void DrawDamagedZones(Graphics graphics)
    {
        for(int i = 0; i < form.length; i++)
        {
            if(damagedForm[i])
            {
                fire.setPosition(form[i].x, form[i].y - (int)(10 * Assets.isoGridCoeff));
                graphics.DrawAnimation(fire);
            }
        }

    }
    //endregion

    public void UpdateAnimation(float eTime)
    {
        if(damagedZones != 0)
            fire.Update(eTime);
    }

    public float getIconWidth()
    {
        return icon.getWidth();
    }

    public float getIconHeight()
    {
        return icon.getHeight();
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public float[] getIconMatrix()
    {
        return icon.matrix;
    }

    public void ResetPosition()
    {
        ResetOffset();
        strokeSetYellow();
        isSelected = false;
        image.setPosition(0, -image.getHeight());
        stroke.setPosition(0, -stroke.getHeight());

        if(isRight)
        {
            isRight = false;
            TurnImage();
        }
    }

    public RectF GetIconPosition()
    {
        return new RectF(getIconMatrix()[12] - getIconWidth()/2, getIconMatrix()[13] - getIconHeight()/2, getIconMatrix()[12] + getIconWidth()/2, getIconMatrix()[13] + getIconHeight()/2);
    }

    public RectF GetBounds()
    {
        return bounds;
    }

    public void UpdateBounds()
    {
        bounds.set(getMatrix()[12] - image.getWidth()/2 + (int)(image.getWidth() * 0.1) - offset.x, getMatrix()[13] - image.getHeight()/2 + (int)(image.getHeight() * 0.1) - offset.y, getMatrix()[12] + image.getWidth()/2 - (int)(image.getWidth() * 0.1) - offset.x , getMatrix()[13]  + image.getHeight()/2 - offset.y - (int)(image.getHeight()*0.1));
    }

    protected void InitializeFormArray()
    {
        damagedForm = new boolean[form.length];
        for(int i = 0; i < form.length; i++)
        {
            damagedForm[i] = false;
            form[i] = new Vector2();
            form[i].SetFalse();
        }
    }

    public Vector2[] GetForm()
    {
        return form;
    }

    public void ChangeDirection()
    {
        isRight = !isRight;
        if(isVisible)
        {
            TurnImage();
            ChangeOffset();
        }
    }

    protected void TurnImage()
    {
        image.hFlip();
        stroke.hFlip();
    }

    public void strokeSetYellow()
    {
        stroke.setColorFilter(Color.argb(255,255,255,0));
    }
    public void strokeSetRed()
    {
        stroke.setColorFilter(Color.argb(255,255, 0,0));
    }

    public void CheckPosition(Field field)
    {
        //Если помех для юнита нет
        if(SetForm(field.selectedSocket, field, false))
            //Подсвечиваем желтым
            strokeSetYellow();
        else
            //Подсвечиваем красным
            strokeSetRed();
    }

    public void SetPosition(Vector2 position)
    {
        image.setPosition(position.x, position.y);
        stroke.setPosition(position.x, position.y);
    }

    public void Select()
    {
        isSelected = true;
    }

    public void Deselect()
    {
        isSelected = false;
    }

    public void NextTurn()
    {
        if(reload > 0)
            reload--;
    }

    public boolean IsReloading()
    {
        return reload > 0;
    }

    public void Reload()
    {
        reload = reloadTime + 1;
    }

    public boolean SetDamage(int damage)
    {
        damagedZones++;
        if(isVisible)
            fire.Start();
        if(armor >= damage)
            armor -= damage;
        else
        {
            hitPoints -= damage - armor;
            armor = 0;
        }
        if(hitPoints <= 0)
        {
            if(isVisible)
            image.setColorFilter(Color.RED);
            isDead = true;
            return true;
        }
        else if(damagedZones == form.length)
        {
            if(isVisible)
            image.setColorFilter(Color.RED);
            isDead = true;
        }
        return false;
    }

    public void UpdateDamagedZones(Vector2 damagedZone)
    {
        if(isDead)
        {
            for(int i = 0; i < form.length; i++)
            {
                damagedForm[i] = true;
            }
        }
        else
        {
            for (int i = 0; i < form.length; i++)
            {
                if (form[i].Equals(damagedZone))
                {
                    damagedForm[i] = true;
                    break;
                }
            }
        }
    }

    public int GetPower()
    {
        return power;
    }

    public boolean IsDead()
    {
        return isDead;
    }

    public void ResetReload()
    {
        reload = 0;
        power = power / 2;
    }

    public void IncreaseReload(int i)
    {
        reload += i;
    }
}
