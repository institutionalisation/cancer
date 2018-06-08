/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * The score tracker class
 */

/*
 * Modification: render scoreboard to HTML
 * David Jacewicz
 * June 8, 2018
 * 20 minutes
 * Version: 0.05
 */

import java.util.*;
import java.io.*;
import static util.Util.*;
import javax.swing.*;
public class Scores {
	final static int LEVEL_COUNT = 3;
	List<List<Score>> value = new ArrayList<>();
	/** Creates a score tracker */
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
	/**
	 * Finds a list of scores for the specified level
	 *
	 * @param level The level to find scores for
	 *
	 * @return A list of score objects for the specified levl
	 */
	public List<Score> levelScores(int level) {
		return value.get(level);
	}
	/**
	 * Adds a score entry for the specified level
	 *
	 * @param level The level number to add to
	 * @param score The score entry to add
	 */
	public void addScore(int level,Score score) {
		value.get(level).add(score);
		Collections.sort(value.get(level));
	}
	/**
	 * Creates an HTML representation of the scores for the given level
	 *
	 * @param level The level to create the HTML for
	 *
	 * @return String The HTML represetation of the scores for the given level as a string
	 */
	public String format(int level) {
		if(value.get(level).size()==0)
			return "<html><style>body{font-size:15px}</style><body>No scores for level "+level+"</body></html>";
		String ret =
			"<html>"+
				"<style>"+
					"body{font-size:15px}"+
					"th{font-size:20px; border-style:solid; border-width:2px;}"+
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

	/** Saves the scores contained in this object to disk */
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
