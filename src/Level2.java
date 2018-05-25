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
public class Level2 extends LevelBase {
	public String getName() { return "Level 2"; }
	public void logic() throws Exception {
		JFrame jFrame = new JFrame(){{
			add(new JButton("hi!"));
			setVisible(true);
		}};
		Window.BoundCallback callback = new Window.BoundCallback() {
			public void invoke(Window window,Dimension b) {
				System.out.println("set bounds");
				jFrame.setBounds(window.x,window.y+window.height,window.width,window.height/3);
			}
		};
		window.print();
		window.resizeCallbacks.add(callback);
		window.positionCallbacks.add(callback);
		for(;running;) {
			System.out.println("hecc");
			Thread.sleep(500); // do stuff
		}
		jFrame.dispatchEvent(new WindowEvent(jFrame,WindowEvent.WINDOW_CLOSING));
	}
}