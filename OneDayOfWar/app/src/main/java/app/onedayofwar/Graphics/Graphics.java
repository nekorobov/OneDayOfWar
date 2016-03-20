package app.onedayofwar.Graphics;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Slava on 13.03.2015.
 */
public class Graphics
{
    GLRenderer renderer;
    AssetManager assets;
    Rectangle rectangle;
    Line line;
    float[] mvpMatrix;

    public Graphics(GLRenderer renderer, AssetManager assets)
    {
        this.renderer = renderer;
        this.assets = assets;
        mvpMatrix = new float[16];
        rectangle = new Rectangle();
        line  = new Line();
    }

    public Texture LoadTexture(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // We are done using the bitmap so we should recycle it.
        bitmap.recycle();

        return new Texture(fileName, texturenames[0], bitmap.getWidth(), bitmap.getHeight());
    }

    public void DrawRect(float x, float y, int width, int height, int color, boolean isFilled)
    {
        rectangle.setShape(x, y, width, height, color, isFilled);
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, rectangle.matrix, 0);
        rectangle.Draw(mvpMatrix);
    }

    public void DrawStaticRect(float x, float y, int width, int height, int color, boolean isFilled)
    {
        rectangle.setShape(x,y,width,height,color,isFilled);
        Matrix.multiplyMM(mvpMatrix, 0, renderer.projectionMatrix, 0, rectangle.matrix, 0);
        rectangle.Draw(mvpMatrix);
    }

    public void DrawLine(float xb, float yb, float xe, float ye, int color)
    {
        line.setShape(xb, yb, xe, ye, color);
        line.Draw(renderer.vpMatrix);
    }

    public void DrawSprite(Sprite sprite)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, sprite.matrix, 0);
        sprite.Draw(mvpMatrix);
    }

    public void DrawAnimation(Animation animation)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, animation.matrix, 0);
        animation.Draw(mvpMatrix);
    }

    public void DrawParallaxSprite(Sprite sprite, float spaceVelocityCoef)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, sprite.matrix, 0);
        mvpMatrix[12] *= spaceVelocityCoef; mvpMatrix[13] *= spaceVelocityCoef;
        sprite.Draw(mvpMatrix);
    }

    public void DrawStaticSprite(Sprite sprite)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.projectionMatrix, 0, sprite.matrix, 0);
        sprite.Draw(mvpMatrix);
    }

    public void DrawText(String text, TextFont font, float x, float y, float rightBorder, int color, float size)
    {
        font.DrawText(text, this, x, y, rightBorder, color, size);
    }

}
