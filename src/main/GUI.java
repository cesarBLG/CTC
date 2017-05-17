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

import javax.swing.JFrame;

public class GUI
{
	private JButton botonEnviar;
	private JTextField cajaTexto;
	private JLabel etiqueta;
	private JFrame frame;
	Loader l;
	public GUI(Loader loader)
	{
		l = loader;
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Start();
	}
	void Start()
	{
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
		/*layout.setLayout(null);
		for(TrackItem t : l.items)
		{
			layout.add(t);
			int x = 0;
			int y = 0;
			int maxx = 30;
			int maxy = 50;
			if(t.SignalLinked==null) x+=20;
			t.setBounds(t.x * maxx + x, t.y * maxy + y, t.getPreferredSize().width, t.getPreferredSize().height);
		}*/
		layout.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0, 0, 0, 0);
		g.gridheight = 1;
		for(TrackItem t : l.items)
		{
			g.gridx = t.x;
			g.gridy = t.y * 2;
			layout.add(t, g);
			if(t.SignalLinked!=null)
			{
				g.insets = new Insets(5, 0, 3, 0);
				g.fill = GridBagConstraints.NONE;
				g.anchor = t.SignalLinked.Direction == Orientation.Even ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
				if(t.SignalLinked instanceof FixedSignal && (t.EvenItem == null || t.OddItem == null) ) g.anchor = t.SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
				g.gridy--;
				layout.add(t.SignalLinked, g);
				g.fill = GridBagConstraints.HORIZONTAL;
				g.insets = new Insets(0, 0, 0, 0);
				g.anchor = GridBagConstraints.NORTH;
			}
		}
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
