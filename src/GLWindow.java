/*
 * David Jacewicz
 * June 3, 2018
 * Ms. Krasteva
 * A window opened with GLFW; can be drawn to with OpenGL
 */

/*
 * Modification: add utility methods
 * David Jacewicz
 * June 5, 2018
 * 30 minutes
 * Version: 0.05
 */

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.util.*;
import java.awt.*;
import java.util.List;

public class GLWindow {
	private final GLWindow window = this;
	private long id;
	public int
		width,height,
		x,y;
	public interface BoundCallback
	{
		/**
		 * The callback method to be invoked
		 *
		 * @param window The window to invoke this callback on
		 */
		void invoke(GLWindow window);
	}
	public List<BoundCallback>
		resizeCallbacks = new ArrayList<>(),
		positionCallbacks = new ArrayList<>();
	/**
	 * Creates a new OpenGL-enabled window
	 *
	 * @param width The width of the window in pixels
	 * @param height The height of the window in pixels
	 * @param title The window title
	 * @param keyboard The keyboard object to use
	 */
	public GLWindow(int width,int height,String title,Keyboard keyboard) {
		this.width = width;
		this.height = height;
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE); // the window will be resizable
		// Create the window
		id = glfwCreateWindow(width,height,title,NULL,NULL);
		if(id == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		glfwSetWindowSizeCallback(id,new GLFWWindowSizeCallback() {
			/**
			 * The callback method that invokes custom window resize callbacks
			 *
			 * @param windowId The ID of the window that was resized
			 * @param width The new width
			 * @param height The new height
			 */
			public void invoke(long windowId,int width,int height) {
				window.width = width;
				window.height = height;
				for(BoundCallback x : resizeCallbacks)
					x.invoke(window);
			}
		});
		glfwSetWindowPosCallback(id,new GLFWWindowPosCallback() {
			/**
			 * The callback method that invokes custom window position change callbacks
			 *
			 * @param windowId The ID of the window that was moved
			 * @param x The new x coordinate
			 * @param y The new y coordinate
			 */
			public void invoke(long windowId,int x,int y) {
				window.x = x;
				window.y = y;
				for(BoundCallback xx : positionCallbacks)
					xx.invoke(window);
			}
		});
		glfwSetKeyCallback(id,keyboard.listener);
		keyboard.getImmediateKeys().put(GLFW_KEY_X,new Runnable() {
			/** The method that closes the window when X is pressed */
			public void run() {
				System.exit(0);
			}
		});
	}
	/** Marks this window as should be closed */
	public void setShouldClose() { glfwSetWindowShouldClose(id,true); }
	/** @return Whether this window should close */
	public boolean shouldClose() { return glfwWindowShouldClose(id); }
	/** Makes the OpenGL context current on this thread */
	public void makeContextCurrent() { glfwMakeContextCurrent(id); }
	/** Displays this window */
	public void show() { glfwShowWindow(id); }
	/** Swaps the display buffers of this window */
	public void swapBuffers() { glfwSwapBuffers(id); }
	/** @return The window ID of this window */
	public long getId() { return id; }
	/** Closes this window */
	public void destroy() {
		glfwFreeCallbacks(id);
		glfwDestroyWindow(id);
	}
}
