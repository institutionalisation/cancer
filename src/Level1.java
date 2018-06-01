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
	private ModelNode stage,redButton,blueButton;
	private Platform redPlatform,bluePlatform,yellowPlatform;
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
	public void inContext() {
		redPlatform = new Platform(){{ new Model("maze2/red","obj",program).rootNode.set(this); }};
		bluePlatform = new Platform(){{ new Model("maze2/blue","obj",program).rootNode.set(this); }};
		yellowPlatform = new Platform(){{ new Model("maze2/yellow","obj",program).rootNode.set(this); }};
		stage = new ModelNode(){
			long start = System.currentTimeMillis();
			{new Model("maze2/static","obj",program).rootNode.set(this);}
			private Matrix4f currentTransform = new Matrix4f();
			public Matrix4f getLocalTransform() {
				float delta = (int)(System.currentTimeMillis()-start)/1000f;
				return identityMatrix.translate(new Vector3f(0,-delta*.01f,0),currentTransform);
			}
		};
		ButtonBuilder bb = new ButtonBuilder(program);
		(blueButton = bb.create(ButtonBuilder.Color.BLUE,2000,raisePlatform(bluePlatform))).getLocalTransform()
			.translate(new Vector3f(0,0,3));
		(redButton = bb.create(ButtonBuilder.Color.RED,2000,raisePlatform(redPlatform))).getLocalTransform()
			;
		for(ModelNode x : new ModelNode[]{stage,redButton,blueButton,redPlatform,bluePlatform})
			player.colliders.add(x);
		for(ModelNode x : new ModelNode[]{stage,redButton,blueButton,redPlatform,bluePlatform})
			renderedModelNodes.add(x);
	}
	public void onReady() { exPrint(()->{
	});}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level1().run(); }
}
