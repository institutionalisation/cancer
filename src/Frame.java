/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * A Swing window
 */

/*
 * Modification: fix resize after frame modification
 * David Jacewicz
 * June 8, 2018
 * 10 minutes
 * Version: 0.05
 */

import java.awt.*;
import javax.swing.*;
import static util.Util.*;
public class Frame extends JFrame {
	private final static Color BACKGROUND_COLOR = new Color(138,196,234);
	private boolean firstElement = true;

	/**
	 * Creates a Swing window with the given title
	 *
	 * @param title The title of the window
	 */
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

	/** Resize after packing frame */
	public void pack() {
		super.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}

	/** Creates a Swing window with an empty title */
	public Frame() { this(""); }

	/**
	 * Adds a Swing GUI component to the window
	 *
	 * @param component The GUI component to be added
	 */
	public void add(JComponent component) {
		if(firstElement)
			firstElement = false;
		else {
			super.add(Box.createRigidArea(new Dimension(0,5)));
		}
		super.add(component);
	}

	/**
	 * Adds a GUI button to this Swing window
	 *
	 * @param button The button to be added
	 */
	public void add(Button button) {
		super.add(new Panel(){{add(button);}}); }
}
