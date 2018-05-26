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
import static util.Util.*;
public class Level2 extends LevelBase {
	private JFrame dialogFrame, meterFrame;
	private JLabel dialog = new JLabel();
	private void dialog(String text) {
		dialog.setText(
			"<html><style>body{font-size:20px;}</style><body>"+
			text+
			"</body></html>");
	}
	public String getName() { return "Level 2"; }
	public void initFrames(Runnable done) {
		dialogFrame = new JFrame(){{
			add(BorderLayout.NORTH,dialog);
			setVisible(true);
		}};
		meterFrame = new MeterFrame(){{
			meters.put("Happiness",new Meter(.1f));
			meters.put("Exercise",new Meter(.2f));
			setVisible(true);
		}};
		Window.BoundCallback bottomFrameAdjust = new Window.BoundCallback() {
			public void invoke(Window w,Dimension b) {
				dialogFrame.setBounds(w.x,w.y+w.height,w.width,w.height/3); } };
		Window.BoundCallback rightFrameAdjust = new Window.BoundCallback() {
			public void invoke(Window w,Dimension b) {
				meterFrame.setBounds(w.x+w.width,w.y,w.height/3,w.height*4/3); } };
		for(Window.BoundCallback x : new Window.BoundCallback[]{bottomFrameAdjust,rightFrameAdjust}) {
			window.resizeCallbacks.add(x);
			window.positionCallbacks.add(x);
		}
		new Thread(done).start();
		dialog("hecc!");
		for(;;)
			for(JFrame x : new JFrame[]{dialogFrame,meterFrame}) {
				exPrint(()->{Thread.sleep(20);});
				x.repaint();
			}
	}
	public void logic() {
		initFrames(()->{exPrint(()->{
			Thread.sleep(1000);
			out.println("hey!");
		});});
	}
	public void close() { exPrint(()->{
		System.exit(0);
	});}
	public static void main(String[] a) throws Exception {
		new Level2().run();
	}
}