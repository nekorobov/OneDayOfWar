package app.onedayofwar.Graphics;

/**
 * Created by Slava on 22.03.2015.
 */
public class Texture
{
    private String path;
    private int id;
    private int width;
    private int height;

    public Texture(String path, int id, int width, int height)
    {
        this.path = new String(path);
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
      return width;
    }

    public int getId() {
    return id;
    }

    public String getPath()
    {
        return path;
    }
}
