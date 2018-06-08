import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import static util.Util.*;
import java.awt.event.*;
public class Button extends JButton { final Button button = this;
	String title;
	boolean hover = false;
	public Button(String title) {
		this.title = title;
		setHorizontalAlignment(SwingConstants.CENTER);
		new Thread(()->{for(;;)repaint();}).start();
		addMouseListener(new MouseAdapter() {
			public void mouseEntered() { hover = true;
				out.println("Enter"); }
			public void mouseExited() { hover = false;
				out.println("Exit"); }
		});
	}
	private int width = 100, height = 80;
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Dimension getSize() { return new Dimension(getWidth(),getHeight()); }
	public Dimension getPreferredSize() { return getSize(); }
	private final static int RADIUS = 40;
	private final static Color
		COLOR = new Color(184,249,199),
		HOVER_COLOR = new Color(0,0,0);
	private final static Font FONT = new Font("arial",Font.PLAIN,20);
	public void paint(Graphics g) {
		((Graphics2D)g).setBackground(new Color(0,0,0));
		g.setColor(hover?HOVER_COLOR:COLOR);
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
		// g.fillOval(0,0,20,20);
		// g.fillOval(20,0,20,20);
		// g.fillOval(40,0,20,20);
		// g.fillOval(60,0,20,20);
		// //g.fillOval(80,0,20,20);
		// //out.println(getWidth());
		// g.fillOval(90,0,20,20);
		//out.println("width:"+getWidth());
		//g.fillOval(0,,20,20);
		//g.fillRoundRect(0,0,getWidth(),getHeight(),RADIUS,RADIUS);
		//g.drawString(title,0,0);
	}
}