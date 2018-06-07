/*
 * Junyi Wang
 * June 7, 2018
 * Ms. Krasteva
 * The driver class for level 1
 */

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
	private Pathfinder pathfinder = new Pathfinder(new LineCollide("models/maze0/a.obj"),.2,.2);
	private final double farThreshold = 2.00;
	private final int pathfindMaxSteps = 1 << 20;

	/*
	 * A callback method that in run in the OpenGL thread
	 * This method initializes meshes, and starts the game logic thread
	 */
	public void inContext()
	{
		maze = new Model("maze0","obj",program).rootNode;
		renderedModelNodes.add(maze);
		oBug = new Model("bug","obj",program).rootNode;
		LineCollide c = new LineCollide("models/maze0/a.obj");
		new Thread(() -> {onReady();}).start();
	}

	/*
	 * This method contains all of the game logic,
	 * it is run from a separate thread from the renderer.
	 */
	public void onReady()
	{
		/* Creates the minion bugs and positions them appropriately */
		Bug[] bugs =
		{
			new Bug(new Matrix4f().scale(.15f),oBug,new Vector3f(-5,0,5),1,new Vector3f(-5,0,5)),
			new Bug(new Matrix4f().scale(.15f),oBug,new Vector3f(-5,0,5),1,new Vector3f(-5,0,5)),
		};
		/* Adds the current bugs to the set of rendered bugs */
		for(Bug x : bugs)
		{
			renderedModelNodes.add(x);
		}
		do
		{
			/* Finds a path to the player for all bugs */
			for(Bug x : bugs)
			{
				/* Checks if bug is activated by player */
				if(x.isActive(player.loc))
				{
					/* The bug is close enough that the player won't notice the
					 * slight discrepancy between threads */
					if(x.distToDest() < farThreshold)
					{
						x.setPath(null);
						Vector2d[] path = pathfinder.findPath(new Vector2d(x.pos.x,x.pos.z),new Vector2d(player.loc.x,player.loc.z),pathfindMaxSteps);
						x.setPath(path);
					}
					else
					/* The bug is too far; the time taken by the pathfinder
					 * is taken into account to smooth out the jump when the
					 * pathfinder finishes */
					{
						long startTime = System.nanoTime();
						Vector2d[] path = pathfinder.findPath(new Vector2d(x.pos.x,x.pos.z),new Vector2d(player.loc.x,player.loc.z),pathfindMaxSteps);
						long delta = System.nanoTime() - startTime;
						synchronized(x)
						{
							x.setPath(path);
							x.runTime(delta);
						}
					}
				}
			}
		}
		while(true);
	}
	
	/*
	 * Performs any finalizing actions and exits the level.
	 */
	public void close()
	{
		System.exit(0);
	}

	/*
	 * The main method; it creates an instance of this object
	 * and runs the appropriate method.
	 */
	public static void main(String[] a)
	{
		new Level0().run();
	}
}
