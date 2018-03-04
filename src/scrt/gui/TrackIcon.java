package scrt.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.packet.ACData;
import scrt.com.packet.ACID;
import scrt.com.packet.ID;
import scrt.com.packet.ItineraryRegister;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.com.packet.ElementType;
import scrt.com.packet.SignalData;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.ctc.AxleCounter;
import scrt.ctc.CTCItem;
import scrt.ctc.Itinerary;
import scrt.ctc.TrackItem;
import scrt.ctc.Signal.EoT;
import scrt.ctc.Signal.ExitIndicator;
import scrt.ctc.Signal.FixedSignal;
import scrt.ctc.Signal.SignalType;
import scrt.event.SRCTEvent;

public class TrackIcon extends CTCIcon {
	JLabel TrackIcon = new JLabel();
	JLabel NumAxles = new JLabel();
	TrackIcon()
	{
		comp = new JPanel();
	}
	TrackRegister reg;
	TrackItemID id;
	TrackData data;
	SignalIcon signal;
	ACID acid = null;
	static TrackItemID ItineraryStart = null;
	public TrackIcon(TrackRegister reg)
	{
		CTCIcon.items.add(this);
		this.reg = reg;
		id = (TrackItemID) reg.id;
		comp = new JPanel();
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
				if(arg0.getButton()==MouseEvent.BUTTON1)
				{
					if(acid!=null)
					{
						ACData a = new ACData(acid);
						a.dir = Orientation.Odd;
						CTCItem.PacketManager.handlePacket(a);
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON2)
				{
					if(ItineraryStart == null) ItineraryStart = id;
					else
					{
						ItineraryRegister r = new ItineraryRegister(ItineraryStart, id);
						r.dir = id.x > ItineraryStart.x ? Orientation.Even : Orientation.Odd;
						Itinerary.handlePacket(r);
						ItineraryStart = null;
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					if(acid!=null)
					{
						ACData a = new ACData(acid);
						a.dir = Orientation.Even;
						CTCItem.PacketManager.handlePacket(a);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
	
		});
		((Container) comp).setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0,0,0,0);
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		comp.setBackground(Color.black);
		TrackIcon.setOpaque(true);
		if(reg.OddRotation==reg.EvenRotation&&reg.EvenRotation==-1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/Right.png")));
			TrackIcon.setMinimumSize(new Dimension(30, 73));
			TrackIcon.setPreferredSize(new Dimension(30, 73));
			TrackIcon.setMaximumSize(new Dimension(30, 73));
		}
		else if(reg.OddRotation==reg.EvenRotation&&reg.EvenRotation==1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/Left.png")));
			TrackIcon.setMinimumSize(new Dimension(41, 36));
			TrackIcon.setPreferredSize(new Dimension(41, 36));
			TrackIcon.setMaximumSize(new Dimension(41, 36));
		}
		else
		{
			TrackIcon.setMinimumSize(new Dimension(30, 3));
			TrackIcon.setPreferredSize(new Dimension(30, 3));
			TrackIcon.setMaximumSize(new Dimension(30, 3));
		}
		g.gridy++;
		((Container)comp).add(TrackIcon, g);
		if(reg.Name.length()>=1)
		{
			JLabel j = new JLabel(reg.Name.length()== 0 ? " " : reg.Name);
			j.setHorizontalAlignment(JLabel.CENTER);
			j.setVerticalAlignment(JLabel.TOP);
			j.setForeground(Color.yellow);
			j.setFont(new Font("Tahoma", 0, 10));
			g.gridy++;
			((Container)comp).add(j, g);
			g.gridy++;
			NumAxles.setFont(new Font("Tahoma", 0, 10));
			NumAxles.setHorizontalAlignment(JLabel.CENTER);
			NumAxles.setVerticalAlignment(JLabel.TOP);
			NumAxles.setHorizontalTextPosition(JLabel.CENTER);
			((Container)comp).add(NumAxles, g);
		}
		if(reg.OddRotation!=reg.EvenRotation||reg.OddRotation==0)
		{
			JPanel jp = new JPanel();
			g.insets = new Insets(0,0,0,0);
			g.gridx++;
			g.gridy = 0;
			g.fill = GridBagConstraints.BOTH;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			((Container)comp).add(jp,g);
			jp = new JPanel();
			g.gridy = 2;
			if(reg.Name.length()>=1) g.gridheight = 2;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			((Container)comp).add(jp,g);
		}
	}
	public void setSignal(SignalIcon sig)
	{
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		g.insets = new Insets(5, 0, 3, 0);
		g.anchor = sig.id.Direction == Orientation.Odd ? GridBagConstraints.SOUTHEAST : GridBagConstraints.SOUTHWEST;
		if(sig.reg.EoT) g.anchor = sig.id.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
		((Container)comp).add(sig.comp, g);
	}
	Timer timer = new Timer(350, new ActionListener()
			{
				boolean t = true;
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					if(!data.Acknowledged)
					{
						if(t) TrackIcon.setBackground(Color.red);
						else TrackIcon.setBackground(Color.yellow);
						t = !t;
					}
				}
			});
	@Override
	public void update()
	{
		TrackData i = data;
		if(i.Acknowledged)
		{
			timer.stop();
			TrackIcon.setBackground(i.Occupied != Orientation.None ? (i.Occupied == Orientation.Unknown ? Color.white : Color.red) : i.BlockState != Orientation.None ? (i.BlockState==Orientation.Unknown ? Color.darkGray : Color.green) : Color.yellow);
		}
		else
		{
			timer.setInitialDelay(0);
			timer.setRepeats(true);
			timer.start();
		}
		if(reg.Name.length()>=1)
		{
			String n = i.Occupied.name();
			if(i.Occupied == Orientation.None && (i.BlockState==Orientation.Odd || i.BlockState==Orientation.Even))
			{
				n = "Block".concat(i.BlockState.name());
			}
			NumAxles.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/".concat(n).concat(".png"))));
			NumAxles.setForeground(i.OddAxles + i.EvenAxles == 0 ? Color.YELLOW : Color.red);
			NumAxles.setText(Integer.toString(i.EvenAxles + i.OddAxles));
		}
	}
	@Override
	public ID getID()
	{
		return id;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof LinkPacket)
		{
			LinkPacket l = (LinkPacket)p;
			ID link = null;
			if(l.id1.equals(id)) link = l.id2;
			else if(l.id2.equals(id)) link = l.id1;
			else return;
			if(link.type == ElementType.AxleCounter)
			{
				acid = (ACID) link;
				return;
			}
			CTCIcon icon = findID(link);
			if(icon instanceof SignalIcon) setSignal((SignalIcon) icon);
		}
		if(p instanceof TrackData)
		{
			TrackData d = (TrackData) p;
			if(!d.id.equals(id)) return;
			data = d;
			update();
		}
	}
}
