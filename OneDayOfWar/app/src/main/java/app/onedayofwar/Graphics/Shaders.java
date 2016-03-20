package app.onedayofwar.Graphics;

/**
 * Created by Slava on 15.03.2015.
 */
public class Shaders
{
    public static final String vertexRectangle =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String fragmentRectangle =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public static final String vertexSprite =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";

    public static final String fragmentSprite =
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform vec4 vColor;" +
                    "uniform int useColorFilter;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    "if(useColorFilter == 1){" +
                    "gl_FragColor = vColor * texture2D( s_texture, v_texCoord);" +
                    "}if(useColorFilter == 0){" +
                    " gl_FragColor = texture2D(s_texture, v_texCoord);}" +
                    "}";

    public static final String vertexAnimation =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "attribute vec4 vPosition;" +
                    "uniform mat4 texMat;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vPosition;" +
                    "v_texCoord = (texMat * vec4(a_texCoord, 0.0, 1.0)).xy;" +
                    "}";
}
