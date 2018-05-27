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
import java.util.*;
import java.util.List;
public class Level2 extends LevelBase {
	private JFrame dialogFrame;
	private MeterFrame meterFrame;
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
			setUndecorated(true);
			setVisible(true);
		}};
		meterFrame = new MeterFrame(){{
			meters.put("Happiness",new Meter(.02f));
			meters.put("Exercise",new Meter(.01f));
			setUndecorated(true);
			setVisible(true);
		}};
		meterFrame.boundCallback.invoke(window);
		GLWindow.BoundCallback bottomFrameAdjust = new GLWindow.BoundCallback() {
			public void invoke(GLWindow w) {
				dialogFrame.setBounds(w.x,w.y+w.height,w.width+meterFrame.getWidth(),w.height/3); }
			{invoke(window);}};
		
		for(GLWindow.BoundCallback x : new GLWindow.BoundCallback[]{bottomFrameAdjust,meterFrame.boundCallback}) {
			window.resizeCallbacks.add(x);
			window.positionCallbacks.add(x);
		}
		new Thread(done).start();
		for(;;)
			for(JFrame x : new JFrame[]{dialogFrame,meterFrame}) {
				exPrint(()->{Thread.sleep(20);});
				x.repaint();
			}
	}
	// private Model bed;
	private Model bed;
	public void inContext() {
		out.println("inContext thread name"+Thread.currentThread().getName());
		out.println("inContext error:"+glGetError());
		bed = new Model("bed","dae",program);
		bed.rootNode.defaultTransform
			//.rotateLocalY((float)Math.toRadians(180))
			.translateLocal(new Vector3f(0,0,0 /* -2 */));
		// for(Mesh x : bed.meshes)
		// 	player.colliders.add(x);
	}
	public void onReady() {
		initFrames(()->{exPrint(()->{
			dialog("hecc!");
			List<RefillPoint> refillPoints = new ArrayList<RefillPoint>(){{
				add(new RefillPoint("Happiness",new Vector3f(0,5,10/*6.561E+0f,0,7.323E+0f*/),bed));
			}};
			renderedModels.add(bed);
			// check proximity to refill points
			new Thread() { public void run() { exPrint(()->{
				for(;;) {
					Thread.sleep(50);
					for(RefillPoint x : refillPoints)
						if(player.loc.distance(x.loc) < 3)
							meterFrame.meters.get(x.name)
								.lastRefill = System.currentTimeMillis();
				}
			});}}.start();
			for(;;) {
				Thread.sleep(1000);
				out.println("player.loc:"+player.loc);
			}
		});});
	}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level2().run(); }
}