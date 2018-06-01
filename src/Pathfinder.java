import org.joml.*;
import static java.lang.Math.*;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

public class Pathfinder
{
	private final LineCollide collider;
	private final double stepSize;

	public Pathfinder(final double extWidth,final Line[] walls,final double stepSize)
	{
/*
		Line[] lines = new Line[walls.length * 4];
		for(int i = 0;i < walls.length;i++)
		{
			double xsq = walls[i].pointA.x - walls[i].pointB.x;
			double ysq = walls[i].pointA.y - walls[i].pointB.y;
			xsq *= xsq;
			ysq *= ysq;
			double cosA = ysq / (xsq + ysq);
			double sinA = sqrt(1 - cosA);
			cosA = sqrt(cosA);
			double xExt = cosA * extWidth;
			double yExt = sinA * extWidth;
			Line parallelA = new Line(new Vector2d(walls[i].pointA.x + xExt,walls[i].pointA.y + yExt),new Vector2d(walls[i].pointB.x + xExt,walls[i].pointB.y + yExt));
			Line parallelB = new Line(new Vector2d(walls[i].pointA.x - xExt,walls[i].pointA.y - yExt),new Vector2d(walls[i].pointB.x - xExt,walls[i].pointB.y - yExt));
			Line perpA = new Line(parallelA.pointA,parallelB.pointA);
			Line perpB = new Line(parallelA.pointB,parallelB.pointB);
			lines[i * 4] = parallelA;
			lines[i * 4 + 1] = parallelB;
			lines[i * 4 + 2] = perpA;
			lines[i * 4 + 3] = perpB;
		}
*/
		collider = new LineCollide(walls);
		this.stepSize = stepSize;
	}

	private static class Position implements Comparable<Position>
	{
		public final long x;
		public final long y;

		public Position(final long x,final long y)
		{
			this.x = x;
			this.y = y;
		}

		public int compareTo(final Position other)
		{
			if(x < other.x)
				return -1;
			if(x > other.x)
				return 1;
			if(y < other.y)
				return -1;
			if(y > other.y)
				return 1;
			return 0;
		}

		public boolean equals(final Position other)
		{
			return x == other.x && y == other.y;
		}
	}

	private static class Node implements Comparable<Node>
	{
		public final Vector2d pos;
		public final double heuristic;
		public final double dist;
		public final Position key;
		public Node prev;

		public Node(final Position key,final Vector2d pos,final double heuristic,final double dist)
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

	private void step(final PriorityQueue<Node> queue,final Set<Position> vis,final Vector2d start,final Vector2d dest,final Node cur,final int x,final int y) // abs(x),abs(y) <= 1
	{
		final Position key = new Position(cur.key.x + x,cur.key.y + y);
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
		Set<Position> vis = new TreeSet<>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		Node root = new Node(new Position(Long.MAX_VALUE,Long.MAX_VALUE),start,start.distance(end),0);
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
