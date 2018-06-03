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
	public final static int LEVEL_DURATION = 60;
	private MeterFrame meterFrame;
	public void initMeterFrame() {
		meterFrame = new MeterFrame(){{
			meters.put("Sleep",new Meter(.01f));
			meters.put("Music",new Meter(.03f));
			meters.put("Engineering",new Meter(.04f));
			meters.put("Studying",new Meter(.02f));
			setUndecorated(true);
			setVisible(true);
		}};
		window.resizeCallbacks.add(meterFrame.boundsCallback);
		window.positionCallbacks.add(meterFrame.boundsCallback);
		meterFrame.boundsCallback.invoke(window);
		new Thread() { public void run() {
			for(;;) {
				exPrint(()->{Thread.sleep(20);});
				meterFrame.repaint();
			}
		}}.start();
	}
	private ModelNode maze,bed,guitar,books,arduino;
	public void inContext() {
		dialogFrameBoundsCallback = new GLWindow.BoundCallback() {
			public void invoke(GLWindow w) {
				dialogFrame.setBounds(w.x,w.y+w.height,
					w.width+(meterFrame==null?0:meterFrame.getWidth()),
					w.height/3); } };
		maze = new Model("maze1","dae",program).rootNode;
		(bed = new Model("bed","dae",program).rootNode).getLocalTransform()
			.rotateLocalY((float)Math.toRadians(-90))
			.translateLocal(new Vector3f(0,0,0));
		(guitar = new Model("guitar","dae",program).rootNode).getLocalTransform()
			.scale(.02f)
			.rotateLocalX((float)Math.toRadians(90))
			.translateLocal(new Vector3f(0,2,0));
		(books = new Model("books","obj",program).rootNode).getLocalTransform()
			.scale(.5f)
			.rotateLocalY((float)Math.toRadians(-45))
			.translateLocal(0,-1,0);
		(arduino = new Model("arduino","obj",program).rootNode).getLocalTransform()
			.scale(.3f)
			.rotateLocalY((float)Math.toRadians(90))
			.rotateLocalX((float)Math.toRadians(-70));
		for(ModelNode x : new ModelNode[]{maze})
			player.colliders.add(x);
	}
	List<RefillPoint> refillPoints;
	public void onReady() { exPrint(()->{
		refillPoints = new ArrayList<RefillPoint>(){{
			add(new RefillPoint("Sleep",new Vector3f(5,0,-7.5f),bed));
			add(new RefillPoint("Music",new Vector3f(-6.5f,0,-6.928f),guitar));
			add(new RefillPoint("Studying",new Vector3f(8.132f,1.5f,8.127f),books));
			add(new RefillPoint("Engineering",new Vector3f(-6.5f,1.5f,7f),arduino));
		}};
		// now that they're in the right spots, start rendering the models
		renderedModelNodes.addAll(list(new ModelNode[]{maze,bed,books,guitar,arduino}));
		String[] dialogStrs = new String[]{
			"Use  W A S D  to move around. Press T to continue.",
			"Explore the level! Find the 4 refill points at the corners of the maze.",
			"When the game starts, meters will appear that are constantly draining.",
			"If any of the meters gets completely empty, you lose.",
			"Keep these meters full by running to the corresonding station.",
			"Good luck! Press T to start the game!",
		};
		keyboard.immediateKeys.put(GLFW_KEY_T,new Runnable() {
			private int dialogIndex = 0;
			public void run() {
				if(dialogIndex < dialogStrs.length)
					dialog(dialogStrs[dialogIndex++]);
				else {
					keyboard.immediateKeys.remove(GLFW_KEY_T);
					new Thread(){public void run(){
						play(); end();
					}}.start();
				}
			}
			{ run(); }
		});
	});}
	public void hideMeterFrame() {
		meterFrame.setVisible(false);
		meterFrame = null;
		dialogFrameBoundsCallback.invoke(window);
	}
	boolean[] win = new boolean[]{true};
	public void play() { exPrint(()->{
		initMeterFrame();
		// resize dialog frame to fill space for newly-created meterFrame
		dialogFrameBoundsCallback.invoke(window);
		// check proximity to refill points
		new Thread(()->{exPrint(()->{
			for(;;) {
				Thread.sleep(50);
				for(RefillPoint x : refillPoints)
					if(player.loc.distance(x.loc) < 2)
						meterFrame.meters.get(x.name)
							.lastRefill = System.currentTimeMillis();
			}
		});}).start();
		meterFrame.emptyCallbacks.add(()->{ win[0] = false; });
		long startTime = System.currentTimeMillis();
		for(long remaining=1;0<remaining && win[0];) {
			Thread.sleep(1000);
			long now = System.currentTimeMillis();
			remaining = LEVEL_DURATION-(now-startTime)/1000;
			dialog("Survive for "+remaining+" seconds to win the game.");
		}
	});}
	public void end() {
		if(win[0]) {
			long now = System.currentTimeMillis();
			int scoreAcc = 0;
			{
				Set<String> meterNames = meterFrame.meters.keySet();
				for(String name : meterNames) {
					Meter x = meterFrame.meters.get(name);
					scoreAcc += 1000*(1f-x.leakRate*(now-x.lastRefill)/1000);
				}
			}
			final int score = scoreAcc;
			hideMeterFrame();
			String[] dialogStrs = new String[]{
				"You won! Press T to continue",
				"Your final score was " + score + " which is how full your meters were at the end.",
				"That was pretty hard, wasn't it?",
				"Now imagine how much harder it would be if we added in procrastination.",
				"There really is no time for procrastination.",
				"We get no work done, and it isn't even fun.",
				"Procrastinating usually makes us feel bad about ourselves.",
				"So here are some things you can do to stop procrastinating...",
			};
			keyboard.immediateKeys.put(GLFW_KEY_T,new Runnable() {
				private int dialogIndex = 0;
				public void run() {
					if(dialogIndex < dialogStrs.length)
						dialog(dialogStrs[dialogIndex++]);
					else {
						keyboard.immediateKeys.remove(GLFW_KEY_T);
						new Thread(){public void run(){
							out.println(score);
							close();
						}}.start();
					}
				}
				{run();}
			});
		} else {
			hideMeterFrame();
			dialog("You lose. Press T to return to the menu.");
			keyboard.immediateKeys.put(GLFW_KEY_T,()->{close();});
		}
	}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level2().run(); }
}