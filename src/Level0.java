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
	private final double farThreshold = 3.00;
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
		cube = new Model("cube","obj",program).rootNode;
		player.colliders.add(maze);
		new Thread(() -> {onReady();}).start();
	}

	/**
	 * Makes a bug chase the player
	 *
	 * @param bug The bug to chase the player with
	 */
	private void chasePlayer(Bug bug)
	{
		/* The bug is close enough that the player won't notice the
		 * slight discrepancy between threads */
		if(bug.distToDest() < farThreshold)
		{
			bug.setPath(null);
			Vector2d[] path = pathfinder.findPath(new Vector2d(bug.pos.x,bug.pos.z),new Vector2d(player.loc.x,player.loc.z),pathfindMaxSteps);
			bug.setPath(path);
		}
		else
		/* The bug is too far; the time taken by the pathfinder
		 * is taken into account to smooth out the jump when the
		 * pathfinder finishes */
		{
			long startTime = System.nanoTime();
			Vector2d[] path = pathfinder.findPath(new Vector2d(bug.pos.x,bug.pos.z),new Vector2d(player.loc.x,player.loc.z),pathfindMaxSteps);
			long delta = System.nanoTime() - startTime;
			synchronized(bug)
			{
				bug.setPath(path);
				bug.runTime(delta);
			}
		}
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
		player.movementAllowed = false;
		player.loc.x = 2;
		player.loc.z = 8.5f;
		SimpleRenderedModel boss = new SimpleRenderedModel(oBug,new Matrix4f().translate(-5,0,0).scale(2).rotateY((float)PI / 2));
		renderedModelNodes.add(boss);
		TrapBlock block1 = new TrapBlock(cube,new Matrix4f().scale(1,2,1),new Vector3f(-5f,6f,16f),new Vector3f(-5f,1f,16f),500000000l);
		final Vector3f trapTrigger = new Vector3f(-5,0,16);
		renderedModelNodes.add(block1);
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
		player.movementAllowed = true;
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
			if(player.loc.distance(bossRoomTrigger) < 3)
				break;
			/* Finds a path to the player for all bugs */
			for(Bug x : bugs)
			{
				/* You die! */
				if(x.pos.distance(player.loc) < 2)
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
					chasePlayer(x);
			}
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{}
		}
		while(true);
		Vector2d[] bugBRpos =
		{
			new Vector2d(-1.7,-7.7),
			new Vector2d(-2.2,-7.6),
			new Vector2d(-2.7,-8),
			new Vector2d(-1.5,-8.6),
		};
		for(int i = 0;i < bugs.length;i++)
			bugs[i].setPath(pathfinder.findPath(new Vector2d(bugs[i].pos.x,bugs[i].pos.z),bugBRpos[i],pathfindMaxSteps));
		keyboard.immediateKeys.put(GLFW_KEY_F,() -> {
			logicThread.interrupt();
		});
		keyboard.immediateKeys.put(GLFW_KEY_T,notifyLogicThread);
		final double bossXL = -8;
		final double bossXR = -2;
		final double bossZL = -5;
		final double bossZR = 4;
		final Vector3f escapeTrigger = new Vector3f(-5f,0,-9.5f);
		boolean attemptEscape = false;
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
				return;
			}
			if(player.loc.distance(trapTrigger) < 2)
			{
				player.movementAllowed = false;
				block1.start();
				dialog("You were crushed by a falling boulder.<br>You have always wondered what the inside of a giant boulder looks like, well, today you found out! Return to menu? (Press T)");
				System.out.println("0");
				keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
					close();
				});
				return;
			}
			if(escapeTrigger.distance(player.loc) < 3)
			{
				bugs[0].speed = 0.01;
				attemptEscape = true;
			}
			for(Bug x : bugs)
			{
				/* You die! */
				if(x.pos.distance(player.loc) < 2)
				{
					dialog("You were caught by a bug. Try running in the other direction next time. Press T to return to the menu.");
					player.movementAllowed = false;
					System.out.println("0");
					for(Bug k : bugs)
						k.setPath(null);
					keyboard.immediateKeys.put(GLFW_KEY_T,() -> {close();});
					return;

				}
			}
			if(attemptEscape)
				chasePlayer(bugs[0]);
		}
		while(true);
		keyboard.immediateKeys.remove(GLFW_KEY_F);
		dialog("Boss: I don't know where you are going in such a hurry, but let me deactivate the trap first. (Press T to continue)");
		keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
			dialog("Boss: Make sure you don't touch my legs while you pass through, they are very venomous. The little bugs don't realize how dangerous they are, try not to catch their attention by taking the small exit! (Press T to continue");
			keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
				dialog("Boss: Good luck wherever you're going!");
			});
		});
		final Vector3f winTrigger = new Vector3f(-5,0,25);
		do
		{
			if(bossXL <= player.loc.x && player.loc.x <= bossXR && bossZL <= player.loc.z && player.loc.z <= bossZR)
			{
				player.movementAllowed = false;
				dialog("You just walked into a giant bug. You philosophize about your life decisions while the bug's venom slowly drains the life out of you. Return to menu? (Press T)");
				System.out.println("0");
				keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
					close();
				});
				return;
			}
			if(escapeTrigger.distance(player.loc) < 3)
			{
				bugs[0].speed = 0.02;
				attemptEscape = true;
			}
			for(Bug x : bugs)
			{
				/* You die! */
				if(x.pos.distance(player.loc) < 2)
				{
					dialog("You were caught by a bug. Try running in the other direction next time. Press T to return to the menu.");
					player.movementAllowed = false;
					System.out.println("0");
					for(Bug k : bugs)
						k.setPath(null);
					keyboard.immediateKeys.put(GLFW_KEY_T,() -> {close();});
					return;

				}
			}
			if(attemptEscape)
				chasePlayer(bugs[0]);
			if(player.loc.distance(winTrigger) < 3)
			{
				player.movementAllowed = false;
				bugs[0].setPath(null);
				long score = abs(System.nanoTime() - levelStart);
				dialog("You finished the level with a final score of " + score + ". Press T to return to the main menu");
				System.out.println(score);
				keyboard.immediateKeys.put(GLFW_KEY_T,() -> {
					close();
				});
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
