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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import scrt.common.packet.JunctionID;
import scrt.common.packet.JunctionLock;
import scrt.common.packet.JunctionPositionSwitch;
import scrt.common.packet.JunctionRegister;
import scrt.common.packet.Packet;
import scrt.common.packet.StatePacket;
import scrt.common.packet.JunctionPositionSwitch.Posibilities;
import scrt.common.Position;

public class Simulator
{
	public static Receiver receiver;
	public static void main(String[] args)
	{
		receiver = new Receiver();
	}
	public static List<Point> points = new ArrayList<>();
	public static void load(Packet p)
	{
		if(p instanceof JunctionRegister)
		{
			JunctionRegister reg = (JunctionRegister) p;
			boolean exists = false;
			for(Point po : points)
			{
				if(po.id.equals(reg.id)) exists = true;
			}
			if(!exists) points.add(new Point(reg));
			return;
		}
		if(!(p instanceof JunctionPositionSwitch || p instanceof JunctionLock)) return;
		for(Point po : points)
		{
			po.load(p);
		}
	}
}
