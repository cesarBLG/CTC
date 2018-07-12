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

public class ClearOrder extends StatePacket implements OrderPacket
{
	public boolean clear = false;
	public boolean override = false;
	public boolean mt = false;
	public ClearOrder(SignalID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.add((clear ? 1 : 0) + (override ? 2 : 0) + (mt ? 4 : 0));
		return data;
	}
	public static ClearOrder byState(InputStream i) throws IOException
	{
		i.read();
		ClearOrder co = new ClearOrder(new SignalID(i));
		int val = i.read();
		co.clear = (val & 1) != 0;
		co.override = (val & 2) != 0;
		co.mt = (val & 4) != 0;
		return co;
	}
}
