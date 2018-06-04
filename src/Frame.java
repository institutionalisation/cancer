import java.awt.*;
import javax.swing.*;
public class Frame extends JFrame {
	private final static Color BACKGROUND_COLOR = new Color(138,196,234);
	public Frame(String title) {
		super(title);
		setContentPane(new JPanel(){{
			setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		}});
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBackground(new Color(50,100,150));
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		getContentPane().setBackground(BACKGROUND_COLOR);
	}
	public Frame() { this(""); }
}