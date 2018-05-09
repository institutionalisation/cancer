import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
public class Keyboard {
	private Set<Integer> keysPressed = new TreeSet<Integer>();
	private Map<Integer,Runnable> immediateKeys = new TreeMap<Integer,Runnable>();
	public GLFWKeyCallbackI listener = new GLFWKeyCallbackI() {
		public void invoke(long window,int key,int scancode,int action,int mods) {
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
	public Set<Integer> getKeysPressed() { return keysPressed; }	
}