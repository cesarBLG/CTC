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

public class AutomaticOrder extends StatePacket implements OrderPacket
{
	public boolean automatic = false;
	public boolean stickClose = true;
	public AutomaticOrder(SignalID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.add(booleanConvert(automatic, stickClose));
		return data;
	}
	public static AutomaticOrder byState(InputStream i) throws IOException
	{
		i.read();
		AutomaticOrder ao = new AutomaticOrder(new SignalID(i));
		boolean[] b = booleanRead(i.read());
		ao.automatic = b[0];
		ao.stickClose = b[1];
		return ao;
	}
}
