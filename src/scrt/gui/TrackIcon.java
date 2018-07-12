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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.packet.ACData;
import scrt.com.packet.ACID;
import scrt.com.packet.ID;
import scrt.com.packet.ID.ElementType;
import scrt.com.packet.ItineraryStablisher;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalID;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;

public class TrackIcon extends CTCIcon {
	JLabel TrackIcon = new JLabel();
	JLabel NumAxles = new JLabel();
	public JLabel Counter = null;
	TrackIcon(TrackItemID id)
	{
		this.id = id;
		comp = new JPanel();
		paint();
	}
	TrackRegister reg;
	TrackItemID id;
	TrackData data;
	SignalID sigId;
	public SignalIcon signal = null;
	ACID acid = null;
	static TrackItemID ItineraryStart = null;
	public TrackIcon(TrackRegister reg)
	{
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
					if(arg0.isAltDown())
					{
						//TrackItem t = (TrackItem) CTCItem.findId(id);
						return;
					}
					if(arg0.isControlDown())
					{
						data.BlockState = Orientation.None;
						receiver.send(data);
						return;
					}
					if(acid!=null)
					{
						ACData a = new ACData(acid);
						a.dir = Orientation.Odd;
						receiver.send(a);
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON2)
				{
					if(ItineraryStart == null) ItineraryStart = id;
					else
					{
						ItineraryStablisher r = new ItineraryStablisher(ItineraryStart, id);
						r.dir = id.x > ItineraryStart.x ? Orientation.Even : Orientation.Odd;
						receiver.send(r);
						ItineraryStart = null;
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					if(acid!=null)
					{
						ACData a = new ACData(acid);
						a.dir = Orientation.Even;
						receiver.send(a);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
	
		});
		comp.setLayout(null);
		comp.setBackground(Color.black);
		TrackIcon.setOpaque(true);
		if(reg.OddRotation==reg.EvenRotation&&reg.EvenRotation!=0)
		{
			Icon ic = new ImageIcon(getClass().getResource("/scrt/Images/Track/".concat(reg.EvenRotation == 1 ? "Left" : "Right").concat(".png")));
			TrackIcon.setIcon(ic);
			TrackIcon.setBounds(0, 0, 30, 73);
		}
		else
		{
			if(reg.OddRotation == reg.EvenRotation)
			{
				TrackIcon.setBounds(0, 35, 30, 3);
			}
			else
			{
				Icon ic = null;
				if(reg.OddRotation == 1 && reg.EvenRotation == 0)
				{
					ic = new ImageIcon(getClass().getResource("/scrt/Images/Track/UpLeft.png"));
					TrackIcon.setBounds(0, 35, 30, 38);
				}
				if(reg.OddRotation == -1 && reg.EvenRotation == 0)
				{
					ic = new ImageIcon(getClass().getResource("/scrt/Images/Track/DownLeft.png"));
					TrackIcon.setBounds(0, 0, 30, 38);
				}
				if(reg.EvenRotation == -1 && reg.OddRotation == 0)
				{
					TrackIcon.setHorizontalAlignment(JLabel.RIGHT);
					ic = new ImageIcon(getClass().getResource("/scrt/Images/Track/UpRight.png"));
					TrackIcon.setBounds(0, 35, 30, 38);
				}
				if(reg.EvenRotation == 1 && reg.OddRotation == 0)
				{
					TrackIcon.setHorizontalAlignment(JLabel.RIGHT);
					ic = new ImageIcon(getClass().getResource("/scrt/Images/Track/DownRight.png"));
					TrackIcon.setBounds(0, 0, 30, 38);
				}
				TrackIcon.setIcon(ic);
			}
		}
		comp.add(TrackIcon);
		if(reg.Name.length()>=1)
		{
			JLabel j = new JLabel(reg.Name.length()== 0 ? " " : reg.Name);
			j.setHorizontalAlignment(JLabel.CENTER);
			j.setVerticalAlignment(JLabel.TOP);
			j.setForeground(Color.yellow);
			j.setFont(new Font("Tahoma", 0, 10));
			j.setBounds(0, 38, 30, 12);
			comp.add(j);
			NumAxles.setFont(new Font("Tahoma", 0, 10));
			NumAxles.setHorizontalAlignment(JLabel.CENTER);
			NumAxles.setVerticalAlignment(JLabel.TOP);
			NumAxles.setHorizontalTextPosition(JLabel.CENTER);
			NumAxles.setBounds(0, 50, 30, 12);
			comp.add(NumAxles);
		}
		paint();
	}
	public void setSignal(SignalIcon sig)
	{
		signal = sig;
		signal.comp.setBounds(0, 10, 30, 25);
		comp.add(signal.comp, 0);
		comp.revalidate();
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
			TrackIcon.setBackground(i.Occupied != Orientation.None ? (i.Occupied == Orientation.Unknown ? Color.white : Color.red) : i.BlockState != Orientation.None ? (i.BlockState==Orientation.Unknown ? Color.darkGray : (i.shunt ? Color.blue : Color.green)) : Color.yellow);
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
		comp.repaint();
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
			if(link.type == ElementType.AC)
			{
				acid = (ACID) link;
				TrackIcon.setBorder(BorderFactory.createMatteBorder(0, acid.dir == Orientation.Odd ? 2 : 0, 0, acid.dir == Orientation.Odd ? 0 : 2, Color.black));
				{
					Counter = new JLabel("CV" + acid.Num);
					Counter.setHorizontalAlignment(acid.dir == Orientation.Even ? JLabel.RIGHT : JLabel.LEFT);
					Counter.setVerticalAlignment(JLabel.TOP);
					Counter.setForeground(Color.cyan);
					Counter.setFont(new Font("Tahoma", 0, 8));
					Counter.setBounds(0, 38, 30, 12);
					comp.add(Counter);
					comp.repaint();
					comp.revalidate();
				}
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
	public static boolean run = false;
	static int a = 0;
	public void paint()
	{
		int x = id.x;
		int y = id.y;
		if(maxx<x)
		{
			maxx = x;
			run = false;
		}
		if(maxy<y)
		{
			maxy = y;
			run = false;
		}
		if(minx>x)
		{
			minx = x;
			run = false;
		}
		if(miny>y)
		{
			miny = y;
			run = false;
		}
		if(!run) SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						if(run) return;
						synchronized(gbc)
						{
							for(int i=minx; i<=maxx; i++)
							{
								gbc.gridx = i;
								gbc.gridy = 0;
								JLabel j = new JLabel(Integer.toString(i));
								j.setForeground(Color.white);
								layout.add(j, gbc);
							}
							for(int i=miny; i<=maxy; i++)
							{
								gbc.gridx = 0;
								gbc.gridy = i;
								JLabel j = new JLabel(Integer.toString(i));
								j.setForeground(Color.white);
								layout.add(j, gbc);
							}
						}
						run = true;
					}			
				});
		synchronized(gbc)
		{
			gbc.weightx = 0;
			gbc.gridx = x;
			gbc.gridy = y;
			comp.setMinimumSize(new Dimension(30, 73));
			comp.setPreferredSize(new Dimension(30, 73));
			comp.setMaximumSize(new Dimension(30, 73));
			layout.add(comp, gbc);
			layout.revalidate();
		}
	}
}
