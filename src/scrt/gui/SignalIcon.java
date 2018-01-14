package scrt.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import scrt.Orientation;
import scrt.ctc.Aspect;
import scrt.ctc.ExitIndicator;
import scrt.ctc.FixedSignal;
import scrt.ctc.MainSignal;
import scrt.ctc.Signal;

public class SignalIcon extends JLabel implements CTCIcon {
	Signal signal;
	JPopupMenu popup;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	public SignalIcon(Signal s)
	{
		signal = s;
		if((s instanceof ExitIndicator)||s instanceof FixedSignal)
		{
			setForeground(Color.WHITE);
			setHorizontalTextPosition(CENTER);
			setVerticalTextPosition(TOP);
			setText(signal.Name);
			setFont(new Font("Tahoma", 0, 10));
		}
		else if(s instanceof MainSignal)
		{
			popup = new JPopupMenu();
			close.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							((MainSignal) signal).UserRequest(!signal.ClearRequest);
						}
				
					});
			override.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							if(signal.OverrideRequest)
							{
								signal.OverrideRequest = false;
								signal.ClearRequest = false;
								signal.Close();
							}
							else
							{
								signal.OverrideRequest = false;
								signal.ClearRequest = true;
								signal.Clear();
							}
						}
				
					});
			auto.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							((MainSignal) signal).setAutomatic(!signal.Automatic);
						}
				
					});
			JMenuItem config = new JMenuItem("Configuración");
			config.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0) {
					String s = JOptionPane.showInputDialog(SignalIcon.this, "Puerto de arduino");
				}
		
			});
			popup.add(close);
			popup.add(override);
			popup.add(auto);
			popup.add(config);
			addMouseListener(new MouseListener()
					{

						@Override
						public void mouseClicked(MouseEvent arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void mouseEntered(MouseEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void mouseExited(MouseEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							// TODO Auto-generated method stub
							if(arg0.isPopupTrigger())
							{
						        popup.show(SignalIcon.this, arg0.getX(), arg0.getY());
							}
							if(arg0.getButton()==MouseEvent.BUTTON1)
							{
								((MainSignal) signal).UserRequest(true);
							}
						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
							// TODO Auto-generated method stub
							if(arg0.isPopupTrigger())
							{
						        popup.show(SignalIcon.this, arg0.getX(), arg0.getY());
							}
						}
				
					});
			setForeground(Color.WHITE);
			setVerticalAlignment(BOTTOM);
			setHorizontalAlignment(signal.Direction == Orientation.Odd ? RIGHT : LEFT);
			setHorizontalTextPosition(CENTER);
			setVerticalTextPosition(TOP);
			setText(signal.Name);
			setFont(new Font("Tahoma", 0, 10));
		}
	}
	@Override
	public void update()
	{
		if(signal instanceof ExitIndicator)
		{
			setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/IS/".concat(signal.SignalAspect.name()).concat("_".concat(signal.Direction.name().concat(".png"))))));
		}
		else if(signal instanceof FixedSignal)
		{
			setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/Fixed/".concat(signal.SignalAspect.name()).concat("_".concat(signal.Direction.name().concat(".png"))))));
		}
		else if(signal instanceof MainSignal)
		{
			setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/".concat((signal.SignalAspect==Aspect.Parada&&signal.Automatic ? "Automatic" : signal.SignalAspect.name()).concat("_".concat(signal.Direction.name().concat(".png")))))));
			close.setText(signal.Cleared ? "Cerrar señal" : "Abrir señal");
			override.setText(signal.Override ? "Desactivar rebase" : "Rebase autorizado");
			auto.setText(!signal.Automatic ? "Modo automático" : "Modo manual");
		}
	}
	
}
