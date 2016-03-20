package app.onedayofwar.Campaign.System;

import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.System.DBController;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameView implements ScreenView
{
    private Space space;
    private DBController dbController;
    private boolean isNewGame;

    private GLView glView;
    private int turns;
    public Vector2 currentCamera;



    public GameView(GLView glView, DBController dbController, boolean isNewGame)
    {
        this.glView = glView;
        this.dbController = dbController;
        this.isNewGame = isNewGame;
    }

    public void Initialize(Graphics graphics)
    {
        turns = 0;
        space = new Space(glView.getActivity(), this);
        space.Initialize(graphics);

        currentCamera = new Vector2();
    }

    public void OnTouch(MotionEvent event)
    {
        space.onTouch(event);
    }

    @Override
    public void Resume()
    {
        glView.getActivity().gameState = MainActivity.GameState.CAMPAIGN;
        glView.moveCamera(currentCamera.x, currentCamera.y);
        Log.i("CAMERA", ""+currentCamera.x+"|"+currentCamera.y);
    }


    public void Update(float eTime)
    {
        space.Update(eTime);
    }

    public void MoveCamera(Vector2 velocity)
    {
        if(glView.getCameraX() + velocity.x > Assets.btnRegion.getWidth())
            velocity.x = -glView.getCameraX() + Assets.btnRegion.getWidth();
        else if(glView.getScreenWidth() - (velocity.x + glView.getCameraX()) > space.getWidth())
            velocity.x = -(space.getWidth() + glView.getCameraX() - glView.getScreenWidth());

        if(glView.getCameraY() + velocity.y > 0)
            velocity.y = -glView.getCameraY();
        else if(glView.getScreenHeight() - (velocity.y + glView.getCameraY()) > space.getHeight())
            velocity.y = -(space.getHeight() + glView.getCameraY() - glView.getScreenHeight());

        glView.moveCamera(velocity.x, velocity.y);
    }

    public void Draw(Graphics graphics)
    {
        space.Draw(graphics);
    }

    public GLView getGlView()
    {
        return glView;
    }

    public float getCameraX(){ return glView.getCameraX();}

    public float getCameraY()
    {
        return glView.getCameraY();
    }

    public boolean IsNewGame()
    {
        return isNewGame;
    }

    public DBController getDBController()
    {
        return dbController;
    }
}
