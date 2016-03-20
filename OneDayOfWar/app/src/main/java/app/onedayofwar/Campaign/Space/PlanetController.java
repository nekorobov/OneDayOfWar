package app.onedayofwar.Campaign.Space;

import android.util.Log;

import java.util.ArrayList;

import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.System.DBController;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;

/**
 * Created by Slava on 23.02.2015.
 */
public class PlanetController
{
    private ArrayList<Planet> planets;
    private Space space;
    private int selectedPlanet;

    public PlanetController(Space space)
    {
        this.space = space;
        planets = new ArrayList<>();
        selectedPlanet = -1;
    }

    public void AddPlanet(int oil, int nanoSteel, int syncoCrystals, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        planets.add(new Planet(oil, nanoSteel, syncoCrystals, spaceGuards, groundGuards, buildings, size));
    }

    public void AddPlanet()
    {
        planets.add(new Planet());
    }

    public void LoadPlanets(Graphics graphics, XMLParser xmlParser)
    {
        //xmlParser.LoadAllPlanets(this);
        for(int i = 0 ; i < 20; i++)
        {
            AddPlanet();
        }

        int rowHeight = space.getHeight() / planets.size();
        int attitude = (int) (space.getScreenHeight() /(4f * rowHeight));
        Vector2 planetPos = new Vector2();
        Vector2 tmpPos = new Vector2();
        int planetR;
        int tmpR;
        int d;

        for (int i = 0; i < planets.size(); i++)
        {
            planets.get(i).setRadius((int) ((Math.random()*(space.getScreenHeight()/4 - space.getScreenHeight()/5) + space.getScreenHeight()/5) / 2f));

            planetR = planets.get(i).getRadius();

            while (true)
            {
                planetPos.SetValue((int) (Math.random() * (space.getWidth() - planetR)), rowHeight * i);
                //Log.i("LOADING WHILE", "planetPos " + i + " : " + planetPos.x + " ; " + planetPos.y);

                for (int j = 1; j <= attitude; j++)
                {
                    if (i - j >= 0)
                    {
                        tmpPos.SetValue(planets.get(i - j).getMatrix()[12], planets.get(i - j).getMatrix()[13]);
                        tmpR = planets.get(i - j).getRadius();

                        d = (int)Math.sqrt(Math.pow((planetR + planetPos.y) - (tmpPos.y + tmpR), 2) + Math.pow((planetR + planetPos.x) - (tmpPos.x + tmpR), 2));

                        /*Log.i("LOADING", "attitude: " + attitude);
                        Log.i("LOADING", "j: " + j);
                        Log.i("LOADING", "i: " + i);
                        Log.i("LOADING", "planetPos(i-j): " + tmpPos.x + " ; " + tmpPos.y);
                        Log.i("LOADING", "planetPos(i): " + planetPos.x + " ; " + planetPos.y);
                        Log.i("LOADING", "left: " + d);
                        Log.i("LOADING", "right: " + (tmpR + planetR + (int)(0.3f * space.getScreenWidth())));*/

                        if (d <= tmpR + planetR + (int)(Math.random()*51/100 * space.getScreenWidth()))
                        {
                            planetPos.SetFalse();
                            break;
                        }
                    }
                }
                if (!planetPos.IsFalse())
                {
                    Log.i("LOADING", "planetPos final " +  i  +" : " + planetPos.x + " ; " + planetPos.y);
                    planets.get(i).loadToMap(graphics, "campaign/space/planet.png",  planetPos);
                    break;
                }

            }
        }

        Log.i("LOAD", planets.size() + " PLANETS LOADED");
    }

    public void LoadPlanets(Graphics graphics, DBController dbController)
    {
        dbController.LoadPlanets(graphics, planets);
    }

    public void SelectPlanet(Vector2 touchPos)
    {
        for(int i = 0; i < planets.size(); i++)
        {
            if(planets.get(i).Select(touchPos))
            {
                selectedPlanet = i;
                Log.i("SELECTION", "PLANET " + i + " SELECTED");
                break;
            }
        }
    }

    public void DrawPlanets(Graphics g)
    {
        for(Planet planet : planets)
        {
            planet.Draw(g);
        }
    }

    public void UpdatePlanets()
    {
    }

    public void NextTurn()
    {
        for(int i = 0; i < planets.size(); i++)
        {
            planets.get(i).NextTurn();
        }
    }

    public Planet getSelectedPlanet()
    {
        return selectedPlanet != -1 ? planets.get(selectedPlanet) : null;
    }

    public boolean isPlanetSelected()
    {
        return selectedPlanet != -1;
    }

    public Planet getPlanet(int num){return planets.get(num);} //Новое

    public int getSelectedPlanetNum(){return selectedPlanet;}

    public void doSelectedPlanetFalse(){selectedPlanet = -1;} //Новое

    public ArrayList<Planet> getPlanets(){return planets;}

}
