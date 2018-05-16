import java.io.FileReader;
import java.io.FileNotFoundException;
public class Bitmap {
	public Bitmap(String filename) {
		FileReader fr = null;
			try { new FileReader(filename); }
				catch(FileNotFoundException e) {}
		char[] header = new char[54];

	}
}