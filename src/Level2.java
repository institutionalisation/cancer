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
public class Level2 extends LevelBase implements Level {
	public String getName() { return "Level 2"; }
	public void run() throws Exception {
		new Thread() {
			public void run() {
				try {
					init();
					renderLoop();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		JFrame jFrame = new JFrame(){{
			add(new JButton("hi!"));
			setVisible(true);
		}};
		for(;running;) {
			System.out.println("hecc");
			Thread.sleep(500); // do stuff
		}
		jFrame.dispatchEvent(new WindowEvent(jFrame,WindowEvent.WINDOW_CLOSING));
	}
}