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
package scrt.simulator;

import static scrt.simulator.Simulator.receiver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionLock;
import scrt.com.packet.JunctionPositionSwitch;
import scrt.com.packet.JunctionPositionSwitch.Posibilities;
import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.ctc.Junction.Position;

public class Point implements Packable
{
	JunctionID id;
	Position position;
	Position target;
	int muelle;
	int lockTarget;
	@Override
	public void load(Packet p)
	{
		if(p instanceof StatePacket)
		{
			if(((StatePacket)p).id.equals(id)) return;
			Timer t = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(p instanceof JunctionPositionSwitch)
					{
						JunctionPositionSwitch jps = new JunctionPositionSwitch((JunctionID) ((JunctionPositionSwitch) p).id, Posibilities.Comprobation);
						jps.position = ((JunctionPositionSwitch) p).orderType == Posibilities.Order ? ((JunctionPositionSwitch) p).position : Position.Straight;
						receiver.send(jps);
					}
					if(p instanceof JunctionLock)
					{
						JunctionLock l = new JunctionLock((JunctionID) ((JunctionLock) p).id);
						l.order = false;
						l.value = ((JunctionLock) p).value;
						receiver.send(l);
					}
				}});
			t.setRepeats(false);
			t.start();
		}
	}
}
