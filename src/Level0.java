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
import static java.lang.Math.*;
import javax.swing.*;
import static util.Util.*;

public class Level0 extends LevelBase
{
	private ModelNode maze;
	private ModelNode oBug;
	private Pathfinder pathfinder = new Pathfinder(new LineCollide("models/maze0/a.obj"),.2,.2);
	private final double farThreshold = 2.00;
	private final int pathfindMaxSteps = 1 << 20;
	private ModelNode cube;

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
		cube = new Model("cube","obj",program).rootNode;
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
			new Bug(oBug,new Matrix4f().scale(.15f),new Vector3f(6.5f,0,4.5f),new Vector3f(2.5f,0,4.5f),2),
			new Bug(oBug,new Matrix4f().scale(.15f),new Vector3f(8.5f,0,7),new Vector3f(2,0,7),2),
			new Bug(oBug,new Matrix4f().scale(.15f),new Vector3f(8.5f,0,-6.5f),new Vector3f(8.5f,0,0.5f),2),
			new Bug(oBug,new Matrix4f().scale(.15f),new Vector3f(8.5f,0,8.5f),true),
		};
		SimpleRenderedModel boss = new SimpleRenderedModel(oBug,new Matrix4f().translate(-5,0,0).scale(2).rotateY((float)PI / 2));
		renderedModelNodes.add(boss);
		TrapBlock block1 = new TrapBlock(cube,new Matrix4f().scale(1,2,1),new Vector3f(-5f,6f,16f),new Vector3f(-5f,1f,16f),10000000000l);
		renderedModelNodes.add(block1);
		block1.start();
		/* Adds the current bugs to the set of rendered models */
		for(Bug x : bugs)
			renderedModelNodes.add(x);
		long levelStart = System.nanoTime();
		dialog("Use W,A,S,D to move around and F to talk. Press T to start the game");
		Thread logicThread = Thread.currentThread();
		Runnable notifyLogicThread = () -> {
			logicThread.interrupt();
		};
		keyboard.immediateKeys.put(GLFW_KEY_T,notifyLogicThread);
		try
		{
			Thread.sleep(100000000);
		}
		catch(InterruptedException e)
		{
		}
		Runnable talkToNothing = () ->
		{
			dialog("You: Hello...? Hello...?<br>The wall does not repond.");
		};
		keyboard.immediateKeys.put(GLFW_KEY_F,talkToNothing);
		dialog("");
		keyboard.immediateKeys.remove(GLFW_KEY_T);
		final Vector3f bossRoomTrigger = new Vector3f(0,0,-8);
		do
		{
			System.out.println(player.loc);
			/* Enters the boss room */
			if(player.loc.distance(bossRoomTrigger) < 2)
				break;
			/* Finds a path to the player for all bugs */
			for(Bug x : bugs)
			{
				/* You die! */
				if(x.pos.distance(player.loc) < 1.2)
				{
					dialog("You were caught by a bug. Try running in the other direction next time. Press T to return to the menu.");
					player.movementAllowed = false;
					System.out.println("0");
					for(Bug k : bugs)
						k.setPath(null);
					keyboard.immediateKeys.put(GLFW_KEY_T,() -> {close();});
					return;

				}
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
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{}
		}
		while(true);
		for(Bug x : bugs)
			x.setPath(null);
		keyboard.immediateKeys.put(GLFW_KEY_F,() -> {
			logicThread.interrupt();
		});
		keyboard.immediateKeys.put(GLFW_KEY_T,notifyLogicThread);
		final double bossXL = -8;
		final double bossXR = -2;
		final double bossZL = -5;
		final double bossZR = 4;
		do
		{
			if(logicThread.interrupted())
				break;
			if(bossXL <= player.loc.x && player.loc.x <= bossXR && bossZL <= player.loc.z && player.loc.z <= bossZR)
			{
				player.movementAllowed = false;
				dialog("You just walked into a giant bug. You philosophize about your life decisions while the bug's venom slowly drains the life out of you. Return to menu? (Press T)");
				System.out.println("0");
				keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
					close();
				});
				try
				{
					Thread.sleep(100000000);
				}
				catch(InterruptedException e)
				{}
				return;
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
