/*
 * David Jacewicz
 * June 1, 2018
 * Ms. Krasteva
 * Keyboard key state tracker
 */

/*
 * Modification: Fix immediateKeys set not being modified
 * David Jacewicz
 * June 3, 2018
 * 1 hour
 * Version: 0.05
 */

import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
public class Keyboard {
	private Set<Integer> keysPressed = new TreeSet<Integer>();
	public Map<Integer,Runnable> immediateKeys = new TreeMap<Integer,Runnable>();
	public GLFWKeyCallbackI listener = new GLFWKeyCallbackI() {
		// TODO: scancode
		/**
		 * This callback is invoked when a key is pressed; it is used to update the data contained in this class and calls the corresponding callbacks
		 * 
		 * @param window The window ID this key was pressed on
		 * @param scancode
		 * @param action The type of key event(key down/key up)
		 * @param mods The modifiers(CTRL/ATL/SHIFT)
		 */
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
	/** @return The callback mapping */
	public Map<Integer,Runnable> getImmediateKeys() {
		return immediateKeys; }
	/** @return The pressed keys set */
	public Set<Integer> getKeysPressed() { return keysPressed; }	
}
