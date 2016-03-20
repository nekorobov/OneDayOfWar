package app.onedayofwar.Graphics;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayDeque;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import app.onedayofwar.System.GLView;

/**
 * Created by Slava on 08.01.2015.
 */
public class GLRenderer implements GLSurfaceView.Renderer, View.OnTouchListener
{
    private Graphics graphics;
    private ScreenView screenView;
    private GLView glView;

    private long startTime;
    private long sleepTime;
    private float eTime = 0.016f;

    final float[] vpMatrix = new float[16];
    final float[] projectionMatrix = new float[16];

    private final float[] viewMatrix = new float[16];

    private ArrayDeque<MotionEvent> motionEvents;

    private ArrayDeque<ScreenView> screenHistory;

    public GLRenderer(GLView glView)
    {
        this.glView = glView;
        motionEvents = new ArrayDeque<>();
        screenHistory = new ArrayDeque<>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.i("TEST", "SURFACE CREATED");
        graphics = new Graphics(this, glView.getActivity().getAssets());
        glView.LoadAssets(graphics);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.i("TEST", "SURFACE CHANGED");

        GLES20.glViewport(0, 0, width, height);

        Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, 1, -1);

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        GLES20.glClearColor(0f, 0f, 0f, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if(screenView == null)
            return;

        startTime = System.currentTimeMillis();
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        if(!motionEvents.isEmpty())
        {
            screenView.OnTouch(motionEvents.poll());
        }

        screenView.Update(eTime);
        screenView.Draw(graphics);


        sleepTime = 15 - (int)((System.currentTimeMillis() - startTime));
        if(sleepTime > 0)
        {
            try {Thread.sleep(sleepTime);}
            catch (InterruptedException e){}
        }
        eTime = (System.currentTimeMillis() - startTime) / 1000f;
    }

    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void changeScreen(ScreenView screen)
    {
        if(screenView != null)
            screenHistory.add(screenView);
        motionEvents.clear();
        viewMatrix[12] = 0;
        viewMatrix[13] = 0;
        screenView = screen;
        screenView.Initialize(graphics);
    }

    public void GoBack()
    {
        if(screenHistory.isEmpty())
            return;
        viewMatrix[12] = 0;
        viewMatrix[13] = 0;
        screenView = screenHistory.pollLast();
        screenView.Resume();
    }

    public void GoMenu()
    {
        if(screenHistory.isEmpty())
            return;
        viewMatrix[12] = 0;
        viewMatrix[13] = 0;
        screenView = screenHistory.pollFirst();
        screenView.Resume();
        screenHistory.clear();
    }

    public float getCameraX()
    {
        return viewMatrix[12];
    }

    public float getCameraY()
    {
        return viewMatrix[13];
    }

    public void moveCamera(float dx, float dy)
    {
        Matrix.translateM(viewMatrix, 0, dx, dy, 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        motionEvents.add(event);
        return true;
    }
}
