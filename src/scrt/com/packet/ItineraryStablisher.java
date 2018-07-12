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

public class ItineraryStablisher extends Packet implements OrderPacket
{
	public TrackItemID start;
	public TrackItemID destination;
	public Orientation dir;
	public ItineraryStablisher(TrackItemID start, TrackItemID destination)
	{
		this.start = start;
		this.destination = destination;
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(start.getId());
		data.addAll(destination.getId());
		data.add(dir.ordinal());
		return data;
	}
	public static ItineraryStablisher byState(InputStream i) throws IOException
	{
		i.read();
		TrackItemID i1 = new TrackItemID(i);
		i.read();
		TrackItemID i2 = new TrackItemID(i);
		ItineraryStablisher ir = new ItineraryStablisher(i1, i2);
		ir.dir = Orientation.values()[i.read()];
		return ir;
	}
}
