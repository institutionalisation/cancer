import java.awt.*;
import javax.swing.*;
import static util.Util.*;
public class Frame extends JFrame {
	private final static Color BACKGROUND_COLOR = new Color(138,196,234);
	private boolean firstElement = true;
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
		add(new Panel(){{ add(new JLabel("<html><style>body{font-size:30px;}</style><body>"+title+"</body></html>"){{
			setAlignmentX(Component.CENTER_ALIGNMENT);
		}});}});
	}
	public Frame() { this(""); }
	public void add(JComponent a) {
		if(firstElement)
			firstElement = false;
		else {
			out.println("aa");
			super.add(Box.createRigidArea(new Dimension(0,5)));
		}
		super.add(a);
	}
	public void add(Button a) {
		super.add(new Panel(){{add(a);}}); }
}