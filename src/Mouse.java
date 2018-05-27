import org.joml.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import java.lang.Math;
public class Mouse {
	private GLWindow window;
	private boolean captured = false;
	private final float SENSITIVITY = 3;
	public Mouse(GLWindow window) {
		this.window = window;
	}
	private Vector2f
		lastPos = new Vector2f(0,0),
		newPos,
		boundedPos = new Vector2f(0,0);
	public Vector2f getCameraCursor() {
		if(!captured) return boundedPos;
		double[]
			x = new double[1],
			y = new double[1];
		glfwGetCursorPos(window.getId(),x,y);
		newPos = new Vector2f((float)x[0],(float)y[0]).mul(.001f);
		//System.out.println("newPos:"+newPos);
		Vector2f delta = newPos.sub(lastPos,new Vector2f());
		lastPos.set(newPos);
		boundedPos.add(delta.mul(SENSITIVITY));
		boundedPos.y = (float)Math.max(-Math.PI/2,(float)Math.min(Math.PI/2,boundedPos.y));
		//System.out.println("bounded:"+boundedPos);
		return boundedPos;
	}
	void capture(boolean state) {
		if(state)
			glfwSetInputMode(window.getId(),GLFW_CURSOR,GLFW_CURSOR_DISABLED);
		else
			glfwSetInputMode(window.getId(),GLFW_CURSOR,GLFW_CURSOR_NORMAL);
		captured = state;
	}
	boolean getCaptured() { return captured; }
}