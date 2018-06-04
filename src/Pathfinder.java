import org.joml.*;
import static java.lang.Math.*;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

public class Pathfinder
{
	private final LineCollide collider;
	private final double stepSize;

	public Pathfinder(final LineCollider collider,final double stepSize)
	{
		this.collider = collider;
		this.stepSize = stepSize;
	}

	private static class Node implements Comparable<Node>
	{
		public final Vector2d pos;
		public final double heuristic;
		public final double dist;
		public final ComparableIntPair key;
		public Node prev;

		public Node(final ComparableIntPair key,final Vector2d pos,final double heuristic,final double dist)
		{
			this.key = key;
			this.pos = pos;
			this.heuristic = heuristic + dist;
			this.dist = dist;
		}

		public int compareTo(final Node other)
		{
			if(heuristic < other.heuristic)
				return -1;
			if(heuristic > other.heuristic)
				return 1;
			return 0;
		}
	}

	private void step(final PriorityQueue<Node> queue,final Set<ComparableIntPair> vis,final Vector2d start,final Vector2d dest,final Node cur,final int x,final int y) // abs(x),abs(y) <= 1
	{
		final ComparableIntPair key = new Position(cur.key.x + x,cur.key.y + y);
		if(vis.contains(key))
			return;
		final Vector2d pos = new Vector2d(cur.pos.x + x * stepSize,cur.pos.y + y * stepSize);
		if(start.distance(pos) < stepSize)
			return;
		double dist = Double.MAX_VALUE;
		Node prev = null;
		if(!collider.check(cur.pos,pos)) // edge from u to v
		{
			dist = cur.dist + stepSize;
			//System.out.println(cur + " edge " + cur.pos + " " + pos + " dist " + dist);
			prev = cur;
		}
		if(!collider.check(cur.prev.pos,pos)) // edge tightening
		{
			double newDist = cur.prev.dist + cur.prev.pos.distance(pos);
			//System.out.println(cur.prev + " tighten " + cur.prev.pos + " " + pos + " dist " + newDist);
			if(newDist < dist)
			{
				dist = newDist;
				prev = cur.prev;
			}
		}
		if(prev != null)
		{
			Node next = new Node(key,pos,dest.distance(pos),dist);
			next.prev = prev;
			queue.offer(next);
			vis.add(key);
		}
	}

	public Vector2d[] findPath(Vector2d start,Vector2d end)
	{
		Set<ComparableIntPair> vis = new TreeSet<>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		Node root = new Node(new ComparableIntPair(Long.MAX_VALUE,Long.MAX_VALUE),start,start.distance(end),0);
		root.prev = root;
		queue.offer(root);
		do
		{
			Node cur = queue.poll();
			if(cur.heuristic - cur.dist < stepSize)
			{
				System.out.println("stop at dist: " + (cur.heuristic - cur.dist) + " dist " + cur.dist + " pos " + cur.pos);
				root = cur;
				break;
			}
			step(queue,vis,start,end,cur,0,1);
			step(queue,vis,start,end,cur,0,-1);
			step(queue,vis,start,end,cur,-1,0);
			step(queue,vis,start,end,cur,1,0);
		}
		while(queue.size() != 0);
		int count = 0;
		Node cur = root;
		while(cur.prev != cur)
		{
			cur = cur.prev;
			count++;
		}
		Vector2d[] path = new Vector2d[count + 2];
		int index = count;
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
