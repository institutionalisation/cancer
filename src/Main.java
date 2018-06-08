/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * Driver class for the whole program
 */

/*
 * Modification: move command line args to this file
 * David Jacewicz
 * June 7, 2018
 * 5 minutes
 * Version: 0.05
 */

/*
 * Modification: import resources from jar instead of directory
 * David Jacewicz
 * June 7, 2018
 * 10 minutes
 * Version: 0.05
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import static util.Util.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
public class Main {
	Scores scores;
	private void run() {
		extractResources();
		scores = new Scores();
		new Frame("Menu"){final Frame menuFrame = this;{
			List<String> includes = Arrays.asList(new String[]{
				".","lwjgl/lwjgl-stb-natives-linux.jar","lwjgl/lwjgl-glfw-natives-windows.jar","lwjgl/lwjgl-assimp-javadoc.jar","lwjgl/lwjgl-glfw-sources.jar","lwjgl/lwjgl-glfw-javadoc.jar","lwjgl/lwjgl-openal-natives-macos.jar","lwjgl/lwjgl-natives-windows.jar","lwjgl/lwjgl-opengl-sources.jar","lwjgl/lwjgl-opengl-javadoc.jar","lwjgl/lwjgl-stb.jar","lwjgl/lwjgl-glfw.jar","lwjgl/lwjgl-stb-natives-macos.jar","lwjgl/lwjgl-natives-linux.jar","lwjgl/lwjgl-stb-natives-windows.jar","lwjgl/lwjgl-assimp.jar","lwjgl/lwjgl-javadoc.jar","lwjgl/lwjgl-openal.jar","lwjgl/lwjgl-stb-javadoc.jar","lwjgl/lwjgl-assimp-natives-windows.jar","lwjgl/lwjgl-assimp-natives-macos.jar","lwjgl/lwjgl-openal-sources.jar","lwjgl/lwjgl-natives-macos.jar","lwjgl/lwjgl-stb-sources.jar","lwjgl/lwjgl-openal-javadoc.jar","lwjgl/lwjgl-glfw-natives-linux.jar","lwjgl/lwjgl-assimp-sources.jar","lwjgl/lwjgl-assimp-natives-linux.jar","lwjgl/lwjgl-opengl-natives-macos.jar","lwjgl/lwjgl-openal-natives-linux.jar","lwjgl/lwjgl-glfw-natives-macos.jar","lwjgl/lwjgl-opengl.jar","lwjgl/lwjgl-opengl-natives-windows.jar","lwjgl/lwjgl-opengl-natives-linux.jar","lwjgl/lwjgl.jar","lwjgl/lwjgl-sources.jar","lwjgl/lwjgl-openal-natives-windows.jar","lwjgl/lwjgl-stb.jar","joml/joml.jar","bin"});
			String classpath = String.join(""+java.io.File.pathSeparatorChar,includes);
			for(int x : new int[]{0,1,2})
				add(new Button("Level "+x){{
					addActionListener((ActionEvent e)->{
						menuFrame.setVisible(false);
						new Frame("Level "+x){ final Frame playScoreSelection = this; {
							add(new Button("Back"){{
								addActionListener((ActionEvent e)->{
									menuFrame.setVisible(true);
									playScoreSelection.dispose();
								});
							}});
							add(new Button("Play"){{
								addActionListener((ActionEvent e)->{
									playScoreSelection.dispose();
									new Frame(){ final Frame namePromptFrame = this; {
										setLayout(new FlowLayout());
										setDefaultCloseOperation(EXIT_ON_CLOSE);
										add(new JLabel("Player name:"));
										JTextField textField = new JTextField(){{
											setPreferredSize(new Dimension(200,20));
											setVisible(true);
										}};
										add(textField);
										add(new Button("Back"){{
											addActionListener((ActionEvent e)->{
												menuFrame.setVisible(true);
												dispose();
											});
										}});
										add(new Button("Continue"){{
											addActionListener((ActionEvent e)->{
												namePromptFrame.dispose();
												try {
													Process proc = Runtime.getRuntime().exec("java -Dorg.lwjgl.util.Debug=true -cp " + classpath + " Level"+x,null,new File(System.getProperty("user.dir")));
													// https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program
													BufferedReader stdInput = new BufferedReader(new 
														InputStreamReader(proc.getInputStream()));
													BufferedReader stdError = new BufferedReader(new 
														InputStreamReader(proc.getErrorStream()));
													new Thread(()->{exPrint(()->{
														// read any errors from the attempted command
														for(String s;(s=stdError.readLine()) != null;) {
															out.println(s); }
													});}).start();
													// read the output from the command
													String lastLine = null;
													for(String line;(line=stdInput.readLine()) != null;) {
														lastLine = line;
														out.println("hecc:"+line);
													}
													final String line = lastLine;
													out.println("lastLine:"+line);
													int score = Integer.parseInt(line);
													out.println("menu score was "+score);
													scores.addScore(x,new Score(textField.getText(),score));
												} catch(Exception exception) {
												}
												menuFrame.setVisible(true);
											});
										}});
										pack(); setVisible(true);
									}};
								});
							}});
							add(new Button("Scores"){{
								addActionListener((ActionEvent e)->{
									playScoreSelection.dispose();
									new Frame("Level "+x+" Scores"){final Frame scoresFrame = this;{
										add(new Panel(){{add(new JLabel(scores.format(x)));}});
										add(new Button("Back"){{
											addActionListener((ActionEvent e)->{
												dispose();
												playScoreSelection.dispose();
												menuFrame.setVisible(true);
											});
										}});
										pack();
										setVisible(true);
									}};
								});
							}});
							pack();
							setVisible(true);
						}};
					});
				}});
			add(new Button("Quit"){{
				setAlignmentX(Component.CENTER_ALIGNMENT);
				addActionListener((ActionEvent e)->{
					menuFrame.dispatchEvent(new WindowEvent(menuFrame,WindowEvent.WINDOW_CLOSING));
				});
			}});
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
				   	scores.save(); } });
			pack();
			setVisible(true);
		}};
	}

	/** The main method that calls everything in the program */
	public static void main(String[] args) throws Exception { new Main().run(); }
}
