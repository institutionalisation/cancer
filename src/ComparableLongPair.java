/*
 * Junyi Wang
 * June 7, 2018
 * Ms. Krasteva
 * A comparable long pair
 */

public class ComparableLongPair implements Comparable<ComparableLongPair>
{
	public final long x;
	public final long y;

	/**
	 * Creates a long pair with the given values
	 * 
	 * @param x The first value
	 * @param y The second value
	 */
	public ComparableLongPair(final long x,final long y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Checks if this long pair is equal to the given long pair
	 *
	 * @param other The long pair to check against
	 *
	 * @return true iff this long pair is equal to the given long pair
	 */
	public boolean equals(final ComparableLongPair other)
	{
		return this.x == other.x && this.y == other.y;
	}

	/**
	 * Compares this long pair against the other long pair
	 *
	 * @param other The long pair to compare against
	 *
	 * @return -1 iff this long pair goes before the given long pair, 0 if this long pair is equal to the given long pair, 1 if this long pair goes after the given long pair
	 */
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
