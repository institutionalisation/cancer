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
	private Bug bug;
	private Pathfinder pathfinder = new Pathfinder(2,new Line[]{new Line(new Vector2d(-20,2.37),new Vector2d(20,2.37))},.2);

	public void inContext()
	{
		maze = new Model("maze1","dae",program).rootNode;
		renderedModelNodes.add(maze);
		oBug = new Model("teapot","obj",program).rootNode;
		bug = new Bug(new Matrix4f(),oBug);
		renderedModelNodes.add(bug);
	}

	public void onReady()
	{
		Vector2d[] path = pathfinder.findPath(new Vector2d(),new Vector2d(0,5));
		bug.setPath(path);
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
