package scrt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.ctc.Signal.Aspect;
import scrt.ctc.Signal.SignalType;

public class SignalIcon extends CTCIcon {
	SignalData sig;
	SignalID id;
	SignalRegister reg;
	JPopupMenu popup;
	JMenuItem close = new JMenuItem("Abrir se�al");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo autom�tico");
	JPanel sigIcon = new JPanel();
	JLabel pie = new JLabel();
	JLabel mastil = new JLabel();
	JLabel foco = new JLabel();
	JLabel sucesion = new JLabel();
	JLabel name;
	ImageIcon mastil1;
	ImageIcon mastil2;
	public SignalIcon(SignalRegister s)
	{
		reg = s;
		id = (SignalID) reg.id;
		sig = new SignalData(id);
		comp = new JPanel();
		comp.setLayout(new BorderLayout());
		comp.setOpaque(false);
		name = new JLabel(reg.Name);
		name.setHorizontalAlignment(id.Direction == Orientation.Even ? JLabel.LEFT : JLabel.RIGHT);
		name.setFont(new Font("Tahoma", 0, 10));
		name.setForeground(Color.WHITE);
		if(!reg.EoT) comp.add(name, BorderLayout.CENTER);
		sigIcon.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		sigIcon.setLayout(new FlowLayout(id.Direction == Orientation.Even ^ reg.EoT ? FlowLayout.LEADING : FlowLayout.TRAILING, 0, 2));
		sigIcon.setOpaque(false);
		comp.add(sigIcon, BorderLayout.SOUTH);
		foco.setOpaque(true);
		mastil.setOpaque(true);
		pie.setOpaque(true);
		if(reg.Fixed)
		{
			pie.setOpaque(false);
			sigIcon.add(pie);
		}
		else if(id.Class == SignalType.Exit_Indicator)
		{
			pie.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/Pie.png")));
			foco.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.white));
			if(id.Direction == Orientation.Even)
			{
				sigIcon.add(pie);
				sigIcon.add(foco);
			}
			else
			{
				sigIcon.add(foco);
				pie.setIcon(IconDatabase.getRotated(pie.getIcon()));
				sigIcon.add(pie);
			}
		}
		else
		{
			pie.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/Pie.png")));
			mastil1 = new ImageIcon(getClass().getResource("/scrt/Images/Signals/Mastil.png"));
			mastil2 = new ImageIcon(getClass().getResource("/scrt/Images/Signals/Recuadro.png"));
			foco.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Signals/Foco.png")));
			sucesion.setHorizontalAlignment(id.Direction == Orientation.Even ? JLabel.LEFT : JLabel.RIGHT);
			sucesion.setHorizontalTextPosition(id.Direction == Orientation.Even ? JLabel.RIGHT : JLabel.LEFT);
			sucesion.setFont(new Font("Tahoma", 0, 9));
			sucesion.setForeground(Color.GREEN);
			sucesion.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, Color.black));
			//comp.add(sucesion, BorderLayout.NORTH);
			if(id.Direction == Orientation.Even)
			{
				sigIcon.add(pie);
				sigIcon.add(mastil);
				sigIcon.add(foco);
			}
			else
			{
				sigIcon.add(foco);
				sigIcon.add(mastil);
				pie.setIcon(IconDatabase.getRotated(pie.getIcon()));
				sigIcon.add(pie);
			}
			popup = new JPopupMenu();
			popup.add(reg.Name);
			popup.addSeparator();
			close.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent arg0) {
							sig.UserRequest = !sig.ClearRequest;
							reader.send(sig);
						}
				
					});
			override.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent arg0) {
							if(sig.OverrideRequest && sig.ClearRequest)
							{
								sig.UserRequest = false;
								sig.OverrideRequest = false;
								reader.send(sig);
							}
							else
							{
								sig.OverrideRequest = true;
								sig.UserRequest = true;
								reader.send(sig);
							}
						}
				
					});
			auto.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent arg0) {
							sig.Automatic = !sig.Automatic;
							reader.send(sig);
						}
				
					});
			JMenuItem config = new JMenuItem("Configuraci�n");
			config.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
				}
		
			});
			popup.add(close);
			if(id.Class != SignalType.Block && id.Class != SignalType.Advanced)
			{
				if(id.Class == SignalType.Entry || id.Class == SignalType.Exit) popup.add(override);
				popup.add(auto);
				//popup.add(config);
			}
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
						        popup.show(sigIcon, arg0.getX(), arg0.getY());
							}
							if(arg0.getButton()==MouseEvent.BUTTON1)
							{
								sig.UserRequest = true;
								reader.send(sig);
							}
						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
							// TODO Auto-generated method stub
							if(arg0.isPopupTrigger())
							{
						        popup.show(sigIcon, arg0.getX(), arg0.getY());
							}
						}
				
					});
		}
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof SignalData)
		{			
			SignalData s = (SignalData)p;
			if(!s.id.equals(id)) return;
			sig = s;
			update();
		}
	}
	@Override
	public SignalID getID(){return id;}
	Timer t;
	boolean flop;
	@Override
	public void update()
	{
		if(t == null)
		{
			t = new Timer(500, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if(flop) foco.setOpaque(true);
					else if(sig.SignalAspect == Aspect.Precaucion || sig.SignalAspect == Aspect.Apagado || (id.Class == SignalType.Exit_Indicator && sig.SignalAspect == Aspect.Parada)) foco.setOpaque(false);
					foco.repaint();
					flop = !flop;
				}
			});
			t.setInitialDelay(500);
			t.setRepeats(true);
			t.start();
		}
		if(id.Class == SignalType.Exit_Indicator)
		{
			if(sig.SignalAspect == Aspect.Apagado) foco.setOpaque(false);
			else 
			{
				foco.setOpaque(true);
				foco.setIcon(IconDatabase.getAspect(new IconDatabase.AspectDiscriminator(sig.SignalAspect, id.Direction, false, true)));
			}
		}
		else if(reg.Fixed)
		{
			pie.setIcon(IconDatabase.getAspect(new IconDatabase.AspectDiscriminator(sig.SignalAspect, id.Direction, true, false)));
		}
		else
		{
			
			if(sig.Automatic)
			{
				sucesion.setText("S");
				name.setForeground(Color.green);
			}
			else
			{
				sucesion.setText("");
				name.setForeground(Color.white);
			}
			if(sig.SignalAspect == Aspect.Rebase || sig.SignalAspect == Aspect.Parada || sig.SignalAspect == Aspect.Preanuncio) mastil.setIcon(mastil2);
			else mastil.setIcon(mastil1);
			switch(sig.SignalAspect)
			{
				case Parada:
					pie.setBackground(Color.red);
					mastil.setBackground(Color.red);
					foco.setBackground(Color.red);
					break;
				case Rebase:
					pie.setBackground(Color.red);
					mastil.setBackground(Color.white);
					foco.setBackground(Color.red);
					break;
				case Precaucion:
				case Anuncio_parada:
				case Preanuncio:
					pie.setBackground(Color.yellow);
					mastil.setBackground(Color.yellow);
					foco.setBackground(Color.yellow);
					break;
				case Anuncio_precaucion:
					pie.setBackground(Color.yellow);
					mastil.setBackground(Color.yellow);
					foco.setBackground(Color.green);
					break;
				case Via_libre:
					pie.setBackground(Color.green);
					mastil.setBackground(Color.green);
					foco.setBackground(Color.green);
					break;
				default:
					break;
			}
			close.setText(sig.ClearRequest ? "Cerrar se�al" : "Abrir se�al");
			override.setText(sig.OverrideRequest && sig.ClearRequest ? "Desactivar rebase" : "Rebase autorizado");
			auto.setText(!sig.Automatic ? "Modo autom�tico" : "Modo manual");
		}
	}
}
