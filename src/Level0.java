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
	private Pathfinder pathfinder = new Pathfinder(new LineCollide("models/maze0/a.obj"),.2);

	public void inContext()
	{
		maze = new Model("maze0","obj",program).rootNode;
		renderedModelNodes.add(maze);
		oBug = new Model("bug","obj",program).rootNode;
		oBug.getLocalTransform().scale(.35f);
		//renderedModelNodes.add(oBug);
		LineCollide c = new LineCollide("models/maze0/a.obj");
		new Thread(() -> {onReady();}).start();
	}

	public void onReady()
	{
		Bug bug = new Bug(new Matrix4f(),oBug,new Vector3f(),1,new Vector3f());
		bug.setPath(new Vector2d[]{new Vector2d(0,0),new Vector2d(-5,-5)});
		renderedModelNodes.add(bug);
		do
		{
			bug.setPath(pathfinder.findPath(new Vector2d(bug.pos.x,bug.pos.z),new Vector2d(player.loc.x,player.loc.z)));
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
		}
		while(true);
/*
		Bug[] bugs =
		{
			new Bug(new Matrix4f(),oBug,new Vector3f(),1,new Vector3f()),
		};
		for(Bug x : bugs)
			renderedModelNodes.add(x);
		do
		{
			for(Bug x : bugs)
				if(x.isActivated())
					x.setPath(pathfinder.findPath(new Vector2d(x.pos.x,x.pos.z),new Vector2d(player.loc.x,player.loc.y)));
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
		}
		while(true);
*/
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
