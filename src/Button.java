import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import static util.Util.*;
public class Button extends JButton {
	// private final static Color TRANSPARENT = new Color(0,0,0,0);
	// private final static Border BORDER = new Border(){
	// 	private final static int RADIUS = 30;
	// 	public Insets getBorderInsets(Component c) {
	// 		return new Insets(RADIUS+1,RADIUS+1,RADIUS+2,RADIUS+2); }
	// 	public boolean isBorderOpaque() { return true; }
	// 	public void paintBorder(Component c,Graphics g,int x,int y,int width,int height) {
	// 		//((Graphics2D)g).setBackground(TRANSPARENT);
			
	// 	}
	// };
	String title;
	public Button(String title) {
		this.title = title;
		//setOpaque(false);
		//setFocusPainted(false);
		//setContentAreaFilled(false);
		//setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setHorizontalAlignment(SwingConstants.CENTER);
		// https://stackoverflow.com/questions/423950/rounded-swing-jbutton-using-java
		//setBorder(BORDER);
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
	public void paint(Graphics g) {
		((Graphics2D)g).setBackground(new Color(0,0,0));
		g.setColor(COLOR);
		g.fillRoundRect(0,0,getWidth(),getHeight(),RADIUS,RADIUS);
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