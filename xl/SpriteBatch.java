package xl;

import java.nio.*;

import static game.Constants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

//class for fast drawing of textured quads
public class SpriteBatch 
{
	//vertex array object, vertex buffer object, and element buffer object, respectively
	private int vao, vbo, ebo;
	private FloatBuffer vertices;
	private IntBuffer indices;
	private int vertexCount;
	private int size;
	
	//size in quads
	public SpriteBatch(int size)
	{
		//check for existing program
		this.size = size;
		//vertices = BufferUtils.createFloatBuffer(size * 4 * VERTEX_DATA_SIZE); //4 vertices for every quad
		//indices = BufferUtils.createIntBuffer(size * 6); //6 indices for every quad
		vertexCount = 0;
		
		//generate and bind buffers
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		
		
		//allocate the proper amount of space
		glBufferData(GL_ARRAY_BUFFER, VERTEX_DATA_SIZE * 4 * size * SIZEOF_FLOAT, null, GL_STREAM_DRAW);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * size * SIZEOF_INT, null, GL_STREAM_DRAW);

		indices = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY).asIntBuffer();

		//fill the index buffer
		for (int i = 0; i < size; i++)
		{	
			indices.put(i * 6, i * 4);
			indices.put(i * 6 + 1, i * 4 + 1);
			indices.put(i * 6 + 2, i * 4 + 2);
			indices.put(i * 6 + 3, i * 4 + 2);
			indices.put(i * 6 + 4, i * 4 + 3);
			indices.put(i * 6 + 5, i * 4);
		}

		glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
		indices.flip();
		
		//enable vertex attributes such as position, color
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 8 * SIZEOF_FLOAT, 0);//position
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 8 * SIZEOF_FLOAT, 2 * SIZEOF_FLOAT);//color
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * SIZEOF_FLOAT, 6 * SIZEOF_FLOAT);//texture coordinates
	}
	
	public void addVertex(float x, float y, float r, float g, float b, float a, float u, float v)
	{		
		vertices.put(vertexCount * 8, x);
		vertices.put(vertexCount * 8 + 1, y);
		
		vertices.put(vertexCount * 8 + 2, r);
		vertices.put(vertexCount * 8 + 3, g);
		vertices.put(vertexCount * 8 + 4, b);
		vertices.put(vertexCount * 8 + 5, a);
		
		vertices.put(vertexCount * 8 + 6, u);
		vertices.put(vertexCount * 8 + 7, v);
		
		vertexCount += 1;
	}
	
	public void draw(float x, float y, float width, float height)
	{			
		addVertex(x, y, 1f, 1f, 1f, 1f, 0f, 0f);
		addVertex(x + width, y, 1f, 1f, 1f, 1f, 0f, 0f);
		addVertex(x + width, y + height, 1f, 1f, 1f, 1f, 0f, 0f);
		addVertex(x, y + height, 1f, 1f, 1f, 1f, 0f, 0f);
	}
	
	public void draw(Texture2D texture, Rectangle rect, float x, float y)
	{	
		float u1 = (float)rect.getX() / (float)texture.getWidth();
		float v1 = (float)rect.getY() / (float)texture.getHeight();
		float u2 = ((float)rect.getX() + (float)rect.getWidth()) / (float)texture.getWidth();
		float v2 = ((float)rect.getY() + (float)rect.getHeight()) / (float)texture.getHeight();
		
		
		addVertex(x, y, 1f, 1f, 1f, 1f, u1, v1);
		addVertex(x + rect.getWidth(), y, 1f, 1f, 1f, 1f, u2, v1);
		addVertex(x + rect.getWidth(), y + rect.getHeight(), 1f, 1f, 1f, 1f, u2, v2);
		addVertex(x, y + rect.getHeight(), 1f, 1f, 1f, 1f, u1, v2);
	}
	
	public void begin()
	{
		glClear(GL_COLOR_BUFFER_BIT);
		
		vertices = glMapBufferRange(GL_ARRAY_BUFFER, 0, size * 4 * VERTEX_DATA_SIZE * SIZEOF_FLOAT,
				GL_MAP_WRITE_BIT).asFloatBuffer();
	}
	
	public void end()
	{
		if(vertexCount > 0)
			render();
		
		glUnmapBuffer(GL_ARRAY_BUFFER);
	}
	
	public void render()
	{
		//vertices.flip();
		glUnmapBuffer(GL_ARRAY_BUFFER);
		
		glDrawElements(GL_TRIANGLES, vertexCount / 4 * 6, GL_UNSIGNED_INT, 0);

		vertices = glMapBufferRange(GL_ARRAY_BUFFER, 0, size * 4 * VERTEX_DATA_SIZE * SIZEOF_FLOAT,
			GL_MAP_WRITE_BIT).asFloatBuffer();

		vertexCount = 0;
	}
}
