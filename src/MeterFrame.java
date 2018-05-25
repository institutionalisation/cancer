import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
public class MeterFrame extends JFrame {
	Color backgroundColor = new Color(242, 226, 109);
	final int numMeters = 1;
	Graphics g;
	public void paint(Graphics g) {
		// https://stackoverflow.com/questions/9367502/double-buffer-a-jframe
		BufferedImage bufferedImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = bufferedImage.createGraphics();
	    drawMeter(g2d,"happiness",.7f);
	    Graphics2D g2dComponent = (Graphics2D) g;
	    g2dComponent.drawImage(bufferedImage, null, 0, 0);
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
			(int)(.15*getHeight()),
			(int)(.5*getWidth()),
			(int)(.7*getHeight()));
	}
}