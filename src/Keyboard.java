import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
public class Keyboard {
	Set<Integer> keysPressed = new TreeSet<Integer>();
	Map<Integer,Runnable> immediateKeys = new TreeMap<Integer,Runnable>();
	public GLFWKeyCallbackI listener = new GLFWKeyCallbackI() {
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			switch(action) {
				case GLFW_PRESS: {
					if(immediateKeys.keySet().contains(key))
						immediateKeys.get(key).run();
					else
						keysPressed.add(key);
				} break;
				case GLFW_RELEASE: {
					keysPressed.remove(key);
				} break;
			}
		}
	};
	public Map<Integer,Runnable> getImmediateKeys() {
		return immediateKeys; }
}