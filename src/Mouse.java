import org.joml.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
public class Mouse {
	Window window;
	public Mouse(Window window) {
		this.window = window;
		glfwSetInputMode(window.getId(),GLFW_CURSOR,GLFW_CURSOR_DISABLED);
	}
	// returns distance mouse has moved
	public Vector2f getPos() {
		double[]
			x = new double[1],
			y = new double[1];
		glfwGetCursorPos(window.getId(),x,y);
		return new Vector2f((float)x[0],(float)y[0]);
	}
}