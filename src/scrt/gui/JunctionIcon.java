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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.packet.ID;
import scrt.com.packet.JunctionData;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.JunctionSwitch;
import scrt.com.packet.Packet;
import scrt.ctc.Junction.Position;

public class JunctionIcon extends TrackIcon {
	
	public JLabel Locking = new JLabel();
	JLabel Direct = new JLabel();
	JLabel Desv = new JLabel();
	JunctionData data;
	JunctionID junctionID;
	JunctionRegister reg;
	public JunctionIcon(JunctionRegister reg) {
		super(reg.TrackId);
		this.reg = reg;
		junctionID = (JunctionID) reg.id;
		JPopupMenu popup = new JPopupMenu();
		popup.add(junctionID.Name);
		popup.addSeparator();
		JMenuItem muelle = new JMenuItem("Muelle");
		muelle.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						JunctionSwitch j = new JunctionSwitch(junctionID);
						j.muelle = true;
						receiver.send(j);
					}
				});
		popup.add(muelle);
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
					JunctionSwitch j = new JunctionSwitch(junctionID);
					if(arg0.isAltDown()) j.force = true;
					receiver.send(j);
				}
				if(arg0.getButton()==MouseEvent.BUTTON1 && arg0.isControlDown())
				{
					data.BlockState = Orientation.None;
					receiver.send(data);
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
		((Container)comp).setLayout(new GridBagLayout());
		comp.setBackground(Color.black);
		setIcon();
	}
	void setIcon()
	{
		GridBagConstraints g = new GridBagConstraints();
		boolean Upwise = (reg.Direction==Orientation.Even && reg.Class == Position.Right) || (reg.Direction==Orientation.Odd && reg.Class == Position.Left); 
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = new Insets(0, 0, 0, 0);
		g.anchor = GridBagConstraints.CENTER;
		g.gridx = reg.Direction == Orientation.Even ? 0 : 2;
		g.gridy = 1;
		TrackIcon.setOpaque(true);
		TrackIcon.setMinimumSize(new Dimension(20, 3));
		TrackIcon.setPreferredSize(new Dimension(20, 3));
		TrackIcon.setMaximumSize(new Dimension(20, 3));
		((Container)comp).add(TrackIcon, g);
		g.gridy++;
		g.insets = new Insets(0,0,0,0);
		JLabel j = new JLabel();
		j.setVerticalAlignment(JLabel.TOP);
		j.setForeground(Color.yellow);
		j.setText("A".concat(Integer.toString(junctionID.Number)));
		j.setFont(new Font("Tahoma", 0, 10));
		((Container)comp).add(j, g);
		g.gridy--;
		if(reg.Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(1, 1, 1, 1);
		Locking.setOpaque(true);
		Locking.setMinimumSize(new Dimension(4, 3));
		Locking.setPreferredSize(new Dimension(4, 3));
		Locking.setMaximumSize(new Dimension(4, 3));
		((Container)comp).add(Locking, g);
		if(reg.Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(0, 0, 0, 0);
		Direct.setOpaque(true);
		Direct.setMinimumSize(new Dimension(4, 3));
		Direct.setPreferredSize(new Dimension(4, 3));
		Direct.setMaximumSize(new Dimension(4, 3));
		Direct.setBackground(Color.yellow);
		((Container)comp).add(Direct, g);
		g.insets = new Insets(0, 0, 0, 0);
		g.ipady = 2;
		if(Upwise) g.gridy++;
		else g.gridy--;
		if(reg.Direction == Orientation.Even) g.gridx--;
		g.gridwidth = 2;
		g.fill = GridBagConstraints.NONE;
		g.anchor = reg.Direction == Orientation.Even ? GridBagConstraints.EAST : GridBagConstraints.WEST;
		Desv.setBackground(Color.yellow);
		Desv.setOpaque(true);
		Desv.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Junction/".concat(reg.Class.name()).concat(".png"))));
		Desv.setPreferredSize(new Dimension(9, 33));
		Desv.setMaximumSize(new Dimension(9, 33));
		Desv.setMinimumSize(new Dimension(9, 33));
		((Container)comp).add(Desv, g);
		g.anchor = GridBagConstraints.CENTER;
		g.ipady = 0;
		JPanel jp = new JPanel();
		g.gridx = 4;
		g.gridy = 2;
		g.fill = GridBagConstraints.BOTH;
		jp.setMinimumSize(new Dimension(0,35));
		jp.setPreferredSize(new Dimension(0,35));
		jp.setMaximumSize(new Dimension(0,35));
		((Container)comp).add(jp,g);
		jp = new JPanel();
		g.gridy = 0;
		jp.setMinimumSize(new Dimension(0,35));
		jp.setPreferredSize(new Dimension(0,35));
		jp.setMaximumSize(new Dimension(0,35));
		((Container)comp).add(jp,g);
		comp.validate();
	}
	Timer FlashingTimer = new Timer(500, new ActionListener()
	{
		boolean a = false;
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Locking.setBackground(a ? Color.blue : Color.white);
			a = !a;
		}
	});
	@Override
	public void update()
	{
		TrackIcon.setBackground(data.Occupied != Orientation.None ? (data.Occupied == Orientation.Unknown ? Color.white : Color.red) : data.BlockState != Orientation.None ? (data.BlockState==Orientation.Unknown ? Color.darkGray : (data.shunt ? Color.blue : Color.green)) : Color.yellow);
		if(data.locking)
		{
			FlashingTimer.setRepeats(true);
			FlashingTimer.start();
			FlashingTimer.setInitialDelay(0);
			Locking.setBackground(Color.blue);
		}
		else
		{
			FlashingTimer.setRepeats(false);
			FlashingTimer.stop();
			Locking.setBackground(data.Locked != -1 ? Color.blue : Color.white);
		}
		if(data.Switch==Position.Straight)
		{
			Desv.setOpaque(false);
			Desv.repaint();
			Direct.setOpaque(true);
			Direct.setBackground((data.blockPosition==0&&(data.BlockState!=Orientation.None || data.Occupied != Orientation.None)) ? (data.Occupied != Orientation.None ? Color.red : (data.BlockState==Orientation.Unknown ? Color.darkGray : (data.shunt ? Color.blue : Color.green))) : Color.yellow);
			Direct.repaint();
		}
		else if(data.Switch!=Position.Unknown)
		{
			Direct.setOpaque(false);
			Direct.repaint();
			Desv.setOpaque(true);
			Desv.setBackground((data.blockPosition==1&&(data.BlockState!=Orientation.None || data.Occupied != Orientation.None)) ? (data.Occupied != Orientation.None ? Color.red : (data.BlockState==Orientation.Unknown ? Color.darkGray : (data.shunt ? Color.blue : Color.green))) : Color.yellow);
			Desv.repaint();
		}
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof JunctionData)
		{
			JunctionData d = (JunctionData) p;
			if(!d.id.equals(junctionID)) return;
			data = d;
			update();
		}
	}
	@Override
	public ID getID() {return junctionID;}
}
