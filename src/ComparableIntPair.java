/*
 * Junyi Wang
 * June 7, 2018
 * Ms.Krasteva
 * A comparable int pair
 */

/*
 * Modification: implement equals for Java TreeSet
 * Junyi Wang
 * June 8, 2018
 * 2 minutes
 * Version: 0.05
 */

public class ComparableIntPair implements Comparable<ComparableIntPair>
{
	public final int x;
	public final int y;

	/**
	 * Creates a new comparable int pair with the given ints
	 *
	 * @param x The first int value
	 * @param y The second int value
	 */
	public ComparableIntPair(final int x,final int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Checks if this int pair is equal to the given int pair
	 * 
	 * @param other The int pair to compare against
	 *
	 * @return true iff the given int pair is equal to this int pair
	 */
	public boolean equals(final ComparableIntPair other)
	{
		return this.x == other.x && this.y == other.y;
	}

	/**
	 * Compares this int pair to the given int pair
	 *
	 * @param other The int pair to compare against
	 *
	 * @return -1 iff this int pair goes before the other int pair, 0 iff they are equal, 1 iff this int pair goes after the other int pair
	 */
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
