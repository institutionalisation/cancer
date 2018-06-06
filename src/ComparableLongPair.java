public class ComparableLongPair implements Comparable<ComparableLongPair>
{
	public final long x;
	public final long y;

	public ComparableLongPair(final long x,final long y)
	{
		this.x = x;
		this.y = y;
	}

	public boolean equals(final ComparableLongPair other)
	{
		return this.x == other.x && this.y == other.y;
	}

	public int compareTo(final ComparableLongPair other)
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
