import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Main {
	private void run() {
		new JFrame(){final JFrame frame = this;{
			setTitle("Menu");
			setBackground(new Color(50,100,150));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new FlowLayout());
			add(new JLabel("<html><style>body{font-size:20px;}</style><body>Menu</body></html>"));
			for(Level level : new Level[]{new Level0(),new Level1(),new Level2()})
				add(new JButton(){{
					setText(level.getName());
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							frame.setVisible(false);
							level.run();
							frame.setVisible(true);
						}
					});
				}});
			setVisible(true);
		}};
	}
	public static void main(String[] args) throws Exception { new Main().run(); }
}