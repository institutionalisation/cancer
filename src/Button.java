/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A rounded GUI button
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import static util.Util.*;

public class Button extends JButton {
	String title;

	/**
	 * Creates a new button with the given text
	 *
	 * @param title The text to be displayed on the button
	 */
	public Button(String title) {
		this.title = title;
		setHorizontalAlignment(SwingConstants.CENTER);
		new Thread(()->{for(;;)repaint();}).start();
	}
	private int width = 100, height = 80;
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Dimension getSize() { return new Dimension(getWidth(),getHeight()); }
	public Dimension getPreferredSize() { return getSize(); }
	private final static int RADIUS = 80;
	private final static Color COLOR = new Color(184,249,199);
	private final static Font FONT = new Font("arial",Font.PLAIN,20);

	/**
	 * The method to draw the button on the screen
	 *
	 * @param g The Graphics object to draw with
	 */
	public void paint(Graphics g) {
		((Graphics2D)g).setBackground(new Color(0,0,0));
		g.setColor(COLOR);
		g.fillRoundRect(0,0,getWidth(),getHeight(),RADIUS,RADIUS);
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title,getWidth()/2-fm.stringWidth(title)/2,getHeight()/2+fm.getHeight()/4);
		g.setColor(new Color(0,0,0,0));
	}
}
