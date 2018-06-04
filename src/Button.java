import java.awt.*;
import javax.swing.*;
public class Button extends JButton {
	public Button(String title) {
		super(title);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	public Button() {
		super("");
	}
}