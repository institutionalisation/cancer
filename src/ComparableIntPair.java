public class ComparableIntPair implements Comparable<ComparableIntPair>
{
	public final int x;
	public final int y;

	public ComparableIntPair(final int x,final int y)
	{
		this.x = x;
		this.y = y;
	}

	public boolean equals(final ComparableIntPair other)
	{
		return this.x == other.x && this.y == other.y;
	}

	public int compareTo(final ComparableIntPair other)
	{
		if(this.x < other.x)
			return -1;
		if(other.x < this.x)
			return 1;
		if(this.y < other.y)
			return -1;
		if(other.y < this.y)
			return 1;
		return 0;
	}
}
