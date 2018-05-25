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
	JLabel dialog = new JLabel();
	private void dialog(String text) {
		dialog.setText(
			"<html><style>body{font-size:20px;}</style><body>"+
			text+
			"</body></html>");
	}
	public String getName() { return "Level 2"; }
	public void logic() throws Exception {
		JFrame bottomFrame = new JFrame(){{
			add(BorderLayout.NORTH,dialog);
			setVisible(true);
		}};
		JFrame rightFrame = new JFrame(){{
			add(BorderLayout.NORTH,new JButton("test"));
			setVisible(true);
		}};
		Window.BoundCallback bottomFrameAdjust = new Window.BoundCallback() {
			public void invoke(Window w,Dimension b) {
				bottomFrame.setBounds(w.x,w.y+w.height,w.width,w.height/3);
			}
		};
		Window.BoundCallback rightFrameAdjust = new Window.BoundCallback() {
			public void invoke(Window w,Dimension b) {
				rightFrame.setBounds(w.x+w.width,w.y,w.width/3,w.height*4/3);
			}
		};
		for(Window.BoundCallback x : new Window.BoundCallback[]{bottomFrameAdjust,rightFrameAdjust}) {
			window.resizeCallbacks.add(x);
			window.positionCallbacks.add(x);
		}
		Thread.sleep(1000);
		dialog("hecc!");
		for(;running;) {
			System.out.println("hecc");
			Thread.sleep(500); // do stuff
		}
		bottomFrame.dispatchEvent(new WindowEvent(bottomFrame,WindowEvent.WINDOW_CLOSING));
	}
}