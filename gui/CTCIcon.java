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

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import scrt.common.packet.ID;
import scrt.common.packet.JunctionRegister;
import scrt.common.packet.Packable;
import scrt.common.packet.Packet;
import scrt.common.packet.PacketManager;
import scrt.common.packet.RegisterPacket;
import scrt.common.packet.SignalRegister;
import scrt.common.packet.TrackRegister;

public abstract class CTCIcon implements Packable {
	public abstract void update();
	public static List<CTCIcon> items = new ArrayList<>();
	public JPanel comp;
	public static Receiver receiver;
	public static GridBagConstraints gbc;
	public static JPanel layout;
	public static int maxx = 0;
	public static int minx = Integer.MAX_VALUE;
	public static int maxy = 0;
	public static int miny = Integer.MAX_VALUE;
	public static PacketManager PacketManager = new PacketManager()
			{
				@Override
				public void handlePacket(Packet p)
				{
					synchronized(CTCIcon.items)
					{
						if(p instanceof RegisterPacket)
						{
							if(p instanceof SignalRegister)
							{
								CTCIcon.items.add(new SignalIcon((SignalRegister)p));
								return;
							}
							if(p instanceof TrackRegister)
							{
								CTCIcon.items.add(new TrackIcon((TrackRegister)p));
								return;
							}
							if(p instanceof JunctionRegister)
							{
								CTCIcon.items.add(new JunctionIcon((JunctionRegister)p));
								return;
							}
						}
						for(CTCIcon i : CTCIcon.items)
						{
							i.load(p);
						}
					}
				}
			};
	public abstract ID getID();
	public static CTCIcon findID(ID id)
	{
		synchronized(items)
		{
			for(CTCIcon i : items)
			{
				if(id.equals(i.getID())) return i;
			}
		}
		return null;
	}
}
