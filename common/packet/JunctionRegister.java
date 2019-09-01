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
package scrt.common.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.common.Orientation;
import scrt.common.Position;

public class JunctionRegister extends StatePacket implements RegisterPacket
{
	public TrackItemID TrackId;
	public Orientation Direction;
	public Position Class;
	public JunctionRegister(JunctionID id1, TrackItemID id2)
	{
		super(id1);
		TrackId = id2;
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.addAll(TrackId.getId());
		data.add(Direction.ordinal());
		data.add(Class.ordinal());
		return data;
	}
	public static JunctionRegister byState(InputStream i) throws IOException
	{
		i.read();
		JunctionID i1 = new JunctionID(i);
		i.read();
		TrackItemID i2 = new TrackItemID(i);
		JunctionRegister jr = new JunctionRegister(i1, i2);
		jr.Direction = Orientation.values()[i.read()];
		jr.Class = Position.values()[i.read()];
		return jr;
	}
}
