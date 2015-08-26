package xl;

import org.lwjgl.BufferUtils;
import java.nio.*;
import static game.Constants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class Camera 
{
	private FloatBuffer projection;
	private ShaderProgram program;
	private int offsetID;
	private Vector2f offset;
	
	public Camera(Game game, int width, int height)
	{
		if(game.getShaderProgram().get() == 0)
			System.out.println("Null shaderprogram, cannot initialize camera.");
		
		offset = new Vector2f(0.0f, 0.0f);
		
		program = game.getShaderProgram();
		projection = BufferUtils.createFloatBuffer(16);
		setupProjectionMatrix(width, height);
		offsetID = glGetUniformLocation(program.get(), "offset");
	}
	
	public void translate(float x, float y)
	{
		offset.x += x;
		offset.y += y;
		
		glUniform2f(offsetID, offset.x, offset.y);
	}
	
	public Vector2f getOffset() { return offset; }
	
	//width and height of the program window
	public void setupProjectionMatrix(int width, int height)
	{
		glViewport(0, 0, width, height);
		
		projection.put((float)2/(float)width).put(0f).put(0f).put(0f);
		projection.put(0f).put((float)2/(float)-height).put(0f).put(0f);
		projection.put(0f).put(0f).put(-2f).put(0f);
		projection.put(-1f).put(1f).put(1f).put(1f);
		projection.flip();
		
		int projectionID = glGetUniformLocation(program.get(), "projection");
		glUniformMatrix4fv(projectionID, false, projection);
	}
}