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
	// https://stackoverflow.com/questions/14169661/read-complete-file-without-using-loop-in-java
	public static String readFile(String name) throws IOException {
		return new String(Files.readAllBytes(Paths.get(name)));
	}
	// print any exceptions that occur
	public static void exPrint(ThrowingRunnable r) {
		try {
			r.run(); }
		catch(Exception e) {
			e.printStackTrace(); }
	}
	public static <E> List<E> list(E... a) {
		return Arrays.asList(a); }
	// https://stackoverflow.com/questions/9464843/how-to-extract-zip-file-from-jar-file
	public static void extractResources() {exPrint(()->{
		if(new File("models").exists())
			return;
		String destination = ".";
		ZipInputStream in = null;
		OutputStream out = null;
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
					new File(destination, outFilename).mkdirs();
				} else {
					out = new FileOutputStream(new File(destination,outFilename));
					// Transfer bytes from the ZIP file to the output file
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
				}
			}
		} finally {
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
