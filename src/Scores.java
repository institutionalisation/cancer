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
						Integer.parseInt(line.substring(0,spaceIndex))
					));
				}
				Collections.sort(this);
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
	public String format(int level) {
		String ret =
			"<html>"+
				"<style>"+
					"body{font-size:15px}"+
					//"th, td {border: 1px solid black;}"+
					"th{font-size:20px;}"+
				"</style>"+
				"<body><table>"+
					"<tr><th>Name</th><th>Score</th></tr>";
		for(Score x : value.get(level))
			ret +=
					"<tr><td>"+x.name+"</td><td>"+x.value+"</td></tr>";
		ret +=
				"</table></body>"+
			"</html>";
		return ret;
	}
	public void save() {exPrint(()->{
		for(int i=0; i<LEVEL_COUNT; ++i) {
			PrintWriter w = new PrintWriter(new FileWriter("scores/"+i));
			for(Score x : value.get(i)) {
				w.println(x.value+" "+x.name);
			}
			w.close();
		}
	});}
}