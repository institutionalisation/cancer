public class Score implements Comparable<Score> {
	public String name;
	public int value;
	public Score(String name,int value) {
		this.name = name;
		this.value = value;
	}
	public int compareTo(Score a) {
		return a.value-this.value;
	}
}