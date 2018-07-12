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
package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrackRegister extends StatePacket implements RegisterPacket
{
	public String Name = "";
	public int OddRotation;
	public int EvenRotation;
	public TrackRegister(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.addAll(toList(Name));
		data.add(OddRotation);
		data.add(EvenRotation);
		return data;
	}
	public static TrackRegister byState(InputStream i) throws IOException
	{
		i.read();
		TrackRegister tr = new TrackRegister(new TrackItemID(i));
		tr.Name = toString(i);
		tr.OddRotation = (byte)i.read();
		tr.EvenRotation = (byte)i.read();
		return tr;
	}
}
