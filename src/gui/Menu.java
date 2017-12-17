package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Menu extends JMenuBar {
	public Menu()
	{
		super();
		JMenu Monitor = new JMenu("Estado");
		add(Monitor);
		JMenuItem Stations = new JMenuItem("Estaciones...");
		Monitor.add(Stations);
		Stations.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		Stations.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new StationManager();
					}
					
				});
		JMenuItem Trains = new JMenuItem("Trenes...");
		Monitor.add(Trains);
		Trains.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		Trains.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new TrainManager();
					}
				});
		JMenu Help = new JMenu("Ayuda");
		add(Help);
		JMenuItem About = new JMenuItem("Acerca de...");
		About.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Control de tráfico centralizado\nCésar Benito Lamata", "Acerca de", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		Help.add(About);
	}
}
