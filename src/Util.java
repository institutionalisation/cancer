package util;
import java.nio.file.*;
import java.io.*;
public class Util {
	public static PrintStream out = System.out;
	public static interface ThrowingRunnable {
		public void run() throws Exception;
	}
	// https://stackoverflow.com/questions/14169661/read-complete-file-without-using-loop-in-java
	public static String readFile(String name) throws IOException {
		return new String(Files.readAllBytes(Paths.get(name)));
	}
	// print any exceptions that occur
	public static void exPrint(ThrowingRunnable r) {
		try {
			r.run(); }
		catch(Exception e) {
			out.println(e.getStackTrace()); }
	}
}