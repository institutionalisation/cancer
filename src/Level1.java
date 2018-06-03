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
public class Level1 extends LevelBase { final Level1 level1 = this;
	private ModelNode stage;
	private Platform redPlatform,bluePlatform,yellowPlatform;
	private enum State { TO_END,TO_START };
	public void inContext() {
		redPlatform = new Platform(){{ set(new Model("maze2/red","obj",program).rootNode); }};
		bluePlatform = new Platform(){{ set(new Model("maze2/blue","obj",program).rootNode); }};
		yellowPlatform = new Platform(){{ set(new Model("maze2/yellow","obj",program).rootNode); }};
		stage = new Model("maze2/static","obj",program).rootNode;
		renderedModelNodes.addAll(list(
			stage,
			redPlatform,bluePlatform,yellowPlatform));
		player.colliders.addAll(list(
			stage,redPlatform,bluePlatform,yellowPlatform));
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
			stage.children.addAll(list(
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
					getLocalTransform().translate(8.6f,0,34.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(8.7f,0,30.7f); }}
			));
			bluePlatform.children.addAll(list(
				bb.new Button(RED){{
					getLocalTransform().translate(0,0,-3.190f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(-4.641f,0,8.120f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(.8f,0,24.6f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(3.6f,0,25.1f); }}
			));
			redPlatform.children.addAll(list(
				bb.new Button(YELLOW){{
					getLocalTransform().translate(-1.2f,0,8.5f); }},
				bb.new Button(RED){{
					getLocalTransform().translate(-7f,0f,16.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(-9f,0,16.8f); }},
				bb.new Button(BLUE){{
					getLocalTransform().translate(-11f,0,16.8f); }}
			));
		}
		// needs to be accessed from anonymous classes
		State[] state = new State[]{State.TO_END};
		keyboard.immediateKeys.put(GLFW_KEY_R,new Runnable() {
			public void run() {
				dialog("Restarted.");
				state[0] = State.TO_END;
				player.loc.set( 9.2f,1.5f,-10.8f);
				// move away all the platforms
				for(Platform x : list(redPlatform,bluePlatform,yellowPlatform))
					x.lastRaise = 0;
			}
			{run();}
		});

		new Thread(()->{exPrint(()->{onReady();});}).start();
		new Thread(()->{exPrint(()->{
			for(;;) {
				switch(state[0]) {
					case TO_END:
						if(37 < player.loc.z) {
							dialog("Good job, now make it back!");
							state[0] = State.TO_START;
						}
						break;
					case TO_START:
						if(player.loc.z<-9) {
							dialog("You win!");
							System.exit(0);
						}
				}
				if(player.loc.y<-10)
					dialog("You fell off the stage, press R to restart.");
				Thread.sleep(200);
			}
		});}).start();
	}
	public void onReady() { exPrint(()->{
		for(;;) {
			Thread.sleep(300);
			out.println(player.loc+" dy:"+player.dy);
		}
	});}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level1().run(); }
}
