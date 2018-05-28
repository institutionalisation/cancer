import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import static util.Util.*;
public class Main {
	Scores scores = new Scores();
	private void run() {
		//System.out.println("working directory: "+System.getProperty("user.dir"));
		new JFrame(){final JFrame frame = this;{
			setSize(200,200);
			setTitle("Menu");
			setBackground(new Color(50,100,150));
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setLayout(new FlowLayout());
			List<String> includes = Arrays.asList(new String[]{
				"lwjgl/lwjgl-stb-natives-linux.jar","lwjgl/lwjgl-glfw-natives-windows.jar","lwjgl/lwjgl-assimp-javadoc.jar","lwjgl/lwjgl-glfw-sources.jar","lwjgl/lwjgl-glfw-javadoc.jar","lwjgl/lwjgl-openal-natives-macos.jar","lwjgl/lwjgl-natives-windows.jar","lwjgl/lwjgl-opengl-sources.jar","lwjgl/lwjgl-opengl-javadoc.jar","lwjgl/lwjgl-stb.jar","lwjgl/lwjgl-glfw.jar","lwjgl/lwjgl-stb-natives-macos.jar","lwjgl/lwjgl-natives-linux.jar","lwjgl/lwjgl-stb-natives-windows.jar","lwjgl/lwjgl-assimp.jar","lwjgl/lwjgl-javadoc.jar","lwjgl/lwjgl-openal.jar","lwjgl/lwjgl-stb-javadoc.jar","lwjgl/lwjgl-assimp-natives-windows.jar","lwjgl/lwjgl-assimp-natives-macos.jar","lwjgl/lwjgl-openal-sources.jar","lwjgl/lwjgl-natives-macos.jar","lwjgl/lwjgl-stb-sources.jar","lwjgl/lwjgl-openal-javadoc.jar","lwjgl/lwjgl-glfw-natives-linux.jar","lwjgl/lwjgl-assimp-sources.jar","lwjgl/lwjgl-assimp-natives-linux.jar","lwjgl/lwjgl-opengl-natives-macos.jar","lwjgl/lwjgl-openal-natives-linux.jar","lwjgl/lwjgl-glfw-natives-macos.jar","lwjgl/lwjgl-opengl.jar","lwjgl/lwjgl-opengl-natives-windows.jar","lwjgl/lwjgl-opengl-natives-linux.jar","lwjgl/lwjgl.jar","lwjgl/lwjgl-sources.jar","lwjgl/lwjgl-openal-natives-windows.jar","lwjgl/lwjgl-stb.jar","joml/joml.jar","bin"});
			String classpath = String.join(""+java.io.File.pathSeparatorChar,includes);
			add(new JLabel("<html><style>body{font-size:20px;}</style><body>Menu</body></html>"));
			for(int x : new int[]{0,1,2})
				add(new JButton(){{
					setText("Level"+x);
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							frame.setVisible(false);
							new JFrame(){ final JFrame namePromptFrame = this; {
								setLayout(new FlowLayout());
								add(new JLabel("Player name:"));
								JTextField textField = new JTextField(){{
									setPreferredSize(new Dimension(200,20));
									setVisible(true);
								}};
								add(textField);
								add(new JButton("Back"){{
									addActionListener(new ActionListener(){
										public void actionPerformed(ActionEvent e) {
											frame.setVisible(true);
											dispose();
										}
									});
								}});
								add(new JButton("Continue"){{
									addActionListener(new ActionListener(){
										public void actionPerformed(ActionEvent e) {
											namePromptFrame.dispose();
											try {
												Process proc = Runtime.getRuntime().exec("java -Dorg.lwjgl.util.Debug=true -cp " + classpath + " Level"+x,null,new File(System.getProperty("user.dir")));
												// https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program
												BufferedReader stdInput = new BufferedReader(new 
													InputStreamReader(proc.getInputStream()));
												BufferedReader stdError = new BufferedReader(new 
													InputStreamReader(proc.getErrorStream()));
												// read the output from the command
												String lastLine = null;
												for(String line;(line=stdInput.readLine()) != null;) {
													lastLine = line;
													System.out.println("hecc:"+line);
												}
												final String line = lastLine;
												out.println("lastLine:"+line);
												// read any errors from the attempted command
												for(String s;(s=stdError.readLine()) != null;) {
													System.out.println(s); }
												int score = Integer.parseInt(line);
												System.out.println("menu score was "+score);
												scores.addScore(x,new Score(textField.getText(),score));
											} catch(Exception a) {
												System.out.println("Exception was thrown:"+a);
												a.printStackTrace();
											}
											out.println("exited somehow");
											frame.setVisible(true);
										}
									});
								}});
								pack(); setVisible(true);
							}};
						}
					});
				}});
			add(new JButton(){{
				setText("Scores");
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

					}
				});
			}});
			add(new JButton(){{
				setText("Quit");
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING));
					}
				});
			}});
			setVisible(true);
		}};
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}