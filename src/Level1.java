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
		ModelNode redButton,blueButton,yellowButton;
		redButton = bb.create(ButtonBuilder.Color.RED,raisePlatform(redPlatform));
		blueButton = bb.create(ButtonBuilder.Color.BLUE,raisePlatform(bluePlatform));
		yellowButton = bb.create(ButtonBuilder.Color.YELLOW,raisePlatform(yellowPlatform));
		stage.children.addAll(list(
			// first 3
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(-10.3f,0,-10.8f); }},
			new ModelNode(){{ set(blueButton);
				getLocalTransform().translate(-3.13f,0,-10.8f); }},
			new ModelNode(){{ set(yellowButton);
				getLocalTransform().translate(4.05f,0,-10.8f); }},
			// on the top-hat-shaped block
			new ModelNode(){{ set(yellowButton);
				getLocalTransform().translate(11.6f,0,2.9f); }},
			new ModelNode(){{ set(blueButton);
				getLocalTransform().translate(11.6f,0,5.2f); }}
		));
		bluePlatform.children.addAll(list(
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(0,0,-3.190f); }},
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(-4.641f,0,8.120f); }},
			new ModelNode(){{ set(yellowButton);
				getLocalTransform().translate(6.9f,0,21.7f); }},
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(4.9f,0,21.7f); }}
		));
		redPlatform.children.addAll(list(
			new ModelNode(){{ set(yellowButton);
				getLocalTransform().translate(-1.2f,0,8.5f); }},
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(-7f,0f,16.8f); }},
			new ModelNode(){{ set(yellowButton); 
				getLocalTransform().translate(-9f,0,16.8f); }},
			new ModelNode(){{ set(blueButton); 
				getLocalTransform().translate(-11f,0,16.8f); }}
		));
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
