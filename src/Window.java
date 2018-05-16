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

public class Window {
	private final Window window = this;
	private long id;
	private int width,height;
	public Window(int width,int height,String title,Keyboard keyboard) {
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
		glfwSetKeyCallback(id,keyboard.listener);
		keyboard.getImmediateKeys().put(GLFW_KEY_X,new Runnable() { public void run() {
			glfwSetWindowShouldClose(id,true);
		}});
		glfwSetWindowSizeCallback(id, (windowId,w,h) -> {
			glViewport(0,0,w,h);
			window.width = w;
			window.height = h;
		});
	}
	void makeContextCurrent() { glfwMakeContextCurrent(id); }
	void show() { glfwShowWindow(id); }
	void swapBuffers() { glfwSwapBuffers(id); }
	long getId() { return id; }
	int getWidth() { return width; }
	int getHeight() { return height; }
	void destroy() {
		glfwFreeCallbacks(id);
		glfwDestroyWindow(id);
	}
}