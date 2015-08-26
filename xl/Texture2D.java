package xl;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
 
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
 
public class Texture2D
{
    private int id;
    private int width;
    private int height;
 
    public Texture2D(URL filePath)
    {
        this(filePath, GL_NEAREST, GL_REPEAT);
    }
 
    public Texture2D(URL filePath, int filter, int wrap)
    {
        this(filePath, filter, filter, wrap);
    }
 
    public Texture2D(URL filePath, int minFilter, int magFilter, int wrap)
    {
        glEnable(GL_TEXTURE_2D);
        load(filePath);
 
        bind();
        setFilters(minFilter, magFilter);
        setWrap(wrap);
     }
 
    public void setWrap(int wrap)
    {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    }
 
    public void setFilters(int minFilter, int magFilter)
    {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
    }
 
    public void upload(ByteBuffer data)
    {
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }
 
    public void load(URL filePath)
    {
        try(InputStream in = filePath.openStream())
        {
            PNGDecoder decoder = new PNGDecoder(in);
 
            ByteBuffer data = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(data, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
 
            width = decoder.getWidth();
            height = decoder.getHeight();
 
            data.flip();
 
            id = glGenTextures();
 
            bind();
            upload(data);
 
        }   catch(Exception e) { e.printStackTrace(); System.exit(0); }
    }
 
    public void bind()
    {
        if(!active())
        {
            throw new IllegalStateException("CANNOT BIND NON-ACTIVE TEXTURE");
        }
 
        glBindTexture(GL_TEXTURE_2D, id);
    }
 
    public void unbind()
    {
        if(!active())
        {
            throw new IllegalStateException("CANNOT UNBIND NON-ACTIVE TEXTURE");
        }
 
        glBindTexture(GL_TEXTURE_2D, 0);
    }
 
    public void delete()
    {
        if(active())
        {
            glDeleteTextures(id);
            id = 0;
        }
    }
    
    public boolean active() { return id != 0; }
    public int getID() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
