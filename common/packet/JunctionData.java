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

public class JunctionData extends StatePacket implements DataPacket
{
	public Orientation BlockState;
	public boolean shunt = false;
	public Orientation Occupied;
	public Position Switch;
	public int Locked;
	public int blockPosition;
	public boolean locking;
	public JunctionData(JunctionID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(shunt ? 1 : 0);
		data.add(Occupied.ordinal());
		data.add(Switch.ordinal());
		data.add(Locked);
		data.add(blockPosition);
		data.add(locking ? 1 : 0);
		return data;
	}
	public static JunctionData byState(InputStream i) throws IOException
	{
		i.read();
		JunctionData jd = new JunctionData(new JunctionID(i));
		jd.BlockState = Orientation.values()[i.read()];
		jd.shunt = i.read() == 1;
		jd.Occupied = Orientation.values()[i.read()];
		jd.Switch = Position.values()[i.read()];
		jd.Locked = (byte)i.read();
		jd.blockPosition = (byte)i.read();
		jd.locking = i.read() == 1;
		return jd;
	}
}
