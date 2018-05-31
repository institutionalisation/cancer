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
	private ModelNode stage,blueButton;
	public void inContext() {
/*
		stage = new Model("maze2","obj",program).rootNode;
		ButtonBuilder bb = new ButtonBuilder(program);
		(blueButton = bb.create(ButtonBuilder.Color.BLUE,2000)).localTransform
			.translate(new Vector3f(0,0,3));
		for(ModelNode x : new ModelNode[]{stage,blueButton})
			player.colliders.add(x);
		for(ModelNode x : new ModelNode[]{stage,blueButton})
			renderedModelNodes.add(x);
*/
	}
	public void onReady() { exPrint(()->{
	});}
	public void close() {
		System.exit(0);
	}
	public static void main(String[] a) { new Level1().run(); }
}
