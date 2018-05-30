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
	public GLWindow window;
	public Program program;
	public Player player;
	public abstract void close();
	public abstract void onReady();
	public abstract void inContext();
	public Set<ModelNode> renderedModelNodes = new HashSet<ModelNode>();
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
		window = new GLWindow(500,500,"Alternate Perspective",keyboard);
		window.makeContextCurrent();
		glfwSwapInterval(1);
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
		window.resizeCallbacks.add(new GLWindow.BoundCallback() {
			public void invoke(GLWindow a) {
				int
					height = window.height,
					width = window.width;
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
			{invoke(window);}});
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		//https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
		glClearColor(0,.5f,.5f,0);
		new Thread() { public void run() { exPrint(()->{
			long prevTime = System.currentTimeMillis();
			for(;;) {
				Thread.sleep(20);
				long nowTime = System.currentTimeMillis();
				int delta = (int)(nowTime-prevTime);
				prevTime = nowTime;
				player.handleInput(delta);
			}
		});}}.start();
		for(;;) {
			glfwPollEvents();
			glUniformMatrix4fv(program.getUniformLocation("view"),false,player.getView());
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			synchronized(renderedModelNodes) {
				for(ModelNode x : renderedModelNodes)
					x.render(new Matrix4f());
			}
			//System.out.println("error:"+glGetError());
			window.swapBuffers();
		}
		// window.setShouldClose(true);
		// window.destroy();
		// glfwTerminate();
		// glfwSetErrorCallback(null).free();
		// close();
	});}
}