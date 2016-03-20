package app.onedayofwar.Graphics;

import android.view.MotionEvent;

/**
 * Created by Slava on 14.03.2015.
 */
public interface ScreenView
{
    abstract public void Initialize(Graphics graphics);
    abstract public void Update(float eTime);
    abstract public void Draw(Graphics graphics);
    abstract public void OnTouch(MotionEvent event);
    abstract public void Resume();
}
