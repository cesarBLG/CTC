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

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class TrackLayout
{
	public static boolean external = false;
	public static JFrame frame;
	public static void start() 
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
	}
}
