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
import static util.Util.*;
public class Level0 extends LevelBase
{
	private ModelNode maze;
	private ModelNode oBug;
	private ModelNode bug;

	public void inContext()
	{
		maze = new Model("maze1","dae",program).rootNode;
		renderedModelNodes.add(maze);
		oBug = new Model("teapot","obj",program).rootNode;
		bug = new ModelNode()
		{
			long startTime = System.nanoTime();
			Matrix4f origXlat = new Matrix4f();
			Matrix4f curPos = new Matrix4f();
			{
				oBug.set(this);
				//getLocalTransform().scale(.2f);
				renderedModelNodes.add(this);
			}
			@Override
			public Matrix4f getLocalTransform()
			{
				long delta = System.nanoTime() - startTime;
				System.out.println("orig" + origXlat);
				return origXlat.translate(new Vector3f(delta * .000000001f,0,0),curPos);
			}
		};
	}

	public void onReady()
	{
	}
	
	public void close()
	{
		System.exit(0);
	}

	public static void main(String[] a)
	{
		new Level0().run();
	}
}
