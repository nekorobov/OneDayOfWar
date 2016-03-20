package app.onedayofwar.Graphics;

import java.util.ArrayList;

import app.onedayofwar.System.XMLParser;

/**
 * Created by Slava on 24.03.2015.
 */
public class TextFont
{
    private ArrayList<Glyph> glyphs;
    private Sprite sprite;
    private static final int TEXTURE_SIZE = 72;

    public TextFont(Texture texture, String fileName, XMLParser parser)
    {
        glyphs = new ArrayList<>();
        sprite = new Sprite(texture);
        parser.LoadFont(fileName, glyphs);
    }

    void DrawText(String text, Graphics graphics, float x, float y, float rightBorder, int color, float size)
    {
        sprite.ResetMatrix();
        sprite.setPosition(x, y + size/2);
        sprite.Scale(size * 1f / TEXTURE_SIZE);
        sprite.setColorFilter(color);
        for(int i = 0; i < text.length(); i++)
        {
            for(int j = 0; j < glyphs.size(); j++)
            {
                if(text.charAt(i) == '\n')
                {
                    sprite.matrix[12] = x;
                    sprite.Move(0, sprite.getHeight());
                    break;
                }
                if(glyphs.get(j).id == (int)text.charAt(i))
                {
                    Glyph glyph = glyphs.get(j);
                    sprite.setCoords(glyph.x, glyph.y, glyph.width, glyph.height);
                    if(rightBorder > 0 && sprite.matrix[12] + sprite.getWidth() > rightBorder)
                    {
                        sprite.matrix[12] = x;
                        sprite.Move(0, sprite.getHeight());
                    }
                    sprite.Move(sprite.getWidth()/2, 0);
                    graphics.DrawStaticSprite(sprite);
                    sprite.Move(sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size, 0);
                    break;
                }
            }
        }
    }
}
