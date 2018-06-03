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
	private Runnable raisePlatform(Platform platform) {
		return ()->{
			long now = System.currentTimeMillis();
			int since = Math.abs((int)(now - platform.lastRaise));
			if(since<6000)
				platform.lastRaise = now
					- (platform.totalTime()/2
						- Math.abs(platform.totalTime()/2 - since));
			else
				platform.lastRaise = now;
		};
	}
	private ModelNode stage;
	private Platform redPlatform,bluePlatform,yellowPlatform;
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
			bb.colorCallbacks.put(RED,raisePlatform(redPlatform));
			bb.colorCallbacks.put(BLUE,raisePlatform(bluePlatform));
			bb.colorCallbacks.put(YELLOW,raisePlatform(yellowPlatform));
			stage.children.addAll(list(
				// first 3
				bb.new Button(RED){{
					getLocalTransform().translate(-10.3f,0,-10.8f); }},
				bb.new Button(BLUE){{
					getLocalTransform().translate(-3.13f,0,-10.8f); }},
				bb.new Button(YELLOW){{
					getLocalTransform().translate(4.05f,0,-10.8f); }},
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
					getLocalTransform().translate(.8f,0,24.6f); }}
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
