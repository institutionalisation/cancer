/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A collection of utility methods
 */

package util;
import java.nio.file.*;
import java.io.*;
import org.joml.*;
import java.util.*;
public class Util {
	public static Matrix4f identityMatrix = new Matrix4f();
	public static PrintStream out = System.out;
	public static interface ThrowingRunnable {
		public void run() throws Exception;
	}
	/**
	 * Reads the entire file given into a string
	 *
	 * @param name The file to read
	 *
	 * @return The contents of the file as a string
	 */
	public static String readFile(String name) throws IOException {
		return new String(Files.readAllBytes(Paths.get(name)));
	}
	/**
	 * Catch and print any exceptions that occur during the execution of the given runnable
	 *
	 * @param runnable The runnable to be run inside the try-catch
	 */
	public static void exPrint(ThrowingRunnable runnable) {
		try {
			runnable.run(); }
		catch(Exception exception) {
			exception.printStackTrace(); }
	}
}
