/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A score entry
 */

public class Score implements Comparable<Score> {
	public String name;
	public int value;
	/**
	 * Creates a new score entry with the given name and value
	 *
	 * @param name The name of the player
	 * @param value The score of the player
	 */
	public Score(String name,int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Compares this score against another score
	 *
	 * @param other The score to compare against
	 *
	 * @return A negative value iff this score is less than the given score, 0 iff this score is equal to the given score, a positive value iff this score is greater than the given score
	 */
	public int compareTo(Score other) {
		return other.value-this.value;
	}
}
