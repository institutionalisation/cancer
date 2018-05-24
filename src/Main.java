import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Main {
	private void run() {
		new JFrame(){final JFrame frame = this;{
			setTitle("Menu");
			setBackground(new Color(50,100,150));
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setLayout(new FlowLayout());
			add(new JLabel("<html><style>body{font-size:20px;}</style><body>Menu</body></html>"));
			for(Level level : new Level[]{new Level0(),new Level1(),new Level2()})
				add(new JButton(){{
					setText(level.getName());
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							frame.setVisible(false);
							try {
								level.run();
							} catch(Exception a) {
								a.printStackTrace(); }
							frame.setVisible(true);
						}
					});
				}});
			add(new JButton(){{
				setText("Quit");
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						frame.dispatchEvent(new WindowEvent(frame,WindowEvent.WINDOW_CLOSING));
					}
				});
			}});
			setVisible(true);
		}};
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}