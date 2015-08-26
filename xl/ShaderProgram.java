package xl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ShaderProgram
{
    private int program;
    protected URL vShaderPath;
    protected URL fShaderPath;
    private int vShader;
    private int fShader;

    public ShaderProgram(URL vsPath, URL fsPath)
    {
        program = glCreateProgram();

        vShaderPath = vsPath;
        fShaderPath = fsPath;

        init();
    }

    //reads shader file from filePath
    public String readShader(URL filePath) throws IOException
    {
    	
        try(InputStream in = filePath.openStream();
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr))
        {
            StringBuilder str = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
            {
                str.append(line).append("\n");
            }

            return str.toString();
        }
    }

    public void init()
    {
    	//create and compile shaders
        try
        {
            vShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vShader, readShader(vShaderPath));
            glCompileShader(vShader);

            if(glGetShaderi(vShader, GL_COMPILE_STATUS) == GL_FALSE)
                throw new RuntimeException("ERROR: Vertex shader failed to compile - \n" +
                        glGetShaderInfoLog(vShader, GL_INFO_LOG_LENGTH));

            fShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fShader, readShader(fShaderPath));
            glCompileShader(fShader);

            if(glGetShaderi(fShader, GL_COMPILE_STATUS) == GL_FALSE)
                throw new RuntimeException("ERROR: Fragment shader failed to compile - \n" +
                        glGetShaderInfoLog(fShader, GL_INFO_LOG_LENGTH));
        }
        catch(IOException e) { e.printStackTrace(); System.exit(0); }

        glAttachShader(program, vShader);
        glAttachShader(program, fShader);

        glLinkProgram(program);

        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("ERROR: Failure to link program - \n" +
                    glGetProgramInfoLog(program, GL_INFO_LOG_LENGTH));

        glDetachShader(program, vShader);
        glDetachShader(program, fShader);

        glDeleteShader(vShader);
        glDeleteShader(fShader);
    }

    public int get()
    {
        return program;
    }

    public void bind()
    {
        glUseProgram(program);
    }

    public void unbind()
    {
        glUseProgram(0);
    }
}
