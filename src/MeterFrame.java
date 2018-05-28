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
		public void invoke(GLWindow w) {
			setBounds(w.x+w.width,w.y,w.height/5 * meters.size(),w.height); } };
	public void paint(Graphics g) {
		// https://stackoverflow.com/questions/9367502/double-buffer-a-jframe
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
	private void drawMeter(Graphics g,String name,float value,int i) {
		int width = getWidth()/meters.size(),
			height = getHeight();
		g.setColor(backgroundColor);
		g.fillRect(width*i,0,width,height);
		g.setColor(Color.GRAY);
		// back panel
		g.fillRect(
			(int)(.2*width + width*i),
			(int)(.1*height),
			(int)(.6*width),
			(int)(.8*height));
		// bottom name panel
		g.fillRect(
			(int)(.1*width + width*i),
			(int)(.85*height),
			(int)(.8*width),
			(int)(.1*height));
		g.setColor(new Color(247, 50, 32));
		// vial inside
		g.fillRect(
			(int)(.25*width + width*i),
			(int)((.15+.7*(1-value))*height),
			(int)(.5*width),
			(int)(.7*value*height));
		g.setColor(Color.BLACK);
		g.setFont(new Font("arial",Font.PLAIN,height/30));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(
			name,
			(int)(.5*width - fontMetrics.stringWidth(name)/2  +  width*i),
			(int)(.9*height + fontMetrics.getHeight()/4));
	}
}