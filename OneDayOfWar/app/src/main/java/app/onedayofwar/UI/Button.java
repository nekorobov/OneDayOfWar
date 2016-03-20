package app.onedayofwar.UI;

import android.graphics.Color;
import android.graphics.RectF;
import android.opengl.Matrix;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.Graphics.Texture;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button
{
    public int width;
    public int height;
    private RectF rect;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;
    private boolean isVisible;
    private Sprite image;

    public Button(Texture texture, float x, float y, boolean isAnimated)
    {
        image = new Sprite(texture);
        image.setPosition(x, y);
        width = image.getWidth();
        height = image.getHeight();
        rect = new RectF(x - width/2, y - height/2, x + width/2, y + height/2);
        isClicked = false;
        isLocked = false;
        isVisible = true;
        this.isAnimated = isAnimated;
    }

    public void Draw(Graphics graphics)
    {
        if(isVisible)
        {
            graphics.DrawStaticSprite(image);
            /*if (isClicked && isAnimated)
                g.drawSprite(image, rect.left + 5, rect.top + 5, rect.width() - 5, rect.height() - 5, 0, 0, rect.width(), rect.height());
            else
                g.drawSprite(image, x, y);*/

        }
    }

    public void Flip()
    {
        image.Scale(-1, 1);
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked && isVisible)
        {
            rect.set(image.matrix[12] - width/2, image.matrix[13] - height/2, image.matrix[12] + width/2, image.matrix[13] + height/2);

            if (touchPos.x > rect.left - 5 && touchPos.x < rect.right + 5 && touchPos.y > rect.top - 5 && touchPos.y < rect.bottom + 5)
                isClicked = true;
            else
                isClicked = false;
        }
    }

    public void Reset()
    {
        isClicked = false;
    }

    public boolean IsClicked()
    {
        return isClicked;
    }

    public void Lock()
    {
        isLocked = true;
        image.setColorFilter(Color.argb(255, 85, 85, 85));
    }

    public void Unlock()
    {
        isLocked = false;
        image.removeColorFilter();
    }

    public void SetVisible()
    {
        isVisible = true;
    }

    public void SetInvisible()
    {
        isVisible = false;
    }

    public void SetPosition(float x, float y)
    {
        image.setPosition(x, y);
    }

    public void Scale(float s)
    {
        image.Scale(s);
        width = image.getWidth();
        height = image.getHeight();
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

}
