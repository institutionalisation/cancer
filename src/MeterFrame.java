/*
 * David Jacewicz
 * May 17, 2018
 * Ms. Krasteva
 * A window displaying the meters for Level 2
 */

/*
 * Modification: fix bug where meter goes over the end
 * David Jacewicz
 * May 19, 2018
 * 2 hours
 * Version: 0.03
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
public class MeterFrame extends JFrame {
	Color backgroundColor = new Color(242, 226, 109);
	final int numMeters = 1;
	Graphics g;
	Map<String,Meter> meters = new TreeMap<>();
	public List<Runnable> emptyCallbacks = new ArrayList<>();
	public GLWindow.BoundCallback boundsCallback = new GLWindow.BoundCallback() {
		/**
		 * Adjusts this window when the main window is moved or resized
		 *
		 * @param window The main window
		 */
		public void invoke(GLWindow window) {
			setBounds(window.x+window.width,window.y,window.height/4 * meters.size(),window.height); } };
	/**
	 * Draws this window with the given graphics object
	 *
	 * @param g The graphics object to draw with
	 */
	public void paint(Graphics g) {
		BufferedImage bufferedImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
	    Graphics imageGraphics = bufferedImage.createGraphics();
	    Set<String> names = meters.keySet();
	    long now = System.currentTimeMillis();
	    int i = 0;
	    for(String name : names) {
	    	Meter x = meters.get(name);
	    	float fill = 1-x.leakRate*(now-x.lastRefill)/1000;
	    	if(fill<0)
	    		for(Runnable y : emptyCallbacks)
	    			y.run();
	    	drawMeter(imageGraphics,name,fill,i);
	    	++i;
	    }
	    g.drawImage(bufferedImage,0,0,null);
	}
	// TODO: one letter variable names
	/**
	 * Draws a single meter with the given graphics object, name and state
	 *
	 * @param g The graphics object to draw with
	 * @param name The name of the meter
	 * @param value The fullness of the meter
	 * @param index Index of the meter; used for positioning
	 */
	private void drawMeter(Graphics g,String name,float value,int index) {
		int width = getWidth()/meters.size(),
			height = getHeight();
		g.setColor(backgroundColor);
		g.fillRect(width*index,0,width,height);
		g.setColor(Color.GRAY);
		// back panel
		g.fillRect(
			(int)(.2*width + width*index),
			(int)(.1*height),
			(int)(.6*width),
			(int)(.8*height));
		// bottom name panel
		g.fillRect(
			(int)(.1*width + width*index),
			(int)(.85*height),
			(int)(.8*width),
			(int)(.1*height));
		g.setColor(new Color(247, 50, 32));
		// vial inside
		g.fillRect(
			(int)(.25*width + width*index),
			(int)((.15+.7*(1-value))*height),
			(int)(.5*width),
			(int)(.7*value*height));
		g.setColor(Color.BLACK);
		g.setFont(new Font("arial",Font.PLAIN,height/30));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(
			name,
			(int)(.5*width - fontMetrics.stringWidth(name)/2  +  width*index),
			(int)(.9*height + fontMetrics.getHeight()/4));
	}
}
