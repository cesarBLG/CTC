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

public class RequestPacket extends Packet
{
	boolean registers;
	boolean links;
	boolean data;
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.add((registers ? 1 : 0) + (links ? 2 : 0) + (registers ? 4 : 0));
		return data;
	}
	public static RequestPacket byState(InputStream i) throws IOException
	{
		RequestPacket rp = new RequestPacket();
		int get = i.read();
		rp.registers = (get & 1) != 0;
		rp.links = (get & 2) != 0;
		rp.data = (get & 4) != 0;
		return rp; 
	}
}
