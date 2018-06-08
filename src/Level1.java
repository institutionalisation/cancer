/*
 * David Jacewicz
 * May 21, 2018
 * Ms. Krasteva
 * Level 2 driver class
 */

/*
 * Modification: draw buttons for the puzzle
 * David Jacewicz
 * May 22, 2018
 * 2 hours
 * Version: 0.03
 */

/*
 * Modification: draw and animate platform
 * David Jacewicz
 * May 25, 2018
 * 4 hours
 * Version: 0.03
 */

/*
 * Modification: fix collsion bug with buttons
 * David Jacewicz
 * May 26, 2018
 * 6 hours
 * Version: 0.03
 */

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

public class Level1 extends LevelBase { final Level1 level = this;
	private ModelNode stage;
	private Platform redPlatform,bluePlatform,yellowPlatform;
	private enum State { TO_END,TO_START };
	private long startTime;

	/** Callback method with GL context; used for rendering */
	public void inContext() {
		redPlatform = new Platform(){{ set(new Model("maze2/red","obj",program).rootNode); }};
		bluePlatform = new Platform(){{ set(new Model("maze2/blue","obj",program).rootNode); }};
		yellowPlatform = new Platform(){{ set(new Model("maze2/yellow","obj",program).rootNode); }};
		stage = new Model("maze2/static","obj",program).rootNode;
		renderedModelNodes.addAll(Arrays.asList(new ModelNode[]{
			stage,
			redPlatform,bluePlatform,yellowPlatform}));
		player.colliders.addAll(Arrays.asList(new ModelNode[]{
			stage,redPlatform,bluePlatform,yellowPlatform}));
		ButtonBuilder bb = new ButtonBuilder(program)
			.setStickTime(2000);
		{
			// no faster way to expose the enum :(
			ButtonBuilder.Color RED = ButtonBuilder.Color.RED;
			ButtonBuilder.Color BLUE = ButtonBuilder.Color.BLUE;
			ButtonBuilder.Color YELLOW = ButtonBuilder.Color.YELLOW;
			bb.colorCallbacks.put(RED,redPlatform.raise());
			bb.colorCallbacks.put(BLUE,bluePlatform.raise());
			bb.colorCallbacks.put(YELLOW,yellowPlatform.raise());
			stage.children.addAll(Arrays.asList(new ButtonBuilder.Button[]{
				// first 3
				bb.new Button(RED){{
					getLocalTransform().translate(-10.3f,0,-10.8f); }},
				bb.new Button(BLUE){{
					getLocalTransform().translate(10.4f,0,-10.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(.05f,0,-10.8f); }},
				// on the top-hat-shaped block
				bb.new Button(YELLOW){{
					getLocalTransform().translate(11.6f,0,2.9f); }},
				bb.new Button(BLUE){{
					getLocalTransform().translate(11.6f,0,5.2f); }},
				// at the end
				bb.new Button(RED){{
					getLocalTransform().translate(8.6f,0,40.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(8.7f,0,37.2f); }}
			}));
				bluePlatform.children.addAll(Arrays.asList(new ButtonBuilder.Button[]{
				bb.new Button(RED){{
					getLocalTransform().translate(0,0,-3.190f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(-4.3f,0,5.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(.2f,0,28.5f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(4.7f,0,28.26f); }}
			}));
			redPlatform.children.addAll(Arrays.asList(new ButtonBuilder.Button[]{
				bb.new Button(YELLOW){{
					getLocalTransform().translate(-1.2f,0,8.5f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(-7f,0,16.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(-9f,0,16.8f); }},
				bb.new Button(BLUE){{
					getLocalTransform().translate(-11f,0,16.8f); }}
			}));
		}
		// needs to be accessed from anonymous classes
		State[] state = new State[]{State.TO_END};
		keyboard.immediateKeys.put(GLFW_KEY_R,new Runnable() {
			public void run() {
				dialog("Restarted.");
				state[0] = State.TO_END;
				player.loc.set(9.2f,1.5f,-10.8f);
				// move away all the platforms
				for(Platform x : Arrays.asList(new Platform[]{redPlatform,bluePlatform,yellowPlatform}))
					x.lastRaise = 0;
				startTime = System.currentTimeMillis();
			}
			{run();}
		});
		new Thread(()->{exPrint(()->{
			for(;;) {
				switch(state[0]) {
					case TO_END:
						if(43 < player.loc.z) {
							dialog("Good job, now make it back!");
							state[0] = State.TO_START;
						}
						break;
					case TO_START:
						if(player.loc.z<-9) {
							end();
							return;
						}
				}
				if(player.loc.y<-10)
					dialog("You fell off the stage, press R to restart.");
				Thread.sleep(200);
			}
		});}).start();
		String[] dialogStrs = new String[]{
			"Press T to continue.",
			"To win this level, you'll need to plan for the future. (Press T to continue)",
			"When you press on a button, platforms of the same colour will be raised.",
			"You can't just think about the buttons you're pressing now though.",
			"You need to think of how they'll help you later on.",
			"Get to the other side."
		};
		keyboard.immediateKeys.put(GLFW_KEY_T,new Runnable() {
			private int dialogIndex = 0;
			public void run() {
				if(dialogIndex < dialogStrs.length)
					dialog(dialogStrs[dialogIndex++]);
				else
					keyboard.immediateKeys.remove(GLFW_KEY_T);
			}
			{run();}
		});
	}

	/** The end game logic */
	private void end() {
		int delta = (int)(System.currentTimeMillis()-startTime);
		int score = 30000000/delta;
		String[] dialogStrs = new String[]{
			"Congrats! You win! (Press T to continue)",
			"You took "+delta/1000f+" seconds to finish, giving you a score of "+score+".",
			"You thought about how your actions affect the future.",
			"Whether it be saving up for a car or choosing which university we want to go to",
			"planning for the future is an important skill that everyone must learn.",
			"Press T to return to the menu.",
		};
		keyboard.immediateKeys.put(GLFW_KEY_T,new Runnable() {
			private int dialogIndex = 0;
			public void run() {
				if(dialogIndex < dialogStrs.length)
					dialog(dialogStrs[dialogIndex++]);
				else {
					keyboard.immediateKeys.remove(GLFW_KEY_T);
					out.println(score);
					close();
				}
			}
			{run();}
		});
		keyboard.immediateKeys.put(GLFW_KEY_G,()->{
			out.println(score);
			close();
		});
	}

	/** Exits this level */
	public void close() {
		System.exit(0); }
	/** Main method to create and run and instance of this class */
	public static void main(String[] a) { new Level1().run(); }
}
