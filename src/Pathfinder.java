/*
 * Junyi Wang
 * June 7, 2018
 * Ms. Krasteva
 * A* grid pathfinder
 */

/*
 * Modification: fix bug where path clips through wall
 * Junyi Wang
 * June 7, 2018
 * 20 minutes
 * Version: 0.05
 */

/*
 * Modification: fix bug where pathfinder gets stuck due to floating point inaccuracy
 * Junyi Wang
 * June 7, 2018
 * 40 minutes
 * Version: 0.05
 */

/*
 * Modification: clean up code and add JavaDoc
 * Junyi Wang
 * June 8, 2018
 * 4 minutes
 * Version: 0.05
 */

import org.joml.*;
import static java.lang.Math.*;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

public class Pathfinder
{
	private final LineCollide collider;
	private final double stepSize;
	private final double width;

	/**
	 * Creates a new pathfinder with the line collision data structure provided 
	 *
	 * @param collider The line collision data structure
	 * @param stepSize The distance the pathfinder can move in one step
	 * @param width The width of the object the path is being found for
	 */
	public Pathfinder(final LineCollide collider,final double stepSize,final double width)
	{
		this.collider = collider;
		this.stepSize = stepSize;
		this.width = width;
	}

	/* A pathfinding node class, used in the priority queue */
	private static class Node implements Comparable<Node>
	{
		public final Vector2d pos;
		public final double heuristic;
		public final double dist;
		public final ComparableLongPair key;
		public Node prev;

		/**
		 * Creates a new pathfinder node
		 *
		 * @param key The position of this node on the pathfinding grid; used as key in set to determine whether this node has been visited
		 * @param pos The continous 2D space position of this node
		 * @param heuristic The heuristic used for A*, calculated as the Euclidean distance to the destination 
		 * @param dist The distance already travelled; used to minimize the lengths of paths found
		 */
		public Node(final ComparableLongPair key,final Vector2d pos,final double heuristic,final double dist)
		{
			this.key = key;
			this.pos = pos;
			this.heuristic = heuristic + dist;
			this.dist = dist;
		}

		/**
		 * Compares this node with another node based on
		 * the possible length of paths found by using
		 * the node; the one that will result in a shorter
		 * path comes first
		 *
		 * @param other The node to compare this node to */
		public int compareTo(final Node other)
		{
			if(heuristic < other.heuristic)
				return -1;
			if(heuristic > other.heuristic)
				return 1;
			return 0;
		}
	}

	/**
	 * Checks whether the specified move is possible;
	 * 
	 * @param pointA The point to move from
	 * @param pointB The point to move to
	 */
	private boolean check(Vector2d pointA,Vector2d pointB)
	{
		/* Checks if this move would travel through a wall */
		if(collider.check(pointA,pointB))
			return false;
		/* Checks if this move would bring the entity partially
		 * inside a wall */
		if(collider.dist(pointA,pointB) < width)
			return false;
		return true;
	}

	/**
	 * Takes one step in the specified direction if possible;
	 * also attempts to minimize existing paths to the specified point
	 *
	 * @param queue The new pathfinding node is appended to this queue
	 * @param vis This set is used to detect whether or not a point has already been visited
	 * @param dest The destination; used to calculate hueristic
	 * @param cur The current pathfinding node
	 * @param x The x distance in grid cells to move; must be either 1 or -1
	 * @param y The y distance in grid cells to move; must be either 1 or -1
	 */
	private void step(final PriorityQueue<Node> queue,final Set<ComparableLongPair> vis,final Vector2d dest,final Node cur,final int x,final int y)
	{
		final ComparableLongPair key = new ComparableLongPair(cur.key.x + x,cur.key.y + y);
		if(vis.contains(key))
			return;
		final Vector2d pos = new Vector2d(cur.pos.x + x * stepSize,cur.pos.y + y * stepSize);
		double dist = Double.MAX_VALUE;
		Node prev = null;
		/* Normal edge */
		if(check(cur.pos,pos))
		{
			dist = cur.dist + stepSize;
			prev = cur;
		}
		/* Tightened edge */
		if(check(cur.prev.pos,pos))
		{
			dist = cur.prev.dist + cur.prev.pos.distance(pos);
			prev = cur.prev;
		}
		/* Adds a node to the queue if possible */
		if(prev != null)
		{
			Node next = new Node(key,pos,dest.distance(pos),dist);
			next.prev = prev;
			queue.offer(next);
			vis.add(key);
		}
	}

	/**
	 * Finds a path between the given two points, taking at most maxSteps steps
	 * 
	 * @param start The starting point
	 * @param end The destination
	 * @param maxSteps The maximum number of pathfinding steps to take before giving up.
	 */
	public Vector2d[] findPath(Vector2d start,Vector2d end,int maxSteps)
	{
		Set<ComparableLongPair> vis = new TreeSet<>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		Node root = new Node(new ComparableLongPair(Long.MAX_VALUE,Long.MAX_VALUE),start,start.distance(end),0);
		root.prev = root;
		queue.offer(root);
		boolean found = false;
		do
		{
			Node cur = queue.poll();
			/* Can this node directly reach the destination? */
			if(cur.heuristic - cur.dist < stepSize && check(cur.pos,end))
			{
				root = cur;
				found = true;
				break;
			}
			step(queue,vis,end,cur,0,1);
			step(queue,vis,end,cur,0,-1);
			step(queue,vis,end,cur,-1,0);
			step(queue,vis,end,cur,1,0);
			/* Enough steps have been taken,
			 * but no path has been found */
			if(--maxSteps == 0)
				return null;
		}
		while(queue.size() != 0);
		/* All possible paths have been explored,
		 * but no path has been found */
		if(!found)
			return null;
		int count = 0;
		Node cur = root;
		/* Count the number of elements in the path
		 * stored as a linked list */
		while(cur.prev != cur)
		{
			cur = cur.prev;
			count++;
		}
		Vector2d[] path = new Vector2d[count + 2];
		int index = count;
		/* Extract the path stored as a linked list
		 * into an array */
		while(root.prev != root)
		{
			path[index--] = root.pos;
			root = root.prev;
		}
		path[0] = start;
		path[count + 1] = end;
		return path;
	}
}
