/*
 * David Jacewicz
 * May 20, 2018
 * Ms. Krasteva
 * A collection of utility methods
 */

/*
 * Modification: remove list() method that gives a warning
 * Junyi Wang
 * June 7, 2018
 * 30 minutes
 * Version: 0.05
 */

package util;
import java.nio.file.*;
import java.io.*;
import org.joml.*;
import java.util.*;
import java.util.zip.*;
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
	/** Extract .jar resources */
	public static void extractResources() {exPrint(()->{

		ZipInputStream in = null;
		OutputStream out = null;
		String destination= ".";
		try {
			// Open the ZIP file
			in = new ZipInputStream(Util.class.getResourceAsStream("/files.zip"));
			System.out.println("in:"+in);
			// Get the first entry
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				System.out.println("entry");
				String outFilename = entry.getName();
				// Open the output file
				if (entry.isDirectory()) {
					System.out.println("dir destination:"+destination+",outFilename:"+outFilename);
					new File(destination, outFilename).mkdirs();
					System.out.println("success");
				} else {
					System.out.println("file destination:"+destination+",outFilename:"+outFilename);
					out = new FileOutputStream(new File(destination,outFilename));
					System.out.println("success: "+out);
					System.out.println(out);
					// Transfer bytes from the ZIP file to the output file
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("couldn't open");
			// Close the stream
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		System.out.println("done zip");
	});}
}
