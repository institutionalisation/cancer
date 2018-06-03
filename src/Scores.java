import java.util.*;
import java.io.*;
import static util.Util.*;
import javax.swing.*;
public class Scores {
	final static int LEVEL_COUNT = 3;
	List<List<Score>> value = new ArrayList<>();
	public Scores() { exPrint(()->{
		for(int i=0; i<LEVEL_COUNT; ++i) {
			final int level = i;
			value.add(new ArrayList<Score>(){{
				BufferedReader r = new BufferedReader(new FileReader("scores/"+level));
				for(String line;(line = r.readLine()) != null;) {
					int spaceIndex = line.indexOf(' ');
					add(new Score(
						line.substring(spaceIndex+1,line.length()),
						Integer.parseInt(line.substring(spaceIndex))
					));
				}
			}});
		}
	});}
	public List<Score> levelScores(int level) {
		return value.get(level);
	}
	public void addScore(int level,Score score) {
		value.get(level).add(score);
		Collections.sort(value.get(level));
	}
	public void format(JLabel jLabel) {}
}