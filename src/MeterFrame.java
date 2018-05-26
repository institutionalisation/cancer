import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class MeterFrame extends JFrame {
	Color backgroundColor = new Color(242, 226, 109);
	final int numMeters = 1;
	Graphics g;
	Map<String,Meter> meters = new TreeMap<>();
	public void paint(Graphics g) {
		// https://stackoverflow.com/questions/9367502/double-buffer-a-jframe
		BufferedImage bufferedImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
	    Graphics imageGraphics = bufferedImage.createGraphics();
	    Set<String> names = meters.keySet();
	    long now = System.currentTimeMillis();
	    for(String name : names) {
	    	Meter x = meters.get(name);
	    	drawMeter(imageGraphics,name,
	    		1f-x.leakRate*(now-x.lastRefill)/1000);
	    }
	    g.drawImage(bufferedImage,0,0,null);
	}
	private void drawMeter(Graphics g,String name,float value) {
		g.setColor(backgroundColor);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.GRAY);
		// back panel
		g.fillRect(
			(int)(.2*getWidth()),
			(int)(.1*getHeight()),
			(int)(.6*getWidth()),
			(int)(.8*getHeight()));
		// bottom name panel
		g.fillRect(
			(int)(.1*getWidth()),
			(int)(.85*getHeight()),
			(int)(.8*getWidth()),
			(int)(.1*getHeight()));
		g.setColor(new Color(247, 50, 32));
		// vial inside
		g.fillRect(
			(int)(.25*getWidth()),
			(int)((.15+.7*(1-value))*getHeight()),
			(int)(.5*getWidth()),
			(int)(.7*value*getHeight()));
		g.setColor(Color.BLACK);
		g.setFont(new Font("arial",Font.PLAIN,getHeight()/30));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(
			name,
			(int)(.5*getWidth() - fontMetrics.stringWidth(name)/2),
			(int)(.9*getHeight() + fontMetrics.getHeight()/4));
	}
}