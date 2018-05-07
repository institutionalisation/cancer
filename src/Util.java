import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
public class Util {
	// https://stackoverflow.com/questions/14169661/read-complete-file-without-using-loop-in-java
	static String readFile(String name) throws IOException {
		return new String(Files.readAllBytes(Paths.get(name)));
	}
}