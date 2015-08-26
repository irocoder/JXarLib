package xl;

import org.lwjgl.opengl.*;

import java.nio.*;
import java.util.concurrent.TimeUnit;

import static game.Constants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public abstract class Game 
{
	private ShaderProgram shaderProgram;
	private SpriteBatch spriteBatch;
	private Camera camera;
	private long window;
	
	public static final long NSPS = 1000000000; //nanoseconds per second
	public static final int MAX_FPS = 60;
	protected int fps;
	private long loopBegin = 0, loopEnd = 0, nextLoopTime = 0;
	protected int width;
	protected int height;
	
	public Game(String title, int width, int height)
	{
		//setup GLFW for context creation
		if(glfwInit() == 0)
		{
			System.out.println("GLFW INIT FAILURE");
		}
				
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
				
		window = glfwCreateWindow(width, height, "Village Simulation", NULL, NULL);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
						
		GLContext.createFromCurrent();
				
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		this.width = width;
		this.height = height;
		
		shaderProgram = new ShaderProgram
				(this.getClass().getResource("../shader/vertex.glsl"), 
				this.getClass().getResource("../shader/fragment.glsl"));
		
		shaderProgram.bind();
				
		spriteBatch = new SpriteBatch(10000); // I don't think we are going to be drawing 
		//10,000 sprites but just in case
		
		camera = new Camera(this, width, height);
		
		init();
	}
	
	public void start()
	{
		/*you will have to use GL_FALSE for things like this
		stupid Java won't let me implicitly convert from int to boolean
		RAEG.jpg*/
		
		while(glfwWindowShouldClose(window) == GL_FALSE)
		{
			glClear(GL_COLOR_BUFFER_BIT);
					
			//limit updates to 60 per second
			if(System.nanoTime() >= nextLoopTime)
			{
				loopBegin = System.nanoTime();
				nextLoopTime = loopBegin + (NSPS / MAX_FPS);
				
				checkInput();
				update();
				draw();
				
				glfwPollEvents();
				glfwSwapBuffers(window);
								
				loopEnd = System.nanoTime();
				
				if(loopEnd - loopBegin > NSPS / MAX_FPS)
				{
					fps = (int)(NSPS / (double)(loopEnd - loopBegin));				
				}
				else
				{
					fps = MAX_FPS;
				}
			}
			else
			{
				try { TimeUnit.NANOSECONDS.sleep(nextLoopTime - System.nanoTime()); }
				catch (InterruptedException ie) { ie.printStackTrace(); System.exit(0); }
			}
		}
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void checkInput();
	public abstract void draw();
	
	public ShaderProgram getShaderProgram() { return shaderProgram; }
	protected long getWindow() { return window; }
	protected SpriteBatch getSpriteBatch() { return spriteBatch; }
	protected Camera getCamera() { return camera; }
}
