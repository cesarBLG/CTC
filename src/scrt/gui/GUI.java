package scrt.gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import scrt.Main;
import scrt.Orientation;
import scrt.ctc.CommandParser;
import scrt.ctc.Loader;
import scrt.ctc.TrackItem;
import scrt.ctc.Signal.FixedSignal;

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
		l = Main.l;
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setTitle("Control de Tráfico Centralizado");
		Start();
	}
	void Start()
	{
		frame.setJMenuBar(new Menu(l));
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
		layout.setBorder(new EmptyBorder(20,20,20,20));
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0, 0, 0, 0);
		g.gridheight = 1;
		for(TrackItem t : l.items)
		{
			g.gridx = t.x + 40;
			g.gridy = t.y * 2;
			layout.add(t.icon.comp, g);
		}
		JScrollPane pane = new JScrollPane(layout);
		pane.getHorizontalScrollBar().setUnitIncrement(20);
		frame.add(pane);
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
