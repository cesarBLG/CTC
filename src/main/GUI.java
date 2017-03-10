package main;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.Signal.Aspect;

import javax.swing.JFrame;

public class GUI
{
	private JButton botonEnviar;
	private JTextField cajaTexto;
	private JLabel etiqueta;
	private JFrame frame;
	Loader l;
	public GUI()
	{
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Start();
	}
	void Prepare()
	{
		JLabel Waiting = new JLabel("Esperando al puerto serie...");
		frame.add(Waiting);
		frame.pack();
		frame.setVisible(true);
		while(!Serial.Connected)
		{
			Serial.begin(9600);
		}
		frame.remove(Waiting);
		Start();
	}
	void Start()
	{
		l = new Loader();
		setTrackLayout();
		setItineraryFrame();
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	void setTrackLayout()
	{
		JPanel layout = new JPanel();
		layout.setBackground(Color.black);
		layout.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0, 2, 0, 2);
		for(TrackItem t : l.items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				g.gridheight = 3;
				g.anchor = (j.Direction==Orientation.Even && j.Class == Position.Right) || (j.Direction==Orientation.Odd && j.Class == Position.Left) ? GridBagConstraints.NORTH : GridBagConstraints.SOUTH;
			}
			else
			{
				g.gridheight = 1;
				g.anchor = GridBagConstraints.NORTH;
			}
			g.gridx = t.x;
			g.gridy = t.y * 2 + 1;
			layout.add(t, g);
			if(t.SignalLinked!=null)
			{
				g.insets = new Insets(0, 2, 3, 2);
				g.fill = GridBagConstraints.NONE;
				g.anchor = t.SignalLinked.Direction == Orientation.Even ? GridBagConstraints.WEST : GridBagConstraints.EAST;
				g.gridy--;
				layout.add(t.SignalLinked, g);
				g.fill = GridBagConstraints.HORIZONTAL;
				g.insets = new Insets(0, 2, 0, 2);
				g.anchor = GridBagConstraints.NORTH;
			}
		}
		/*g.anchor = GridBagConstraints.CENTER;
		g.gridx = 2;
		g.gridy = 6;
		g.insets = new Insets(15, 2, 0, 2);
		JLabel l = new JLabel("Arboleda");
		l.setForeground(Color.yellow);
		layout.add(l, g);
		g.gridx = 9;
		g.gridy = 4;
		l = new JLabel("C. Madera");
		l.setForeground(Color.yellow);
		layout.add(l, g);*/
		frame.add(layout);
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
					public void actionPerformed(ActionEvent e)
					{
						onSendCommand();
					}
				});
		frame.add(itinerary, BorderLayout.SOUTH);
	}
	public void onSendCommand()
	{
		String s = cajaTexto.getText();
		cajaTexto.setText("");
		CommandParser.Parse(s, l);
	}
}
