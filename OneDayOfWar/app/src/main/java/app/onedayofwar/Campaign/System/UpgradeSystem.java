package app.onedayofwar.Campaign.System;

import app.onedayofwar.Campaign.Space.Planet;

/**
 * Created by Никита on 08.04.2015.
 */
public class UpgradeSystem
{
    private Planet planet;
    public UpgradeSystem(Planet planet)
    {
        this.planet = planet;
    }

    public void UpgradeBuild(int i)
    {
        planet.UpgradeBuilding(i);
    }

    public void CreateUnit(int i){planet.CreateUnit(i);}
}
