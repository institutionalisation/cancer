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
	public void initFrames() {
		dialogFrame = new JFrame(){{
			add(BorderLayout.NORTH,dialog);
			setUndecorated(true);
			setVisible(true);
		}};
		meterFrame = new MeterFrame(){{
			meters.put("Sleep",new Meter(.01f));
			meters.put("Music",new Meter(.03f));
			meters.put("Engineering",new Meter(.04f));
			meters.put("Studying",new Meter(.02f));
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
		new Thread() { public void run() {
			for(;;) for(JFrame x : new JFrame[]{dialogFrame,meterFrame}) {
				exPrint(()->{Thread.sleep(20);});
				x.repaint();
			}
		}}.start();
	}
	private Model bed,guitar,books,arduino;
	public void inContext() {
		(bed = new Model("bed","dae",program)).rootNode.defaultTransform
			.rotateLocalY((float)Math.toRadians(-90))
			.translateLocal(new Vector3f(0,0,0));
		renderedModels.add(bed);
		(guitar = new Model("guitar","dae",program)).rootNode.defaultTransform
			.scale(.02f)
			.rotateLocalX((float)Math.toRadians(90))
			.translateLocal(new Vector3f(0,2,0));
		(books = new Model("books","obj",program)).rootNode.defaultTransform
			.scale(.5f)
			.rotateLocalY((float)Math.toRadians(-45))
			.translateLocal(0,-1,0);
		(arduino = new Model("arduino","obj",program)).rootNode.defaultTransform
			.scale(.3f)
			.rotateLocalY((float)Math.toRadians(90))
			.rotateLocalX((float)Math.toRadians(-70));
		for(Model x : new Model[]{bed,guitar,books,arduino})
			renderedModels.add(x);
	}
	public void onReady() { exPrint(()->{
		initFrames();
		dialog("hecc!");
		List<RefillPoint> refillPoints = new ArrayList<RefillPoint>(){{
			add(new RefillPoint("Sleep",new Vector3f(5,0,-7.5f),bed));
			add(new RefillPoint("Music",new Vector3f(-6.5f,0,-6.928f),guitar));
			add(new RefillPoint("Studying",new Vector3f(8.132f,1.5f,8.127f),books));
			add(new RefillPoint("Engineering",new Vector3f(-6.5f,1.5f,7f),arduino));
		}};
		// check proximity to refill points
		new Thread() { public void run() { exPrint(()->{
			for(;;) {
				Thread.sleep(50);
				for(RefillPoint x : refillPoints)
					if(player.loc.distance(x.loc) < 2)
						meterFrame.meters.get(x.name)
							.lastRefill = System.currentTimeMillis();
			}
		});}}.start();
		for(;;) {
			Thread.sleep(1000);
			out.println("player.loc:"+player.loc);
		}
	});}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level2().run(); }
}