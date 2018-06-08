/*
 * David Jacewicz
 * May 14, 2018
 * Ms. Krasteva
 * A rounded GUI button
 */

/*
 * Modification: round borders
 * David Jacewicz
 * May 15, 2018
 * 4 hours
 * Version: 0.04
 */

/*
 * Modification: fix bug where round corners show black triangle on Windows
 * David Jacewicz
 * May 16, 2018
 * 2 hours
 * Version: 0.04
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import static util.Util.*;
import java.awt.event.*;
public class Button extends JButton { final Button button = this;
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
	private final static int RADIUS = 40;
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
		//g.fillRoundRect(0,0,getWidth(),getHeight(),RADIUS,RADIUS);
		g.fillOval(0,0,RADIUS,RADIUS);
		g.fillOval(getWidth()-RADIUS,0,RADIUS,RADIUS);
		g.fillOval(0,getHeight()-RADIUS,RADIUS,RADIUS);
		g.fillOval(getWidth()-RADIUS,getHeight()-RADIUS,RADIUS,RADIUS);
		g.fillRect(RADIUS/2,0,getWidth()-RADIUS,getHeight());
		g.fillRect(0,RADIUS/2,getWidth(),getHeight()-RADIUS);
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title,getWidth()/2-fm.stringWidth(title)/2,getHeight()/2+fm.getHeight()/4);
		g.setColor(new Color(0,0,0,0));
	}
}
