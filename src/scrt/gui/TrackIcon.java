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
import scrt.ctc.AxleCounter;
import scrt.ctc.CTCItem;
import scrt.ctc.Itinerary;
import scrt.ctc.TrackItem;
import scrt.ctc.packet.ACData;
import scrt.ctc.packet.Packet;
import scrt.ctc.packet.SignalData;
import scrt.ctc.Signal.EoT;
import scrt.ctc.Signal.ExitIndicator;
import scrt.ctc.Signal.FixedSignal;
import scrt.ctc.Signal.SignalType;
import scrt.event.SRCTEvent;

public class TrackIcon extends CTCIcon {
	TrackItem item;
	JLabel TrackIcon = new JLabel();
	JLabel NumAxles = new JLabel();
	TrackIcon()
	{
		comp = new JPanel();
	}
	static TrackItem ItineraryStart = null;
	public TrackIcon(TrackItem item) 
	{
		comp = new JPanel();
		this.item = item;
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
					if(item.CounterLinked!=null)
					{
						ACData a = (ACData) item.CounterLinked.getPacket();
						a.dir = Orientation.Odd;
						CTCItem.PacketManager.handlePacket(a);
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON2)
				{
					if(ItineraryStart == null) ItineraryStart = item;
					else
					{
						Itinerary.set(ItineraryStart, item, item.x > ItineraryStart.x ? Orientation.Even : Orientation.Odd, false);
						ItineraryStart = null;
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					if(item.CounterLinked!=null)
					{
						ACData a = (ACData) item.CounterLinked.getPacket();
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
		if(item.OddRotation==item.EvenRotation&&item.EvenRotation==-1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/Right.png")));
			TrackIcon.setMinimumSize(new Dimension(30, 73));
			TrackIcon.setPreferredSize(new Dimension(30, 73));
			TrackIcon.setMaximumSize(new Dimension(30, 73));
		}
		else if(item.OddRotation==item.EvenRotation&&item.EvenRotation==1)
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
		if(item.Name.length()>=1)
		{
			JLabel j = new JLabel(item.Name.length()== 0 ? " " : item.Name);
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
		if(item.OddRotation!=item.EvenRotation||item.OddRotation==0)
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
			if(item.Name.length()>=1) g.gridheight = 2;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			((Container)comp).add(jp,g);
		}
	}
	public void setSignal()
	{
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		if(item.SignalLinked!=null)
		{
			g.insets = new Insets(5, 0, 3, 0);
			g.anchor = item.SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHEAST : GridBagConstraints.SOUTHWEST;
			if(item.SignalLinked instanceof EoT && (item.EvenItem == null || item.OddItem == null) ) g.anchor = item.SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
			for(CTCIcon i : CTCIcon.items)
			{
				if(i.getPacket().equals(item.SignalLinked.getPacket()))
				{
					((Container)comp).add(i.comp, g);
				}
			}
		}
	}
	Timer timer = new Timer(350, new ActionListener()
			{
				boolean t = true;
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					if(!item.Acknowledged)
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
		TrackItem i = item;
		if(i.Acknowledged)
		{
			timer.stop();
			TrackIcon.setBackground(i.Occupied != Orientation.None ? (i.Occupied == Orientation.Unknown ? Color.white : Color.red) : i.BlockState != Orientation.None ? Color.green : Color.yellow);
		}
		else
		{
			timer.setInitialDelay(0);
			timer.setRepeats(true);
			timer.start();
		}
		if(i.Name.length()>=1)
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
	public Packet getPacket()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void load(Packet p)
	{
		// TODO Auto-generated method stub
		
	}
}
