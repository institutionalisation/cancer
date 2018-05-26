import org.lwjgl.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.joml.*;
import java.lang.Math;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static util.Util.*;
import java.util.*;
import java.util.List;
public abstract class LevelBase {
	public Keyboard keyboard = new Keyboard();
	public Mouse mouse;
	public Window window;
	public Program program;
	public Player player;
	public abstract void close();
	public abstract void onReady();
	public abstract void inContext();
	public List<Model> renderedModels = new ArrayList<Model>();
	// inContext needs to have the GL context
	// ready is put to a new thread
	public void run() { exPrint(()->{
		init();
		inContext();
		new Thread(()->{exPrint(()->{onReady();});}).start();
		renderLoop();
	});}
	public void init() { exPrint(()->{
		GLFWErrorCallback.createPrint(System.err).set();
		// initialize GLFW. most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		window = new Window(500,500,"Alternate Perspective",keyboard);
		glfwSwapInterval(1);
		window.makeContextCurrent();
		GL.createCapabilities();
		program = new Program(
			new Shader("vertex",GL_VERTEX_SHADER),
			new Shader("fragment",GL_FRAGMENT_SHADER));
		program.use();
		mouse = new Mouse(window);
		new Runnable() { final Runnable capture = this;
			public void run() {
				mouse.capture(true);
				keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE, new Runnable() { public void run() {
					mouse.capture(false);
					keyboard.getImmediateKeys().put(GLFW_KEY_ESCAPE,capture);
				}});
			}
		}.run();
		player = new Player(keyboard,mouse);
	});}
	public void renderLoop() { exPrint(()->{
		window.show();
		window.resizeCallbacks.add(new Window.BoundCallback() {
			public void invoke(Window a,Dimension size) {
				int
					height = size.height,
					width = size.width;
				System.out.println("resize");
				final float FOV = (float) Math.toRadians(100f);
				final float Z_NEAR = .1f;
				final float Z_FAR = 100;
				Matrix4f perspectiveMatrix;
				System.out.println("w:"+width+",h:"+height);
				float aspectRatio = 1f*width/height;
				perspectiveMatrix = new Matrix4f().perspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
				glUniformMatrix4fv(program.getUniformLocation("perspective"),false,
					perspectiveMatrix.get(new float[16]));
				glViewport(0,0,width,height);
			}
		});
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		//https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
		Model maze = new Model("maze0","dae",program);
		for(Mesh x : maze.meshes)
			player.colliders.add(x);
		maze.rootNode.defaultTransform.rotateLocalX((float)Math.toRadians(180));
		renderedModels.add(maze);
		glClearColor(0,.5f,.5f,0);
		long prevTime = System.currentTimeMillis();
		for(;!glfwWindowShouldClose(window.getId());) {
			glfwPollEvents();
			long nowTime = System.currentTimeMillis();
			int delta = (int)(nowTime-prevTime);
			prevTime = nowTime;
			player.handleInput(delta);
			glUniformMatrix4fv(program.getUniformLocation("view"),false,player.getView());
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			for(Model x : renderedModels) {
				x.render(new Matrix4f());//.rotateX((float)Math.toRadians(90)));
			}
			out.println("player.loc"+player.loc);
			//System.out.println("error:"+glGetError());
			glDisableVertexAttribArray(0);
			glBindVertexArray(0);
			window.swapBuffers();
		}
		window.destroy();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		close();
	});}
}