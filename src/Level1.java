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
	private static class Platform extends ModelNode {
		public long lastRaise;// = System.currentTimeMillis();
		public static int
			RAISE_TIME = 2000,
			HOLD_TIME = 2000;
		public int totalTime() { return RAISE_TIME*2 + HOLD_TIME; }
		public int delta() { return (int)(System.currentTimeMillis()-lastRaise); }
		private Matrix4f currentTransform = new Matrix4f();
		public Matrix4f getLocalTransform() {
			float f = Math.abs(delta()-totalTime()/2);
			f-=HOLD_TIME/2;
			f=f<0?0:f;
			f/=1000;
			return identityMatrix
				.translate(new Vector3f(0,-f*f,0),currentTransform);
		}
	}
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
		for(ModelNode button : new ModelNode[]{
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(-10.3f,0,-10.8f); }},
			new ModelNode(){{ set(blueButton);
				getLocalTransform().translate(-.75f,0,-10.8f); }},
			new ModelNode(){{ set(yellowButton);
				getLocalTransform().translate(8.8f,0,-10.8f); }},
		}) {
			renderedModelNodes.add(button);
			player.colliders.add(button);
		}
		bluePlatform.children.addAll(list(
			new ModelNode(){{ set(redButton);
				getLocalTransform().translate(0,0,-3.190f);
				bluePlatform.children.add(this); }}));
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
