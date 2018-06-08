/*
 * David Jacewicz
 * May 17, 2018
 * Ms. Krasteva
 * The base class for all the levels
 */

/*
 * Modification: refactor keyboard and mouse to be in here
 * David Jacewicz
 * May 24, 2018
 * 1 hour
 * Version: 0.04
 */

/*
 * Modification: move dialogue window to this class
 * David Jacewicz
 * May 26, 2018
 * 2 hours
 * Version: 0.04
 */

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
	public abstract void inContext();
	public Set<ModelNode> renderedModelNodes = new HashSet<ModelNode>();
	public JFrame dialogFrame;
	public JLabel dialog = new JLabel();

	/**
	 * Display dialog in dialog window
	 *
	 * @param text The dialog to display
	 */
	public void dialog(String text) {
		dialog.setText(
			"<html><style>body{font-size:20px;}</style><body>"+
			text+
			"</body></html>");
	}
	public GLWindow.BoundCallback dialogFrameBoundsCallback = new GLWindow.BoundCallback() {
		/** Method to update this window when main window is moved */
		public void invoke(GLWindow w) {
			dialogFrame.setBounds(
				w.x,w.y+w.height,
				w.width,w.height/3); } };;

	/** Initialized the dialog window */
	public void initDialogFrame() {
		dialogFrame = new JFrame(){{
			add(BorderLayout.NORTH,dialog);
			setUndecorated(true);
			setVisible(true);
		}};
	}
	/** Method to initialize and start the level */
	public void run() { exPrint(()->{
		init();
		inContext();
		renderLoop();
	});}

	/** Initializes OpenGL and calls inContext() */
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

	/** Renders object in a loop */
	public void renderLoop() { exPrint(()->{
		window.show();
		initDialogFrame();
		window.positionCallbacks.add(dialogFrameBoundsCallback);
		window.resizeCallbacks.add(dialogFrameBoundsCallback);
		dialogFrameBoundsCallback.invoke(window);
		window.resizeCallbacks.add(new GLWindow.BoundCallback() {
			/** Updates the main window when resized */
			public void invoke(GLWindow a) {
				int
					height = window.height,
					width = window.width;
				final float FOV = (float) Math.toRadians(100f);
				final float Z_NEAR = .1f;
				final float Z_FAR = 100;
				Matrix4f perspectiveMatrix;
				float aspectRatio = 1f*width/height;
				perspectiveMatrix = new Matrix4f().perspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
				glUniformMatrix4fv(program.getUniformLocation("perspective"),false,
					perspectiveMatrix.get(new float[16]));
				glViewport(0,0,width,height);
			}
			{invoke(window);}});
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearColor(0,.5f,.5f,0);
		new Thread() {
			/** Looped method to handle player movement input */
			public void run() { exPrint(()->{
				long prevTime = System.currentTimeMillis();
				for(;;) {
					long nowTime = System.currentTimeMillis();
					int delta = (int)(nowTime-prevTime);
					prevTime = nowTime;
					player.handleInput(delta);
				}
			});
		}}.start();
		for(;;) {
			glfwPollEvents();
			glUniformMatrix4fv(program.getUniformLocation("view"),false,player.getView());
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			synchronized(renderedModelNodes) {
				for(ModelNode x : renderedModelNodes)
					x.render(new Matrix4f());
			}
			window.swapBuffers();
		}
	});}
}
