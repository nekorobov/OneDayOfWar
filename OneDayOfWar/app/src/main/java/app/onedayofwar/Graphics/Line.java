package app.onedayofwar.Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Slava on 15.03.2015.
 */
public class Line
{
    private FloatBuffer vertexBuffer;

    private final int program;
    private int positionHandle;
    private int matrixMVPHandle;

    private final int vertexCount = 4 / COORDS_PER_VERTEX;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static final int COORDS_PER_VERTEX = 2;

    private float[] lineCoords;

    private float[] color;

    public Line(float xb, float yb, float xe, float ye, int color)
    {
        lineCoords = new float[4];
        this.color = new float[4];
        vertexBuffer = ByteBuffer.allocateDirect(lineCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        setShape(xb, yb, xe, ye, color);

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexRectangle));

        // add the fragment shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentRectangle));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    public Line()
    {
        lineCoords = new float[4];
        this.color = new float[4];
        vertexBuffer = ByteBuffer.allocateDirect(lineCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexRectangle));

        // add the fragment shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentRectangle));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    public void setShape(float xb, float yb, float xe, float ye, int color)
    {

        this.color[0] = ((color & 0xff0000) >> 16) / 255f;
        this.color[1] = ((color & 0xff00) >> 8) / 255f;
        this.color[2] = (color & 0xff) / 255f;
        this.color[3] = 1;

        lineCoords[0] = xb;
        lineCoords[1] = yb;
        lineCoords[2] = xe;
        lineCoords[3] = ye;

        // initialize vertex byte buffer for shape coordinates
        vertexBuffer.position(0);
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }

    public void Draw(float[] vpMatrix)
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
        GLES20.glUniformMatrix4fv(matrixMVPHandle, 1, false, vpMatrix, 0);

        // Enable generic vertex attribute array
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, "vColor"), 1, color, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the Rect
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
