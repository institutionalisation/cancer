/*
 * David Jacewicz
 * June 7, 2018
 * Ms. Krasteva
 * Panel that is always non-opaque
 */

import java.awt.*;
import javax.swing.*;
public class Panel extends JPanel {
	/** Creates a new JPanel and makes it non-opaque */
	public Panel() {
		setOpaque(false);
	}
}
