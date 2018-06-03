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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import scrt.ctc.CommandParser;

public class GUI
{
	private JButton botonEnviar;
	private JTextField cajaTexto;
	private JLabel etiqueta;
	private JFrame frame;
	public GUI()
	{
		//while(!JOptionPane.showInputDialog(null, "Introduzca contraseña").equals("1234"));
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setTitle("Control de Tráfico Centralizado");
		Start();
	}
	void Start()
	{
		frame.setJMenuBar(new Menu());
		new TrackLayout(frame);
		setItineraryFrame();
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	void setItineraryFrame()
	{
		JPanel itinerary = new JPanel();
		itinerary.setLayout(new FlowLayout());
		botonEnviar = new JButton("Enviar");
		cajaTexto = new JTextField(12);
		etiqueta= new JLabel("Código");
		etiqueta.setHorizontalAlignment(4);
		itinerary.add(etiqueta);
		itinerary.add(cajaTexto);
		itinerary.add(botonEnviar);
		cajaTexto.addKeyListener(new KeyListener()
				{
					@Override
					public void keyPressed(KeyEvent k)
					{
						if(k.getKeyCode()==KeyEvent.VK_ENTER) botonEnviar.doClick();
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
		botonEnviar.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						onSendCommand();
					}
				});
		frame.add(itinerary, BorderLayout.SOUTH);
	}
	void onSendCommand()
	{
		String s = cajaTexto.getText();
		cajaTexto.setText("");
		CommandParser.Parse(s);
	}
}
