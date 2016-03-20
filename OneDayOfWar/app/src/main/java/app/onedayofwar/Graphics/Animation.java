package app.onedayofwar.Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import app.onedayofwar.System.Vector2;


/**
 * Created by Slava on 14.02.2015.
 */
public class Animation
{
    private int frames;
    private int currentFrame;
    private int latency;
    private float tick;
    private int width;
    private int height;
    private int start;
    private boolean isLooped;
    private boolean isStart;

    public float[] textureMatrix;
    public float[] matrix;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;

    private final int program;
    private int positionHandle;
    private int texturePositionHandle;

    private static final int vertexCount = 8 / 2;
    private static final int vertexStride = 2 * 4;

    private float[] color;

    private Texture texture;
    private float[] buffer;

    private Vector2 scale;

    public Animation(Texture texture, int frames, int latency, int start, boolean isLooped)
    {
        buffer = new float[8];
        scale = new Vector2(1, 1);
        this.frames = frames;
        this.latency = latency;
        this.width = texture.getWidth()/frames;
        this.height = texture.getHeight();
        this.texture = texture;
        this.isLooped = isLooped;
        this.start = start;
        isStart = false;
        tick = 0;
        currentFrame = 0;

        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, 1, -1, 1);

        textureMatrix = new float[16];
        Matrix.setIdentityM(textureMatrix, 0);

        color = new float[4];

        // initialize vertex byte buffer for shape coordinates
        vertexBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(new float[]{
                -width/2, height/2,
                -width/2, -height/2,
                width/2, -height/2,
                width/2, height/2});
        vertexBuffer.position(0);

        uvBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvBuffer.put(new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f/frames, 1.0f,
                1.0f/frames, 0.0f});
        uvBuffer.position(0);

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexAnimation));

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

    public void Update(float eTime)
    {
        if(isStart)
        {
            if (tick >= latency)
            {
                if (currentFrame == frames - 1)
                {
                    if(!isLooped)
                    {
                        isStart = false;
                    }
                    currentFrame = start;
                    Matrix.setIdentityM(textureMatrix, 0);
                }
                Matrix.translateM(textureMatrix, 0, 1f/frames, 0, 0);
                tick = 0;
                currentFrame++;
            }
            else
            {
                tick += eTime * 10000;
            }
        }
    }

    public void setTexture(Texture texture, int frames, int latency)
    {
        this.texture = texture;
        this.frames = frames;
        this.latency = latency;
        Matrix.setIdentityM(textureMatrix, 0);
        width = texture.getWidth()/frames;
        height = texture.getHeight();

        vertexBuffer.position(0);
        buffer[0] = -width/2; buffer[1] = height/2;
        buffer[2] = -width/2; buffer[3] = -height/2;
        buffer[4] = width/2; buffer[5] = -height/2;
        buffer[6] = width/2; buffer[7] = height/2;
        vertexBuffer.put(buffer);
        vertexBuffer.position(0);

        uvBuffer.position(0);
        buffer[0] = 0f; buffer[1] = 0f;
        buffer[2] = 0f; buffer[3] = 1f;
        buffer[4] = 1f/frames; buffer[5] = 1f;
        buffer[6] = 1f/frames; buffer[7] = 0f;
        uvBuffer.put(buffer);
        uvBuffer.position(0);

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
        GLES20.glEnableVertexAttribArray (texturePositionHandle );

        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "useColorFilter"), color[3] == 0 ? 0 : 1);

        GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, "vColor"), 1, color, 0);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer (texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i (GLES20.glGetUniformLocation (program, "s_texture" ), 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uMVPMatrix"), 1, false, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "texMat"), 1, false, textureMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void Start()
    {
        isStart = true;
    }

    public void setPosition(float x, float y)
    {
        matrix[12] = x;
        matrix[13] = y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean IsStart()
    {
        return isStart;
    }
}
