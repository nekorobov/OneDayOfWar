package app.onedayofwar.Battle.System;

import android.graphics.Color;
import android.opengl.GLES20;
import android.view.MotionEvent;

import app.onedayofwar.Battle.Mods.Battle;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.System.GLView;

/**
 * Created by Slava on 31.03.2015.
 */
public class GameOverView implements ScreenView
{
    private GLView glView;
    private boolean isVictory;
    private int reward;
    private boolean cBattle;

    public GameOverView(GLView glView, Battle.BattleState state, int reward, boolean cBattle)
    {
        this.glView = glView;
        this.reward = reward;
        this.cBattle = cBattle;
        isVictory = state == Battle.BattleState.Win;
    }

    @Override
    public void Initialize(Graphics graphics)
    {

    }

    @Override
    public void Update(float eTime)
    {

    }

    @Override
    public void Draw(Graphics graphics)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        graphics.DrawText("YOU " + (isVictory ? "WIN!" : "LOSE!"), Assets.arialFont, glView.getScreenWidth()/2, glView.getScreenHeight()/2, 0, Color.GREEN, 72);
        graphics.DrawText("Reward: " + reward, Assets.arialFont, glView.getScreenWidth()/2, glView.getScreenHeight()/2 + 80, 0, Color.GREEN, 72);
    }

    @Override
    public void OnTouch(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(cBattle)
            {
                glView.goBack();
                glView.goBack();
            }
            else
            {
                glView.getActivity().ResetBTController();
                glView.gotoMainMenu();
            }
        }
    }

    @Override
    public void Resume()
    {

    }
}
