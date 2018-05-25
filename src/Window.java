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

public class Window {
	private final Window window = this;
	private long id;
	public int
		width,height,
		x,y;
	public interface BoundCallback {
		public void invoke(Window a,Dimension b); }
	public List<BoundCallback>
		resizeCallbacks = new ArrayList<>(),
		positionCallbacks = new ArrayList<>();
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
		glfwSetWindowSizeCallback(id,new GLFWWindowSizeCallback() {
			public void invoke(long windowId,int width,int height) {
				window.width = width;
				window.height = height;
				System.out.println("resizeCallbacks.size()"+resizeCallbacks.size());
				for(BoundCallback x : resizeCallbacks) {
					System.out.println("callback resize");
					x.invoke(window,new Dimension(width,height));
				}
			}
		});
		glfwSetWindowPosCallback(id,new GLFWWindowPosCallback() {
			public void invoke(long windowId,int x,int y) {
				window.x = x;
				window.y = y;
				System.out.println("positionCallbacks.size():"+positionCallbacks.size());
				for(BoundCallback xx : positionCallbacks)
					xx.invoke(window,new Dimension(x,y));
			}
		});
		glfwSetKeyCallback(id,keyboard.listener);
		keyboard.getImmediateKeys().put(GLFW_KEY_X,new Runnable() { public void run() {
			glfwSetWindowShouldClose(id,true);
		}});
	}
	public boolean shouldClose() { return glfwWindowShouldClose(id); }
	public void makeContextCurrent() { glfwMakeContextCurrent(id); }
	public void show() { glfwShowWindow(id); }
	public void swapBuffers() { glfwSwapBuffers(id); }
	public long getId() { return id; }
	public void destroy() {
		glfwFreeCallbacks(id);
		glfwDestroyWindow(id);
	}
}