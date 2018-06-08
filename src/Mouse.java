/*
 * David Jacewicz
 * May 25, 2018
 * Ms. Krasteva
 * Mouse input handler
 */

/*
 * Modification: clip mouse to normal human field of view
 * David Jacewicz
 * June 1, 2018
 * 2 hours
 * Version: 0.05
 */

import org.joml.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import java.lang.Math;
public class Mouse {
	private GLWindow window;
	private boolean captured = false;
	private final float SENSITIVITY = 3;

	/**
	 * Creates a mouse input handler for the given OpenGL enabled window
	 *
	 * @param window The window to read input from
	 */
	public Mouse(GLWindow window) {
		this.window = window;
	}
	private Vector2f
		lastPos = new Vector2f(0,0),
		newPos,
		boundedPos = new Vector2f(0,0);

	/**
	 * Compute the camera state from the curson position
	 *
	 * @return The camera state
	 */
	public Vector2f getCameraCursor() {
		if(!captured) return boundedPos;
		double[]
			x = new double[1],
			y = new double[1];
		glfwGetCursorPos(window.getId(),x,y);
		newPos = new Vector2f((float)x[0],(float)y[0]).mul(.001f);
		Vector2f delta = newPos.sub(lastPos,new Vector2f());
		lastPos.set(newPos);
		boundedPos.add(delta.mul(SENSITIVITY));
		boundedPos.y = (float)Math.max(-Math.PI/2,(float)Math.min(Math.PI/2,boundedPos.y));
		return boundedPos;
	}

	/**
	 * Sets whether the mouse is captured
	 *
	 * @param state true iff the mouse should be captured
	 */
	// TODO: access level of this method
	void capture(boolean state) {
		if(state)
			glfwSetInputMode(window.getId(),GLFW_CURSOR,GLFW_CURSOR_DISABLED);
		else
			glfwSetInputMode(window.getId(),GLFW_CURSOR,GLFW_CURSOR_NORMAL);
		captured = state;
	}

	/** @return Whether or not the mouse is being captured */
	boolean getCaptured() { return captured; }
}
