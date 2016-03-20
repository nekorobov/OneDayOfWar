package app.onedayofwar.Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 05.03.2015.
 */
public class Sprite
{
    private int width;
    private int height;

    FloatBuffer vertexBuffer;
    FloatBuffer uvBuffer;

    private final int program;
    private int positionHandle;
    private int texturePositionHandle;

    private float[] buffer;

    private static final int vertexCount = 8 / 2;
    private static final int vertexStride = 2 * 4;

    private float[] color;

    private Texture texture;

    public float[] matrix;

    private Vector2 scale;

    public Sprite(Texture texture)
    {
        buffer = new float[8];

        scale = new Vector2(1, 1);

        color = new float[4];

        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, 1, -1, 1);

        height = texture.getHeight();
        width = texture.getWidth();
        this.texture = texture;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(8 * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(new float[]{
                -width/2, height/2,
                -width/2, -height/2,
                width/2, -height/2,
                width/2, height/2});
        vertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(8 * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f});
        uvBuffer.position(0);

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexSprite));

        // add the fragment shader to program
        GLES20.glAttachShader(program,  GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentSprite));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    public void Move(float x, float y)
    {
        matrix[12] += x;
        matrix[13] += y;
    }

    public void setPosition(float x, float y)
    {
        matrix[12] = x;
        matrix[13] = y;
    }

    public void setCoords(float x, float y, int width, int height)
    {
        uvBuffer.position(0);
        buffer[0] = x / texture.getWidth(); buffer[1] = y / texture.getHeight();
        buffer[2] = x / texture.getWidth(); buffer[3] = y / texture.getHeight() + height * 1f/ texture.getHeight();
        buffer[4] = x / texture.getWidth() + width * 1f/ texture.getWidth(); buffer[5] = y / texture.getHeight()  + height * 1f/ texture.getHeight();
        buffer[6] = x / texture.getWidth() + width * 1f/ texture.getWidth(); buffer[7] = y / texture.getHeight();
        uvBuffer.put(buffer);
        uvBuffer.position(0);

        vertexBuffer.position(0);
        buffer[0] = -width/2; buffer[1] = height/2;
        buffer[2] = -width/2; buffer[3] = -height/2;
        buffer[4] = width/2; buffer[5] = -height/2;
        buffer[6] = width/2; buffer[7] = height/2;
        vertexBuffer.put(buffer);
        vertexBuffer.position(0);

        this.width = (int)(width * scale.x);
        this.height = (int)(height * scale.y);
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        vertexBuffer.position(0);
        buffer[0] = -width/2; buffer[1] = height/2;
        buffer[2] = -width/2; buffer[3] = -height/2;
        buffer[4] = width/2; buffer[5] = -height/2;
        buffer[6] = width/2; buffer[7] = height/2;
        vertexBuffer.put(buffer);
        vertexBuffer.position(0);
        width = (int)(width * scale.x);
        height = (int)(height * scale.y);
    }

    void Draw(float[] mvpMatrix)
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getId());

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Get handle to texture coordinates location
        texturePositionHandle = GLES20.glGetAttribLocation(program, "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray (texturePositionHandle);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer (texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "useColorFilter"), color[3] == 0 ? 0 : 1);

        GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, "vColor"), 1, color, 0);

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i (GLES20.glGetUniformLocation (program, "s_texture" ), 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uMVPMatrix"), 1, false, mvpMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void Scale(float sx, float sy)
    {
        Matrix.scaleM(matrix, 0, sx, sy, 1);
        scale.x *= sx;
        scale.y *= sy;
        width = (int)(width * sx);
        height = (int)(height * sy);
    }

    public void Scale(float s)
    {
        Matrix.scaleM(matrix, 0, s, s, 1);
        scale.x *= s;
        scale.y *= s;
        width = (int)(width * s);
        height = (int)(height * s);
    }

    public void hFlip()
    {
        Matrix.scaleM(matrix, 0, -1, 1, 1);
    }

    public void Rotate(float angle, float x, float y, float z)
    {
        Matrix.rotateM(matrix, 0, angle, x, y, z);
    }

    public void ResetMatrix()
    {
        Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, 1, -1, 1);
        scale.SetValue(1, 1);
    }

    public void setColorFilter(int color)
    {
        this.color[0] = ((color & 0xff0000) >> 16) / 255f;
        this.color[1] = ((color & 0xff00) >> 8) / 255f;
        this.color[2] = (color & 0xff) / 255f;
        this.color[3] = 1;
    }

    public void removeColorFilter()
    {
        color[3] = 0;
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