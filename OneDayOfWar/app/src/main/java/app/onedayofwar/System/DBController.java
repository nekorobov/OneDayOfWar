package app.onedayofwar.System;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import app.onedayofwar.Campaign.CharacterControl.Character;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Graphics;

/**
 * Created by Slava on 08.04.2015.
 */
public class DBController
{
    //region Класс для работы с БД

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db)
        {
            Log.i("DB", "create database");
            db.execSQL("CREATE TABLE WORLD(" +
                    "Camera TEXT," +
                    "Planets INT," +
                    "Info INT" +
                    ");");
            db.execSQL("CREATE TABLE PLANETS( " +
                    "ID INT," +
                    "Coords TEXT," +
                    "Radius INT," +
                    "Size INT," +
                    "Skin TEXT," +
                    "GArmy TEXT," +
                    "SArmy TEXT," +
                    "Buildings TEXT," +
                    "Resources TEXT," +
                    "RCapacity TEXT," +
                    "Creation TEXT," +
                    "Info TEXT" +
                    ");");

            db.execSQL("CREATE TABLE AI(" +
                    "Coords TEXT," +
                    "MPoints INT," +
                    "Resources TEXT," +
                    "Info TEXT" +
                    ");");

            db.execSQL("CREATE TABLE PLAYER(" +
                    "Coords TEXT," +
                    "MPoints INT," +
                    "Resources TEXT," +
                    "Info TEXT" +
                    ");");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS WORLD");
            db.execSQL("DROP TABLE IF EXISTS PLAYER");
            db.execSQL("DROP TABLE IF EXISTS AI");
            db.execSQL("DROP TABLE IF EXISTS PLANETS");
            onCreate(db);
        }
    }
    //endregion

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues contentValues;

    private final String BD_NAME = "GSBD";
    private final int VERSION = 2;

    public DBController(Context context)
    {
        dbHelper = new DBHelper(context, BD_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
        contentValues = new ContentValues();
    }

    public boolean IsGameSaved()
    {
        Cursor c = db.query("WORLD", null, null, null, null, null, null);
        int planets;
        if(c.moveToFirst())
        {
            planets = c.getInt(c.getColumnIndex("Planets"));
            c.close();
            c = db.query("PLANETS", null, null, null, null, null, null);
            if(c.getCount() == planets)
            {
                c.close();
                return true;
            }
            else
            {
                c.close();
                return false;
            }
        }
        else
        {
            c.close();
            return false;
        }
    }

    public void LoadWorld(GLView glView)
    {
        Cursor c = db.query("WORLD", null, null, null, null, null, null);
        if (c.moveToFirst())
        {

            String[] coords = c.getString(c.getColumnIndex("Camera")).split("\\|");
            glView.moveCamera(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
            c.close();
        }
    }

    public void LoadPlanets(Graphics graphics, ArrayList<Planet> planets)
    {
        Cursor c = db.query("PLANETS", null, null, null, null, null, null);
        if (c.moveToFirst())
        {
            Vector2 coords = new Vector2();
            byte[] gArmy = new byte[c.getString(c.getColumnIndex("GArmy")).split("\\|").length];
            byte[] sArmy = new byte[c.getString(c.getColumnIndex("SArmy")).split("\\|").length];
            byte[] buildings = new byte[c.getString(c.getColumnIndex("Buildings")).split("\\|").length];
            int[] resources = new int[c.getString(c.getColumnIndex("Resources")).split("\\|").length];
            int[] rCapacity = new int[c.getString(c.getColumnIndex("RCapacity")).split("\\|").length];
            Vector2 building = new Vector2();
            Vector2 factory = new Vector2();
            for(int i = 0; i < c.getCount(); i++)
            {
                planets.add(new Planet());
                Planet planet = planets.get(i);
                String[] coordsS = c.getString(c.getColumnIndex("Coords")).split("\\|");
                coords.SetValue(Float.parseFloat(coordsS[0]), Float.parseFloat(coordsS[1]));

                String[] gArmyS = c.getString(c.getColumnIndex("GArmy")).split("\\|");
                for(int k = 0; k < gArmy.length; k++)
                    gArmy[k] = Byte.parseByte(gArmyS[k]);

                String[] sArmyS = c.getString(c.getColumnIndex("SArmy")).split("\\|");
                for(int k = 0; k < sArmy.length; k++)
                    sArmy[k] = Byte.parseByte(sArmyS[k]);

                String[] buildingsS = c.getString(c.getColumnIndex("Buildings")).split("\\|");
                for(int k = 0; k < buildings.length; k++)
                    buildings[k] = Byte.parseByte(buildingsS[k]);

                String[] resourcesS = c.getString(c.getColumnIndex("Resources")).split("\\|");
                for(int k = 0; k < resources.length; k++)
                    resources[k] = Integer.parseInt(resourcesS[k]);

                String[] rCapacityS = c.getString(c.getColumnIndex("RCapacity")).split("\\|");
                for(int k = 0; k < rCapacity.length; k++)
                    rCapacity[k] = Integer.parseInt(rCapacityS[k]);

                String[] creation = c.getString(c.getColumnIndex("Creation")).split("\\|");
                building.SetValue(Integer.parseInt(creation[0]), Integer.parseInt(creation[1]));
                factory.SetValue(Integer.parseInt(creation[2]), Integer.parseInt(creation[3]));

                planet.setRadius(c.getInt(c.getColumnIndex("Radius")));
                planet.loadToMap(graphics, c.getString(c.getColumnIndex("Skin")), coords);
                planet.DBLoad((byte)c.getInt(c.getColumnIndex("Size")), gArmy, sArmy, buildings, resources, rCapacity, factory, building);
                c.moveToNext();
            }
        }
        c.close();
    }

    public void LoadCharacter(boolean isPlayer, Character character)
    {
        Cursor c = db.query(isPlayer ? "PLAYER" : "AI", null, null, null, null, null, null);
        if (c.moveToFirst())
        {
            String[] coords = c.getString(c.getColumnIndex("Coords")).split("\\|");
            String[] resources = c.getString(c.getColumnIndex("Resources")).split("\\|");
            character.DBLoad(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), c.getInt(c.getColumnIndex("MPoints")), new int[]{Integer.parseInt(resources[0]), Integer.parseInt(resources[1]), Integer.parseInt(resources[2])});
        }
        c.close();
    }

    public void SavePlanets(ArrayList<Planet> planets)
    {
        db.delete("PLANETS", null, null);
        for(int i = 0; i < planets.size(); i++)
        {
            Planet planet = planets.get(i);
            contentValues.clear();
            contentValues.put("ID", i);
            contentValues.put("Coords", planet.getMatrix()[12] + "|" + planet.getMatrix()[13]);
            contentValues.put("Radius", planet.getRadius());
            contentValues.put("Size", planet.getFieldSize());
            contentValues.put("Skin", planet.getSkin());
            contentValues.put("GArmy", bAstS(Arrays.toString(planet.getGroundGuards())));
            contentValues.put("SArmy", bAstS(Arrays.toString(planet.getSpaceGuards())));
            contentValues.put("Buildings", bAstS(Arrays.toString(planet.getBuildings())));
            contentValues.put("Resources", planet.credits + "|" + planet.oil + "|" + planet.nanosteel);
            contentValues.put("RCapacity", planet.credits + "|" + planet.oil + "|" + planet.nanosteel);
            contentValues.put("Creation", (int)planet.buildingUpgrade.x + "|" + (int)planet.buildingUpgrade.y + "|" + (int)planet.factoryCreation.x + "|" + (int)planet.factoryCreation.y);
            contentValues.put("Info", "PlanetTest");
            db.insert("PLANETS", null, contentValues);
        }
    }

    public void SaveCharacter(boolean isPlayer, Character character)
    {
        db.delete(isPlayer ? "PLAYER" : "AI", null, null);
        contentValues.clear();
        contentValues.put("Coords", character.getMatrix()[12] + "|" + character.getMatrix()[13]);
        contentValues.put("MPoints", character.getPointsToMove());
        contentValues.put("Resources", bAstS(Arrays.toString(character.getResources())));
        contentValues.put("Info", "CharacterTest");
        db.insert(isPlayer ? "PLAYER" : "AI", null, contentValues);
    }

    public void SaveWorld(Space space)
    {
        db.delete("WORLD", null, null);
        contentValues.clear();
        contentValues.put("Camera", space.GetCameraX() + "|" + space.GetCameraY());
        contentValues.put("Planets", space.getPlanetController().getPlanets().size());
        contentValues.put("Info", "WorldTest");
        db.insert("WORLD", null, contentValues);
    }

    private String bAstS(String array)
    {
        String replaced = array.replace(", ", "|");
        return replaced.substring(1, replaced.length() - 1);
    }

    public void Delete()
    {
        db.delete("WORLD", null, null);
        db.delete("PLANETS", null, null);
        db.delete("PLAYER", null, null);
        db.delete("AI", null, null);
    }

    public void Close()
    {
        dbHelper.close();
    }
}
