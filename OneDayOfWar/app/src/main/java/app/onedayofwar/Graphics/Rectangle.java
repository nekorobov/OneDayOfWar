package app.onedayofwar.Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Slava on 15.03.2015.
 */
public class Rectangle
{
    float[] matrix;
    private int width;
    private int height;
    private boolean isFilled;

    private FloatBuffer vertexBuffer;

    private final int program;
    private int positionHandle;
    private int matrixMVPHandle;

    private final int vertexCount = 8 / COORDS_PER_VERTEX;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static final int COORDS_PER_VERTEX = 2;

    private float[] spriteCoords;

    private float[] color;

    public Rectangle(float x, float y, int width, int height, int color, boolean isFilled)
    {
        this.isFilled = isFilled;
        spriteCoords = new float[8];
        matrix = new float[16];
        this.color = new float[4];
        vertexBuffer = ByteBuffer.allocateDirect(spriteCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        setShape(x, y, width, height, color, isFilled);

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexRectangle));

        // add the fragment shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentRectangle));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    public Rectangle()
    {
        matrix = new float[16];
        spriteCoords = new float[8];
        this.color = new float[4];
        vertexBuffer = ByteBuffer.allocateDirect(spriteCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexRectangle));

        // add the fragment shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentRectangle));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    public void setShape(float x, float y, int width, int height, int color, boolean isFilled)
    {
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, x, y, 0);

        this.isFilled = isFilled;
        this.height = height;
        this.width = width;

        this.color[0] = ((color & 0xff0000) >> 16) / 255;
        this.color[1] = ((color & 0xff00) >> 8) / 255;
        this.color[2] = (color & 0xff) / 255;
        this.color[3] = 1;

        spriteCoords[0] = -width/2;
        spriteCoords[1] = height/2;
        spriteCoords[2] = -width/2;
        spriteCoords[3] = -height/2;
        spriteCoords[4] = width/2;
        spriteCoords[5] = -height/2;
        spriteCoords[6] = width/2;
        spriteCoords[7] = height/2;

        // initialize vertex byte buffer for shape coordinates
        vertexBuffer.position(0);
        vertexBuffer.put(spriteCoords);
        vertexBuffer.position(0);
    }

    public void Draw(float[] mvpMatrix)
    {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // get handle to shape's transformation matrix
        matrixMVPHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(matrixMVPHandle, 1, false, mvpMatrix, 0);

        // Enable generic vertex attribute array
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, "vColor"), 1, color, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the Rect
        GLES20.glDrawArrays(isFilled ? GLES20.GL_TRIANGLE_FAN : GLES20.GL_LINE_LOOP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
