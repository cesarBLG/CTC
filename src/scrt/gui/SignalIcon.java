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
import scrt.com.packet.ID;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.ctc.CTCItem;
import scrt.ctc.Signal.Aspect;
import scrt.ctc.Signal.ExitIndicator;
import scrt.ctc.Signal.FixedSignal;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;
import scrt.ctc.Signal.SignalType;

public class SignalIcon extends CTCIcon {
	SignalData sig;
	SignalID id;
	SignalRegister reg;
	JPopupMenu popup;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	public SignalIcon(SignalRegister s)
	{
		comp = new JLabel();
		reg = s;
		id = (SignalID) reg.id;
		sig = new SignalData(id);
		if(reg.Fixed || id.Class == SignalType.Exit_Indicator)
		{
			comp.setForeground(Color.WHITE);
			((JLabel)comp).setHorizontalTextPosition(JLabel.CENTER);
			((JLabel)comp).setVerticalTextPosition(JLabel.TOP);
			((JLabel)comp).setText(id.Name);
			((JLabel)comp).setFont(new Font("Tahoma", 0, 10));
		}
		else
		{
			popup = new JPopupMenu();
			close.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							sig.UserRequest = !sig.ClearRequest;
							CTCItem.PacketManager.handlePacket(sig);
						}
				
					});
			override.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							if(sig.OverrideRequest)
							{
								sig.UserRequest = false;
								sig.OverrideRequest = false;
								CTCItem.PacketManager.handlePacket(sig);
							}
							else
							{
								sig.OverrideRequest = true;
								sig.UserRequest = true;
								CTCItem.PacketManager.handlePacket(sig);
							}
						}
				
					});
			auto.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0) {
							sig.Automatic = !sig.Automatic;
							CTCItem.PacketManager.handlePacket(sig);
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
			comp.addMouseListener(new MouseListener()
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
						        popup.show(comp, arg0.getX(), arg0.getY());
							}
							if(arg0.getButton()==MouseEvent.BUTTON1)
							{
								sig.UserRequest = true;
								CTCItem.PacketManager.handlePacket(sig);
							}
						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
							// TODO Auto-generated method stub
							if(arg0.isPopupTrigger())
							{
						        popup.show(comp, arg0.getX(), arg0.getY());
							}
						}
				
					});
			comp.setForeground(Color.WHITE);
			((JLabel)comp).setVerticalAlignment(JLabel.BOTTOM);
			((JLabel)comp).setHorizontalAlignment(id.Direction == Orientation.Odd ? JLabel.RIGHT : JLabel.LEFT);
			((JLabel)comp).setHorizontalTextPosition(JLabel.CENTER);
			((JLabel)comp).setVerticalTextPosition(JLabel.TOP);
			((JLabel)comp).setText(id.Name);
			comp.setFont(new Font("Tahoma", 0, 10));
		}
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof SignalData)
		{
			sig = (SignalData)p;
			update();
		}
	}
	@Override
	public ID getId(){return id;}
	@Override
	public void update()
	{
		if(id.Class == SignalType.Exit_Indicator)
		{
			((JLabel)comp).setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/IS/".concat(sig.SignalAspect.name()).concat("_".concat(id.Direction.name().concat(".png"))))));
		}
		else if(reg.Fixed)
		{
			((JLabel)comp).setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/Fixed/".concat(sig.SignalAspect.name()).concat("_".concat(id.Direction.name().concat(".png"))))));
		}
		else
		{
			((JLabel)comp).setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/".concat((sig.SignalAspect==Aspect.Parada&&sig.Automatic ? "Automatic" : sig.SignalAspect.name()).concat("_".concat(id.Direction.name().concat(".png")))))));
			close.setText(sig.ClearRequest ? "Cerrar señal" : "Abrir señal");
			override.setText(sig.OverrideRequest ? "Desactivar rebase" : "Rebase autorizado");
			auto.setText(!sig.Automatic ? "Modo automático" : "Modo manual");
		}
	}
}
