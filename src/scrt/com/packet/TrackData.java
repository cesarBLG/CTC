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

import scrt.Orientation;

public class TrackData extends StatePacket implements DataPacket
{
	public Orientation BlockState;
	public boolean shunt;
	public Orientation Occupied;
	public int OddAxles;
	public int EvenAxles;
	public boolean Acknowledged;
	public TrackData(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(shunt ? 1 : 0);
		data.add(Occupied.ordinal());
		data.add(OddAxles);
		data.add(EvenAxles);
		data.add(Acknowledged ? 1 : 0);
		return data;
	}
	public static TrackData byState(InputStream i) throws IOException
	{
		i.read();
		TrackData td = new TrackData(new TrackItemID(i));
		td.BlockState = Orientation.values()[i.read()];
		td.shunt = i.read() == 1;
		td.Occupied = Orientation.values()[i.read()];
		td.OddAxles = (byte)i.read();
		td.EvenAxles = (byte)i.read();
		td.Acknowledged = i.read() == 1;
		return td;
	}
}
