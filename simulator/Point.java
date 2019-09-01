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

import scrt.common.packet.ID;
import scrt.common.packet.JunctionID;
import scrt.common.packet.JunctionLock;
import scrt.common.packet.JunctionPositionSwitch;
import scrt.common.packet.JunctionRegister;
import scrt.common.packet.Packable;
import scrt.common.packet.Packet;
import scrt.common.packet.StatePacket;
import scrt.common.packet.JunctionPositionSwitch.Posibilities;
import scrt.common.Position;

public class Point implements Packable
{
	JunctionID id;
	JunctionRegister reg;
	Position position = Position.Straight;
	Position target = Position.Straight;
	int muelle;
	int lock = -1;
	int lockTarget = -1;
	public Point(JunctionRegister reg)
	{
		this.id = (JunctionID) reg.id;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof StatePacket)
		{
			if(!equals(p)) return;
			if(p instanceof JunctionLock)
			{
				lockTarget = ((JunctionLock) p).value;
			}
			if(p instanceof JunctionPositionSwitch)
			{
				if(lockTarget == -1)
				{
					if(((JunctionPositionSwitch) p).orderType == Posibilities.Request)
					{
						sendPosition();
						return;
					}
					target = ((JunctionPositionSwitch) p).position;
				}
			}
			Timer t = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(p instanceof JunctionPositionSwitch)
					{
						if(lockTarget != -1) return;
						position = target;
						sendPosition();
					}
					if(p instanceof JunctionLock)
					{
						lock = lockTarget;
						sendLock();
					}
				}});
			t.setRepeats(false);
			t.start();
		}
	}
	void sendPosition()
	{
		JunctionPositionSwitch jps = new JunctionPositionSwitch(id, Posibilities.Comprobation);
		jps.position = position;
		receiver.send(jps);
	}
	void sendLock()
	{
		JunctionLock l = new JunctionLock(id);
		l.order = false;
		l.value = lock;
		receiver.send(l);
	}
	public boolean equals(Object obj)
	{
		if(obj instanceof Point) return id.equals(((Point) obj).id);
		if(obj instanceof ID) return id.equals(obj);
		if(obj instanceof StatePacket) return id.equals(((StatePacket) obj).id);
		return super.equals(obj);
	}
}
