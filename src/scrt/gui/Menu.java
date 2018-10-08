/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scrt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import scrt.ctc.Loader;
import scrt.event.ListEvent;
import scrt.event.SCRTListener;
import scrt.event.SRCTEvent;
import scrt.regulation.Regulation;

public class Menu extends JMenuBar {
	public Menu()
	{
		super();
		JMenu Monitor = new JMenu("Estado");
		add(Monitor);
		JMenuItem Traffic = new JMenuItem("Gestor de tráfico...");
		Monitor.add(Traffic);
		Traffic.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		Traffic.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						Regulation.g = new TrafficGraph(Regulation.p);
						Regulation.g.updateData(Regulation.services);
						Regulation.services.addListener(new SCRTListener()
								{
									@Override
									public void actionPerformed(SRCTEvent e)
									{
										if(e instanceof ListEvent) Regulation.g.updateData(Regulation.services);
									}
									@Override
									public void muteEvents(boolean mute){}
								});
					}
					
				});
		JMenuItem Stations = new JMenuItem("Estaciones...");
		Monitor.add(Stations);
		Stations.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		Stations.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new StationWindow();
					}
					
				});
		JMenuItem Trains = new JMenuItem("Horarios...");
		Monitor.add(Trains);
		Trains.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		Trains.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new TimetableWindow();
					}
				});
		JMenuItem Routes = new JMenuItem("Itinerarios...");
		Monitor.add(Routes);
		Routes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		Routes.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JDialog d = new JDialog();
						d.add(new JList(Loader.itineraries.toArray()));
						d.pack();
						d.setVisible(true);
					}
				});
		JMenuItem GRP = new JMenuItem("Gestión de rutas...");
		Monitor.add(GRP);
		GRP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		GRP.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new FAIWindow(null);
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
