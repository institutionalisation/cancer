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
	private ModelNode stage,base0,base1,redUnpressed,redPressed,buttonSlot;
	public void inContext() {
		stage = new Model("maze2","obj",program).rootNode;
		redPressed = new Model("button/center-pressed/red","obj",program).rootNode;
		redUnpressed = new Model("button/center-unpressed/red","obj",program).rootNode;
		buttonSlot = new ModelNode(){
			boolean[] pressed = new boolean[]{false};
		{
			redUnpressed.set(this);
			collisionCallbacks.add(()->{
				if(!pressed[0]) {
					pressed[0] = true;
					redPressed.set(this);
					new Thread(()->{exPrint(()->{
						Thread.sleep(2000);
						redUnpressed.set(this);
						pressed[0] = false;
					});}).start();
				}
			});
		}};
		base0 = new Model("button/base","obj",program).rootNode;
		base1 = new ModelNode(){{
			base0.set(this);
			localTransform
				.translate(new Vector3f(3,0,0));
		}};
		for(ModelNode x : new ModelNode[]{stage,base0,base1,buttonSlot})
			player.colliders.add(x);
		for(ModelNode x : new ModelNode[]{stage,base0,base1,buttonSlot})
			renderedModelNodes.add(x);
	}
	public void onReady() { exPrint(()->{
	});}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level1().run(); }
}