package app.onedayofwar.Battle.BattleElements;

import android.graphics.Color;

import app.onedayofwar.Battle.Bonus.ForBonusEnemy;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Animation;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

public class Field
{
    //region Variables
    private Sprite sprite;
    private Sprite signSprite;

    public int width;
    public int height;
    public int socketSizeX;
    public int socketSizeY;
    public int size;
    public Vector2 selectedSocket;
    public Vector2 globalSocketCoord;
    public Vector2 localSocketCoord;

    public Animation explodeAnimation;

    byte[][] fieldInfo;
    byte[][] shots;

    private boolean isIso;
    private int color;

    //endregion

    //region Constructor
    public Field(int x, int y, int size, boolean isIso)
    {
        this.size = size;
        this.isIso = isIso;

        sprite = new Sprite(isIso ? Assets.gridIso : Assets.grid);
        sprite.Move(x, y);
        sprite.Scale(isIso ? (float)Assets.isoGridCoeff : (float)Assets.gridCoeff);

        signSprite = new Sprite(Assets.signMiss);
        signSprite.Scale(isIso ? (float)Assets.isoGridCoeff : (float)Assets.gridCoeff);

        Initialize();
    }
    //endregion

    //region Initialization
    private void Initialize()
    {
        width = sprite.getWidth();
        height = sprite.getHeight();

        socketSizeX = width / size;
        socketSizeY = height / size;

        fieldInfo = new byte[size][size];
        shots = new byte[size][size];

        InitFieldInfo();

        selectedSocket = new Vector2(-1,-1);

        globalSocketCoord = new Vector2();
        localSocketCoord = new Vector2();

        color = Color.RED;

        if(isIso)
        {
            explodeAnimation = new Animation(Assets.explosion, 24, 100, 0, false);
            explodeAnimation.Scale((float)Assets.isoGridCoeff);
        }
    }
    //endregion

    //region
    public void UpdateAnimation(float eTime)
    {
        explodeAnimation.Update(eTime);
    }
    //endregion

    //region Draw
    /**
     * производит отрисовку поля
     * @param
     */
    public void Draw(Graphics g)
    {
        g.DrawSprite(sprite);
        DrawFieldInfo(g);
        if(!isIso)
        {
            DrawSelectedSocket(g);
        }
    }

    public void setShot(int x, int y, byte value)
    {
        if(x >= 0 && y >= 0 && x < size && y < size)
            shots[y][x] = value;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public float[] getMatrix()
    {
        return sprite.matrix;
    }

    /**
     * Отрисовывает выделенную ячейку
     * @param
     */
    private void DrawSelectedSocket(Graphics g)
    {
        if (!selectedSocket.IsFalse())
           g.DrawRect(selectedSocket.x + socketSizeX/2, selectedSocket.y + socketSizeY/2, socketSizeX, socketSizeY, Color.argb(255, 0 , 255, 0), false);// g.drawPath(selectedSocketForm, selectedSocketPaint.getColor());
    }
    //endregion

    public void Move()
    {
        if(getMatrix()[12] < 0)
            getMatrix()[12] += 3*getWidth();
        else
            getMatrix()[12] -= 3*getWidth();
        selectedSocket.SetFalse();
    }
    //endregion

    //region FieldInfo Methods
    /**
     * Заполнение начальными значениями информации о массиве, а так же инициализация картинок знаков на поле
     */
    public void InitFieldInfo()
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                fieldInfo[i][j] = -1;
                shots[i][j] = 0;
            }
        }

    }

    public int[][] getSquare()
    {
        int tmp[][] = new int[3][3];
        int x, y;
        x = (int) ForBonusEnemy.socket.x;
        y = (int)ForBonusEnemy.socket.y;
        if(x - 1 >= 0 && y - 1 >=0)
        {
            if (GetFieldInfo()[y - 1][x - 1] >= 0)
                tmp[0][0] = 100;
        }
        else
            tmp[0][0] = -100;
        if(y - 1 >=0)
        {
            if(GetFieldInfo()[y - 1][x] >= 0)
                tmp[0][1] = 100;
        }
        else
            tmp[0][1] = -100;
        if(x + 1 < size && y - 1 >= 0)
        {
            if(GetFieldInfo()[y -1][x + 1]>=0)
                tmp[0][2] = 100;
        }
        else
            tmp[0][2] = -100;
        if(x - 1 >= 0)
        {
            if (GetFieldInfo()[y][x - 1] >= 0)
                tmp[1][0] = 100;
        }
        else
            tmp[1][0] = -100;
        if(GetFieldInfo()[y][x] >= 0)
            tmp[1][1] = 100;
        if(x + 1 < size)
        {
            if(GetFieldInfo()[y][x + 1] >= 0)
                tmp[1][2] = 100;
        }
        else
            tmp[1][2] = -100;
        if(x - 1 >= 0 && y + 1 < size)
        {
            if (GetFieldInfo()[y + 1][x - 1] >= 0)
                tmp[2][0] = 100;
        }
        else
            tmp[2][0] = -100;
        if(y + 1 < size)
        {
            if(GetFieldInfo()[y + 1][x] >= 0)
                tmp[2][1] = 100;
        }
        else
            tmp[2][1] = -100;
        if(x + 1 < size && y + 1 < size)
        {
            if (GetFieldInfo()[y + 1][x + 1] >= 0)
                tmp[2][2] = 100;
        }
        else
            tmp[2][2] = -100;
        return tmp;
    }

    public byte GetSelectedSocketInfo()
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        if(localSocketCoord.IsNegative(false))
            return -1;
        return fieldInfo[(int)localSocketCoord.y][(int)localSocketCoord.x];
    }

    public byte GetSelectedSocketShot()
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        return shots[(int)localSocketCoord.y][(int)localSocketCoord.x];
    }

    public boolean IsIso()
    {
        return isIso;
    }

    public byte[][] GetFieldInfo()
    {
        return fieldInfo;
    }

    public byte[][] GetShots()
    {
        return shots;
    }

    public void SetShot(boolean isGoodShot)
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        if(isGoodShot)
            shots[(int)localSocketCoord.y][(int)localSocketCoord.x] = 2;
        else
            shots[(int)localSocketCoord.y][(int)localSocketCoord.x] = 1;
    }

    public void SetFlag()
    {
        if(!selectedSocket.IsFalse())
        {
            if(GetSelectedSocketShot() == 0)
            {
                localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
                shots[(int)localSocketCoord.y][(int)localSocketCoord.x] = 3;
            }
            else if(GetSelectedSocketShot() == 3)
            {
                localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
                shots[(int)localSocketCoord.y][(int)localSocketCoord.x] = 0;
            }
        }
    }

    /**
     * Поместить юнит, который прошел все проверки на поле
     * @param form
     * @param unitID
     */
    public void PlaceUnit(Vector2[] form, int unitID)
    {
        for(int i = 0; i < form.length; i++)
        {
            //получаем локальные координаты
            localSocketCoord.SetValue(GetLocalSocketCoord(form[i]));

            fieldInfo[(int)localSocketCoord.y][(int)localSocketCoord.x] = (byte)unitID;

            SetRestrictedArea(localSocketCoord);
        }
    }

    /**
     * Окружить юнит запретной зоной, в которую нельзя ставить другие юниты
     * @param localCoord
     */
    private void SetRestrictedArea(Vector2 localCoord)
    {
        // Расчеты ведутся в локальных координатах
        for(int i = 0; i < 3; i++)
        {
            //проверка на выход за границу массива
            if(localCoord.x - 1 + i >= 0 && localCoord.x - 1 + i < size)
            {
                //проверка на выход за границу массива
                if (localCoord.y - 1 >= 0)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int)localCoord.y - 1][(int)localCoord.x - 1 + i] < 0)
                        fieldInfo[(int)localCoord.y - 1][(int)localCoord.x - 1 + i]--;
                }
                //проверка на выход за границу массива
                if (localCoord.y + 1 < size)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int)localCoord.y + 1][(int)localCoord.x - 1 + i] < 0)
                        fieldInfo[(int)localCoord.y + 1][(int)localCoord.x - 1 + i]--;
                }
            }
            //проверка на выхо
            // д за границу массива
            if(localCoord.y - 1 + i >= 0 && localCoord.y - 1 + i < size)
            {
                //проверка на выход за границу массива
                if (localCoord.x - 1 >= 0)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int)localCoord.y - 1 + i][(int)localCoord.x - 1] < 0)
                        fieldInfo[(int)localCoord.y - 1 + i][(int)localCoord.x - 1]--;
                }
                //проверка на выход за границу массива
                if (localCoord.x + 1 < size)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int)localCoord.y - 1 + i][(int)localCoord.x + 1] < 0)
                        fieldInfo[(int)localCoord.y - 1 + i][(int)localCoord.x + 1]--;
                }
            }
        }
    }

    /**
     * Удаление запретной зоны, в которую нельзя ставить другие юниты
     * @param form
     */
    private void DeleteRestrictedArea(Vector2[] form)
    {
        // Расчеты ведутся в локальных координатах
        for(int k = 0; k < form.length; k++)
        {
            for (int i = 0; i < 3; i++)
            {
                Vector2 tmp = GetLocalSocketCoord(form[k]);

                //проверка на выход за границу массива
                if (tmp.x - 1 + i >= 0 && tmp.x - 1 + i < size)
                {
                    //проверка на выход за границу массива
                    if (tmp.y - 1 >= 0)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int)tmp.y - 1][(int)tmp.x - 1 + i] < -1)
                            fieldInfo[(int)tmp.y - 1][(int)tmp.x - 1 + i]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.y + 1 < size)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int)tmp.y + 1][(int)tmp.x - 1 + i] < -1)
                            fieldInfo[(int)tmp.y + 1][(int)tmp.x - 1 + i]++;
                    }
                }
                //проверка на выход за границу массива
                if (tmp.y - 1 + i >= 0 && tmp.y - 1 + i < size)
                {
                    //проверка на выход за границу массива
                    if (tmp.x - 1 >= 0)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int)tmp.y - 1 + i][(int)tmp.x - 1] < -1)
                            fieldInfo[(int)tmp.y - 1 + i][(int)tmp.x - 1]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.x + 1 < size)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int)tmp.y - 1 + i][(int)tmp.x + 1] < -1)
                            fieldInfo[(int)tmp.y - 1 + i][(int)tmp.x + 1]++;
                    }
                }
            }
        }
    }

    /**
     * Удаляет юнита с поля
     * @param unit
     */
    public void DeleteUnit(Unit unit)
    {
        DeleteRestrictedArea(unit.GetForm());
        for(int i = 0; i < unit.GetForm().length; i++)
        {
            Vector2 tmp = GetLocalSocketCoord(unit.GetForm()[i]);
            fieldInfo[(int)tmp.y][(int)tmp.x] = -1;
        }
    }

    /**
     * Отрисовка информации о поле
     * @param
     */
    public void DrawFieldInfo(Graphics g)
    {
        if(!isIso)
        {
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    localSocketCoord.SetValue(j,i);
                    globalSocketCoord.SetValue(GetGlobalSocketCoord(localSocketCoord));

                    /*testTextPaint.setARGB(255,0,0,255);

                    if(fieldInfo[i][j] >=0)
                    {
                        testTextPaint.setARGB(255,255,0,0);
                    }
                    if(fieldInfo[i][j] == -1)
                    {
                        testTextPaint.setARGB(255,250,240,20);
                    }
                    g.drawText("" + fieldInfo[i][j], 15, globalSocketCoord.x + 5, globalSocketCoord.y + 25, testTextPaint.getColor());*/

                    signSprite.setPosition(globalSocketCoord.x + socketSizeX/2, globalSocketCoord.y + socketSizeY/2);

                    switch (shots[i][j])
                    {
                        case 1:
                            signSprite.setTexture(Assets.signMiss);
                            g.DrawSprite(signSprite);
                            break;
                        case 2:
                            signSprite.setTexture(Assets.signHit);
                            g.DrawSprite(signSprite);
                            break;
                        case 3:
                            signSprite.setTexture(Assets.signFlag);
                            g.DrawSprite(signSprite);
                            break;
                        case 100:
                            signSprite.setTexture(Assets.signGlare);
                            g.DrawSprite(signSprite);
                            break;
                        case 101:
                            signSprite.setTexture(Assets.signError);
                            g.DrawSprite(signSprite);
                            break;
                    }
                }
            }
        }

        //region TEST
        else
        {
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    if(shots[i][j] == 1)
                    {
                        if(BattlePlayer.armyType.equals("Ground"))
                        {
                            localSocketCoord.SetValue(j,i);
                            globalSocketCoord.SetValue(GetGlobalSocketCoord(localSocketCoord));
                            signSprite.setPosition(globalSocketCoord.x, (int)(globalSocketCoord.y + 2 * Assets.isoGridCoeff + socketSizeY/2));
                            signSprite.setTexture(Assets.signMissIso);
                            g.DrawSprite(signSprite);
                        }
                    }
                }
            }
        }
        //endregion
    }

    public void ClearFieldInfo()
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                fieldInfo[i][j] = -1;
            }
        }
    }

    //endregion

    //region Coord Converters
    /**
     * Возвращает локальные (0,1,2...size-1) координаты ячейки поля по глобальным координатам
     * @param socketGlobalCoord
     * @return
     */
    public Vector2 GetLocalSocketCoord(Vector2 socketGlobalCoord)
    {
        localSocketCoord.SetFalse();
        //если не изометрия
        if(!isIso)
        {
            localSocketCoord.SetValue((int)(socketGlobalCoord.x - getMatrix()[12] + width/2) / socketSizeX, (int)(socketGlobalCoord.y - getMatrix()[13] + height/2) / socketSizeY);
            return localSocketCoord;
        }
        else
        {
            //начинаем перебор прямых с координаты верхней точки ромба
            for (int i = 0; i < size; i++)
            {
                //проверяем через функцию прямой с положительным коэффициэнтом совпадает ли у функции и у ячейки
                if (socketGlobalCoord.y == (0.5 * (socketGlobalCoord.x - getMatrix()[12]) + i * socketSizeY + getMatrix()[13] - height/2))
                {
                    localSocketCoord.y = i;
                }

                //проверяем через функцию прямой с отрицательным коэффициэнтом совпадает ли у функции и у ячейки
                if (socketGlobalCoord.y == (-0.5 * (socketGlobalCoord.x - getMatrix()[12]) + i * socketSizeY + getMatrix()[13] - height/2))
                {
                    localSocketCoord.x = i;
                }

                //если вычислили локальные координаты
                if (localSocketCoord.x != -1 && localSocketCoord.y != -1)
                    break;
            }
        }
        return localSocketCoord;
    }

    /**
     * Возвращает глобальные координаты ячейки поля по локальным координатам
     * @param socketLocalCoord
     * @return
     */
    public Vector2 GetGlobalSocketCoord(Vector2 socketLocalCoord)
    {
        //если не изометрия
        if(!isIso)
        {
            globalSocketCoord.SetValue(getMatrix()[12] - width/2 + socketLocalCoord.x * socketSizeX, getMatrix()[13] - height/2 + socketLocalCoord.y * socketSizeY);
        }
        else
        {
            globalSocketCoord.SetValue(getMatrix()[12] + socketSizeX / 2 * (socketLocalCoord.x - socketLocalCoord.y), getMatrix()[13] - height/2 + socketSizeY / 2 * (socketLocalCoord.y + socketLocalCoord.x));
        }
        return globalSocketCoord;
    }
    public Vector2 GetGlobalSocketCoord(int localX, int localY)
    {
        //если не изометрия
        if(!isIso)
        {
            globalSocketCoord.SetValue(getMatrix()[12] - width/2 + localX * socketSizeX, getMatrix()[13] - height/2 + localY * socketSizeY);
        }
        else
        {
            globalSocketCoord.SetValue(getMatrix()[12] + socketSizeX / 2 * (localX - localY), getMatrix()[13] - height/2 + socketSizeY / 2 * (localY + localX));
        }
        return globalSocketCoord;
    }

    /**
     * Возвращает размеры ячейки поля
     * @return
     */
    public Vector2 GetSocketsSizes()
    {
        return new Vector2(socketSizeX, socketSizeY);
    }
    //endregion

    //region SelectedSocket Methods
    /**
     * Вычисляет координаты ячейки в пределах которой было совершено касание
     * @param touchPos
     * @param socketForm
     */
    public void SelectSocket(Vector2 touchPos, int socketForm)
    {
        //если касание произошло в пределах поля
        if (IsVectorInField(touchPos))
        {
            //если не изометрия
            if (!isIso)
            {
                selectedSocket.x = getMatrix()[12] - width/2 + ((int)(touchPos.x - getMatrix()[12] + width/2) / socketSizeX) * socketSizeX;
                selectedSocket.y = getMatrix()[13] - height/2 + ((int)(touchPos.y - getMatrix()[13] + height/2) / socketSizeY) * socketSizeY;
            }
            else
            {
                end:for (int i = 0; i < size; i++)
                {
                    for (int j = 0; j < size; j++)
                    {
                        //проверяем касание по функции ромба для каждой ячейки
                        if (socketSizeY * Math.abs(touchPos.x + (i - j) * socketSizeX/2 - getMatrix()[12]) + socketSizeX * (touchPos.y - getMatrix()[13] + height/2 - (i + j) * socketSizeY/2) <= socketSizeY * socketSizeX)
                        {
                            selectedSocket.x = getMatrix()[12] - width/2 - i * socketSizeX/2 + width/2 + j * socketSizeX/2;
                            selectedSocket.y = getMatrix()[13] - height/2 + i * socketSizeY/2 + j * socketSizeY/2;
                            break end;
                        }
                    }
                }
            }
        }
        //если удалось выделить ячейку, то придаем ей конкретную форму
        /*if(!selectedSocket.IsFalse())
            SetSelectedSocketForm(socketForm);*/
    }

    /**
     * Проверяет лежит ли вектор в пределах поля
     * @param vector
     * @return
     */
    public boolean IsVectorInField(Vector2 vector)
    {
        if (isIso)
            return Math.abs(0.5 * (vector.x - getMatrix()[12])) + getMatrix()[13] - height / 2 <= vector.y && -Math.abs(0.5 * (vector.x - getMatrix()[12])) + getMatrix()[13] + height / 2 >= vector.y;
        else
            return vector.x > getMatrix()[12] - width / 2 && vector.x < getMatrix()[12] + width / 2 && vector.y > getMatrix()[13] - height / 2 && vector.y < getMatrix()[13] + height / 2;
    }

    /**
     * Придает выделенной ячейке форму в зависимости от передаваемого параметра
     * @param socketForm
     */
    /*private void SetSelectedSocketForm(int socketForm)
    {
        float right;
        float top;
        float left;
        float bottom;

        //Если не изометрия
        if(!isIso)
        {
            switch (socketForm)
            {
                //Прямоугольник размером с ячейку
                case 0:
                    right = selectedSocket.x + socketSizeX;
                    left = selectedSocket.x;
                    top = selectedSocket.y;
                    bottom = selectedSocket.y + socketSizeY;
                    break;
                //Крест
                case 1:
                    //Vertical Rectangle
                    left = selectedSocket.x;

                    top = selectedSocket.y - socketSizeY;
                    if (top < y)
                        top = y;

                    right = selectedSocket.x + socketSizeX;

                    bottom = selectedSocket.y + 2 * socketSizeY;
                    if (bottom > y + height)
                        bottom = y + height;

                    //Horizontal Rectangle
                    float leftH = selectedSocket.x - socketSizeX;
                    if (leftH < x)
                        leftH = x;
                    float topH = selectedSocket.y;
                    float rightH = selectedSocket.x + 2 * socketSizeX;
                    if (rightH > x + width)
                        rightH = x + width;
                    float bottomH = selectedSocket.y + socketSizeY;

                    selectedSocketForm.addRect(leftH, topH, rightH, bottomH, Path.Direction.CW);
                    break;
                //Прямоугольник побольше
                case 2:
                    left = selectedSocket.x - socketSizeX;
                    if (left < x)
                        left = x;

                    top = selectedSocket.y - socketSizeY;
                    if (top < y)
                        top = y;

                    right = selectedSocket.x + 2 * socketSizeX;
                    if (right > x + width)
                        right = x + width;

                    bottom = selectedSocket.y + 2 * socketSizeY;
                    if (bottom > y + height)
                        bottom = y + height;

                    break;

                //Вертикальная линия длиной во все поле
                case 3:
                    left = x;
                    top = selectedSocket.y;
                    right = x + width;
                    bottom = selectedSocket.y + socketSizeY;
                    break;

                //Горизонтальная линия длиной во все поле
                case 4:
                    left = selectedSocket.x;
                    top = y;
                    right = selectedSocket.x + socketSizeX;
                    bottom = y + height;
                    break;

                //По дефолту прямоугольник
                default:
                    right = selectedSocket.x + socketSizeX;
                    left = selectedSocket.x;
                    top = selectedSocket.y;
                    bottom = selectedSocket.y + socketSizeY;
                    break;
            }
            selectedSocketForm.addRect(left, top, right, bottom, Path.Direction.CW);
        }
        else
        {
            switch (socketForm)
            {
                //Прямоугольник размером с ячейку
                case 0:
                    selectedSocketForm.setLastPoint(selectedSocket.x, selectedSocket.y);
                    selectedSocketForm.lineTo(selectedSocket.x + socketSizeX/2, selectedSocket.y + socketSizeY/2);
                    selectedSocketForm.lineTo(selectedSocket.x, selectedSocket.y + socketSizeY);
                    selectedSocketForm.lineTo(selectedSocket.x - socketSizeX/2, selectedSocket.y + socketSizeY/2);
                    selectedSocketForm.lineTo(selectedSocket.x, selectedSocket.y);
                    break;
            }
        }
    }*/
    //endregion

}



