package scrt.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class TrackLayout
{
	public static boolean external = false;
	public static JFrame frame;
	public static void main(String[] args) 
	{
		external = true;
		frame = new JFrame("Editor");
		new TrackLayout(frame);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public TrackLayout(Container c)
	{
		JPanel layout = new JPanel();
		layout.setBackground(Color.black);
		layout.setLayout(new GridBagLayout());
		layout.setBorder(new EmptyBorder(20,20,20,20));
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0, 0, 0, 0);
		g.weightx = 3;
		g.gridheight = 1;
		CTCIcon.gbc = g;
		CTCIcon.layout = layout;
		CTCIcon.receiver = new Receiver();
		JScrollPane pane = new JScrollPane(layout);
		pane.getHorizontalScrollBar().setUnitIncrement(20);
		c.add(pane);
		for(int i=-20; i<70; i++)
		{
			g.gridx = i;
			g.gridy = 50;
			layout.add(new JLabel(" "));
		}
	}
}
