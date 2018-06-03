import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import static util.Util.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
public class Main {
	Scores scores = new Scores();
	private void setupJFrame(JFrame jFrame) {
		jFrame.setContentPane(new JPanel(){{
			setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		}});
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		jFrame.setBackground(new Color(50,100,150));
		jFrame.setLayout(new BoxLayout(jFrame.getContentPane(),BoxLayout.Y_AXIS));
	}
	private void run() {
		//System.out.println("working directory: "+System.getProperty("user.dir"));
		new JFrame("Menu"){final JFrame menuFrame = this;{
			setupJFrame(this);
			List<String> includes = Arrays.asList(new String[]{
				"lwjgl/lwjgl-stb-natives-linux.jar","lwjgl/lwjgl-glfw-natives-windows.jar","lwjgl/lwjgl-assimp-javadoc.jar","lwjgl/lwjgl-glfw-sources.jar","lwjgl/lwjgl-glfw-javadoc.jar","lwjgl/lwjgl-openal-natives-macos.jar","lwjgl/lwjgl-natives-windows.jar","lwjgl/lwjgl-opengl-sources.jar","lwjgl/lwjgl-opengl-javadoc.jar","lwjgl/lwjgl-stb.jar","lwjgl/lwjgl-glfw.jar","lwjgl/lwjgl-stb-natives-macos.jar","lwjgl/lwjgl-natives-linux.jar","lwjgl/lwjgl-stb-natives-windows.jar","lwjgl/lwjgl-assimp.jar","lwjgl/lwjgl-javadoc.jar","lwjgl/lwjgl-openal.jar","lwjgl/lwjgl-stb-javadoc.jar","lwjgl/lwjgl-assimp-natives-windows.jar","lwjgl/lwjgl-assimp-natives-macos.jar","lwjgl/lwjgl-openal-sources.jar","lwjgl/lwjgl-natives-macos.jar","lwjgl/lwjgl-stb-sources.jar","lwjgl/lwjgl-openal-javadoc.jar","lwjgl/lwjgl-glfw-natives-linux.jar","lwjgl/lwjgl-assimp-sources.jar","lwjgl/lwjgl-assimp-natives-linux.jar","lwjgl/lwjgl-opengl-natives-macos.jar","lwjgl/lwjgl-openal-natives-linux.jar","lwjgl/lwjgl-glfw-natives-macos.jar","lwjgl/lwjgl-opengl.jar","lwjgl/lwjgl-opengl-natives-windows.jar","lwjgl/lwjgl-opengl-natives-linux.jar","lwjgl/lwjgl.jar","lwjgl/lwjgl-sources.jar","lwjgl/lwjgl-openal-natives-windows.jar","lwjgl/lwjgl-stb.jar","joml/joml.jar","bin"});
			String classpath = String.join(""+java.io.File.pathSeparatorChar,includes);
			add(new JPanel(){{ add(new JLabel("<html><style>body{font-size:20px;}</style><body>Menu</body></html>"){{
				setAlignmentX(Component.CENTER_ALIGNMENT);
			}});}});
			for(int x : new int[]{0,1,2})
				add(new JButton(){{
					setAlignmentX(Component.CENTER_ALIGNMENT);
					setText("Level"+x);
					addActionListener((ActionEvent e)->{
						menuFrame.setVisible(false);
						new JFrame("Level "+x){ final JFrame playScoreSelection = this; {
							setupJFrame(this);
							add(new JButton("Back"){{
								setAlignmentX(Component.CENTER_ALIGNMENT);
								addActionListener((ActionEvent e)->{
									menuFrame.setVisible(true);
									playScoreSelection.dispose();
								});
							}});
							add(new JButton("Play"){{
								setAlignmentX(Component.CENTER_ALIGNMENT);
								addActionListener((ActionEvent e)->{
									playScoreSelection.dispose();
									new JFrame(){ final JFrame namePromptFrame = this; {
										setupJFrame(this);
										setLayout(new FlowLayout());
										setDefaultCloseOperation(EXIT_ON_CLOSE);
										add(new JLabel("Player name:"));
										JTextField textField = new JTextField(){{
											setPreferredSize(new Dimension(200,20));
											setVisible(true);
										}};
										add(textField);
										add(new JButton("Back"){{
											addActionListener((ActionEvent e)->{
												menuFrame.setVisible(true);
												dispose();
											});
										}});
										add(new JButton("Continue"){{
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
												} catch(Exception a) {
													System.out.println("Exception was thrown:"+a);
													a.printStackTrace();
												}
												out.println("exited somehow");
												menuFrame.setVisible(true);
											});
										}});
										pack(); setVisible(true);
									}};
								});
							}});
							add(new JButton("Scores"){{
								setAlignmentX(Component.CENTER_ALIGNMENT);
								addActionListener((ActionEvent e)->{
									playScoreSelection.dispose();
									new JFrame("Level "+x+" Scores"){final JFrame scoresJFrame = this;{
										setupJFrame(this);
										add(new JPanel(){{add(new JLabel(scores.format(x)));}});
										add(new JButton("Back"){{
											setAlignmentX(Component.CENTER_ALIGNMENT);
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
			add(new JButton("Quit"){{
				setAlignmentX(Component.CENTER_ALIGNMENT);
				addActionListener((ActionEvent e)->{
					menuFrame.dispatchEvent(new WindowEvent(menuFrame,WindowEvent.WINDOW_CLOSING));
				});
			}});
			pack();
			setVisible(true);
		}};
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}