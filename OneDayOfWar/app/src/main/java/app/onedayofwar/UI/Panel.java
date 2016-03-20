package app.onedayofwar.UI;

import android.graphics.Color;
import android.opengl.Matrix;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 26.11.2014.
 */
public class Panel
{
    static public enum Type{UP, DOWN, LEFT, RIGHT}
    public float[] matrix;
    private int beginX;
    private int beginY;
    public Vector2 velocity;
    public int width;
    public int height;
    public boolean isStop;
    public boolean isClose;
    private Type type;
    private Button closeBtn;

    //region Constructor
    public Panel(int x, int y, int width, int height, Type type)
    {
        beginX = x;
        beginY = y;
        this.width = width;
        this.height = height;
        this.type = type;
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, x, y, 0);
       /* if(openType == 3)
            image = BitmapFactory.decodeResource(res,R.drawable.gate_top);
        else if(openType == 2)
            image = BitmapFactory.decodeResource(res,R.drawable.gate_bottom);*/

        Initialize();
    }
    //endregion

    //region Initialization
    public void Initialize()
    {
        SetVelocity();

        if(type == Type.RIGHT)
        {
            closeBtn = new Button(Assets.btnPanelClose, (int) (matrix[12] + Assets.btnPanelClose.getWidth() / 2 * Assets.btnCoeff - width / 2), height / 2, false);
            closeBtn.Scale(Assets.btnCoeff);
        }

        isStop = true;
        isClose = true;
    }
    //endregion

    private void SetVelocity()
    {
        switch (type)
        {
            case LEFT:
                velocity = new Vector2(-15, 0);
                break;
            case UP:
                velocity = new Vector2(0, -1000);
                break;
            case RIGHT:
                velocity = new Vector2(1000, 0);
                break;
            case DOWN:
                velocity = new Vector2(0, 1000);
                break;
        }
    }

    public void Update(float eTime)
    {
        if(!isStop)
        {
            matrix[12] += velocity.x * eTime;
            matrix[13] += velocity.y * eTime;

            if (type == Type.RIGHT)
                closeBtn.getMatrix()[12] += velocity.x * eTime;

            switch (type)
            {
                case LEFT:
                    /*if (Math.abs(matrix[12] - beginX) >= width)
                    {
                         = -width;
                        Matrix.translateM(closeBtn.matrix, 0, -closeBtn.width, 0, 0);
                        velocity.ChangeSign();
                        closeBtn.Flip();
                        isClose = false;
                        isStop = true;

                    }
                    else if (Math.abs(offsetX) <= 0)
                    {
                        offsetX = 0;
                        velocity.ChangeSign();
                        isClose = true;
                        isStop = true;
                    }*/
                break;

                case RIGHT:
                    if (matrix[12] - beginX >= width)
                    {
                        matrix[12] = beginX + width;
                        closeBtn.getMatrix()[12] = beginX + width/2 - closeBtn.width/2;
                        velocity.ChangeSign();
                        closeBtn.Flip();
                        isClose = false;
                        isStop = true;
                    }
                    else if (matrix[12] - beginX <= 0)
                    {
                        matrix[12] = beginX;
                        closeBtn.getMatrix()[12] = beginX - width/2 + closeBtn.width/2;
                        velocity.ChangeSign();
                        isClose = true;
                        isStop = true;
                    }
                break;

                case UP:
                    if (beginY - matrix[13] >= height)
                    {
                        matrix[13] = beginY - height;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = false;
                    }
                    else if (beginY - matrix[13] <= 0)
                    {
                        matrix[13] = beginY;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = true;
                    }
                break;

                case DOWN:
                    if (matrix[13] - beginY >= height)
                    {
                        matrix[13] = beginY + height;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = false;
                    }
                    else if (matrix[13] - beginY <= 0)
                    {
                        matrix[13] = beginY;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = true;
                    }
                break;
            }
        }
    }

    public void Move()
    {
        if(type == Type.RIGHT)
        {
            if (!isClose)
            {
                closeBtn.Flip();
                closeBtn.getMatrix()[12] =  beginX + width/2 + closeBtn.width/2;
            }
        }
        isStop = false;
    }

    public void Draw(Graphics g)
    {
        //if(image == null)
        if(isClose || !isStop)
            g.DrawStaticRect(matrix[12], matrix[13], width, height, Color.BLACK, true);//g.drawRect(rect.left, rect.top, rect.width(), rect.height(), paint.getColor(), true);
        /*else
            canvas.drawBitmap(image, new Rect(0,0,image.getIconWidth(), image.getIconHeight()), new RectF(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height), null);*/
    }

    public void DrawButton(Graphics g)
    {
        closeBtn.Draw(g);
    }

    public boolean IsCloseBtnPressed()
    {
        return closeBtn.IsClicked();
    }

    public void UpdateCloseBtn(Vector2 touchPos)
    {
        closeBtn.Update(touchPos);
    }

    public void ResetCloseBtn()
    {
        closeBtn.Reset();
    }

    public void CloseBtnLock() { closeBtn.Lock(); }

}
