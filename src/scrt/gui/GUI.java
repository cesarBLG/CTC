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
