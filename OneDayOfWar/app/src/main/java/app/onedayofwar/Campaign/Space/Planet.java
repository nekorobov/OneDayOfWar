package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.graphics.RectF;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.Graphics.Texture;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 20.02.2015.
 */
public class Planet
{
    public int oil;
    public int nanosteel;
    public int credits;
    private byte[] spaceGuards = {1,0,0,0,0,0};//DROID, AKIRA, DEFAINT, STORM, BIOSHIP, BIRD
    private byte[] groundGuards = {0,0,0,0,0,1}; //ROBOT, IVF, ROCKET, TANK,TURRET,SONDER
    //0-credits; 1-oil; 2-nanosteel; 3-factory; 4-workshop;
    private byte[] buildings = {1, 1, 1, 1, 0};
    //0-credits; 1-oil; 2-nanosteel;
    private int[] capacities;
    private byte size;
    private int radius;
    private Sprite image;
    private Texture texture;
    private boolean isPlanetConquered;
    private RectF touch;
    private RectF planet;
    public Vector2 buildingUpgrade;
    public Vector2 unitGroundUpgrade;
    public Vector2 unitSpaceUpgrade;
    public Vector2 factoryCreation;
    public String upgradeName;

    public Planet(int oil, int nanosteel, int credits, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        this.oil = oil;
        this.nanosteel = nanosteel;
        this.credits = credits;
        this.spaceGuards = spaceGuards.clone();
        this.groundGuards = groundGuards.clone();
        this.buildings = buildings.clone();
        this.size = size;
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
        unitGroundUpgrade = new Vector2();
        unitGroundUpgrade.SetFalse();
        unitSpaceUpgrade = new Vector2();
        unitSpaceUpgrade.SetFalse();
        factoryCreation = new Vector2();
        factoryCreation.SetFalse();
    }

    public Planet()
    {
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
        unitGroundUpgrade = new Vector2();
        unitGroundUpgrade.SetFalse();
        unitSpaceUpgrade = new Vector2();
        unitSpaceUpgrade.SetFalse();
        factoryCreation = new Vector2();
        factoryCreation.SetFalse();
    }

    public void loadToMap(Graphics graphics, String path, Vector2 position)
    {
        texture = graphics.LoadTexture(path);
        image = new Sprite(texture);

        //matrix.Rotate((float)Math.random()*361, 0, 0);

        image.setPosition(position.x, position.y);
        image.Scale(radius * 2f / image.getHeight());
    }

    public byte[] getGroundGuards()
    {
        return groundGuards;
    }

    public byte[] getSpaceGuards(){return spaceGuards;}

    public byte[] getBuildings()
    {
        return buildings;
    }

    public byte getFieldSize()
    {
        return size;
    }

    public boolean Select(Vector2 touchPos)
    {
        touch.set(touchPos.x-2,touchPos.y-2,touchPos.x+2,touchPos.y+2);
        planet.set(getMatrix()[12] - radius, getMatrix()[13] - radius, getMatrix()[12]+radius, getMatrix()[13]+radius);
        return touch.intersect(planet);
    }

    public void Draw(Graphics g)
    {
        g.DrawSprite(image);
        if(isPlanetConquered)
            g.DrawText("+", Assets.arialFont, getMatrix()[12], getMatrix()[13], 0, buildingUpgrade.IsFalse() ? Color.YELLOW : Color.RED , 72);
        else
            g.DrawText("+", Assets.arialFont, getMatrix()[12], getMatrix()[13], 0, buildingUpgrade.IsFalse() ? Color.BLUE : Color.WHITE , 72);

    }

    public void Update()
    {
        //matrix.Rotate(5, 3*radius, 3*radius);
    }

    public void ConquerPlanet()
    {
        isPlanetConquered = true;
    }

    public void AntiConquerPlanet(){isPlanetConquered = false;}


    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public void NextTurn()
    {
        if(!buildingUpgrade.IsFalse())
        {
            buildingUpgrade.y--;
            if(buildingUpgrade.y == 0)
            {
                buildings[(int)buildingUpgrade.x]++;
                buildingUpgrade.SetFalse();
            }
        }
        else
        {
            credits += buildings[1] * 20;
            oil += buildings[1] * 40;
            nanosteel += buildings[1] * 30;
        }
        if(!unitGroundUpgrade.IsFalse())
        {
            unitGroundUpgrade.y--;
            if(unitGroundUpgrade.y == 0)
            {
                groundGuards[(int)unitGroundUpgrade.x]++;
                unitGroundUpgrade.SetFalse();
            }
        }
        else if(!unitSpaceUpgrade.IsFalse())
        {
            unitSpaceUpgrade.y--;
            if(unitSpaceUpgrade.y == 0)
            {
                spaceGuards[(int)unitSpaceUpgrade.x]++;
                unitSpaceUpgrade.SetFalse();
            }
        }
    }

    public boolean IsConquered()
    {
        return isPlanetConquered;
    }

    public void UpgradeBuilding(int building)
    {
        if(!buildingUpgrade.IsFalse())
            return;
        switch (building)
        {
            case 0:
                if(credits >= buildings[0] * 500 && nanosteel >= buildings[0] * 650)
                {
                    credits -= buildings[0] * 500;
                    nanosteel -= buildings[0] * 650;
                    buildingUpgrade.SetValue(0, buildings[0] * 10 + (buildings[0] == 0 ? 5 : 0));
                    upgradeName = "MARKET";
                }
                break;
            case 1:
                if(credits >= buildings[1] * 500 && nanosteel >= buildings[1] * 650)
                {
                    credits -= buildings[1] * 500;
                    nanosteel -= buildings[1] * 650;
                    buildingUpgrade.SetValue(1, buildings[1] * 10 + (buildings[1] == 0 ? 5 : 0));
                    upgradeName = "OIL DRILL";
                }
                break;
            case 2:
                if(credits >= buildings[2] * 500 && nanosteel >= buildings[2] * 650)
                {
                    credits -= buildings[2] * 500;
                    nanosteel -= buildings[2] * 650;
                    buildingUpgrade.SetValue(2, buildings[2] * 10 + (buildings[2] == 0 ? 5 : 0));
                    upgradeName = "NANOSTEEL MINES";
                }
                break;
            case 3:
                if(credits >= buildings[3] * 500 && nanosteel >= buildings[3] * 650)
                {
                    credits -= buildings[3] * 500;
                    nanosteel -= buildings[3] * 650;
                    buildingUpgrade.SetValue(3, buildings[3] * 10 + (buildings[3] == 0 ? 5 : 0));
                    upgradeName = "FACTORY";
                }
                break;
            case 4:
                if(credits >= buildings[4] * 500 && nanosteel >= buildings[4] * 650)
                {
                    credits -= buildings[4] * 500;
                    nanosteel -= buildings[4] * 650;
                    buildingUpgrade.SetValue(4, buildings[4] * 10 + (buildings[4] == 0 ? 5 : 0));
                    upgradeName = "WORKSHOP";
                }
                break;
        }
    }

    public void CreateUnit(int unit)
    {
        if(!unitGroundUpgrade.IsFalse() || !unitSpaceUpgrade.IsFalse())
            return;
        switch (unit)
        {
            case 0: //DROID
                if(credits >= 10 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(0, 1);
                }
                break;
            case 1: //AKIRA
                if(credits >= 10 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(1, 1);
                }
                break;
            case 2: //DEFAINT
                if(credits >= 10 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(2, 1);
                }
                break;
            case 3: //STORM
                if(credits >= 10 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(3, 1);
                }
                break;
            case 4: //BIOSHIP
                if(credits >= 500 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(4, 1);
                }
                break;
            case 5: //BIRD
                if(credits >= 500 && nanosteel >= 10)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitSpaceUpgrade.SetValue(5, 1);
                }
                break;
            case 6: //ROBOT
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(0, 1);
                }
                break;
            case 7:  //IVF
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(1, 1);
                }
                break;
            case 8: //ROCKET
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(2, 1);
                }
                break;
            case 9: //TANK
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(3, 1);
                }
                break;
            case 10: //TURRET
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(4, 1);
                }
                break;
            case 11: //SONDER
                if(credits >= 500 && nanosteel >= 650)
                {
                    credits -= 500;
                    nanosteel -= 650;
                    unitGroundUpgrade.SetValue(5, 1);
                }
                break;
        }
    }

    public void DBLoad(byte Size, byte[] gArmy, byte[] sArmy, byte[] Buildings, int[] Resources, int[] RCapacity, Vector2 factory, Vector2 building)
    {
        this.size = Size;
        groundGuards = gArmy.clone();
        spaceGuards = sArmy.clone();
        buildings = Buildings.clone();
        credits = Resources[0];
        oil = Resources[1];
        nanosteel = Resources[2];
        buildingUpgrade.SetValue(building);
        factoryCreation.SetValue(factory);
        capacities = RCapacity.clone();
    }

    public String getSkin()
    {
        return texture.getPath();
    }

    public boolean isSpaceArmyHere()
    {
        for(int i = 0; i < spaceGuards.length; i++)
        {
            if(spaceGuards[i] != 0)
                return true;
        }
        return false;
    }

    public boolean isGroundArmyHere()
    {
        for(int i = 0; i < groundGuards.length; i++)
        {
            if(groundGuards[i] != 0)
                return true;
        }
        return false;
    }
}
