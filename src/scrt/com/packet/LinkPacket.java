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

public class LinkPacket extends Packet
{
	public ID id1;
	public ID id2;
	public LinkPacket(ID linked1, ID linked2)
	{
		id1 = linked1;
		id2 = linked2;
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id1.getId());
		data.addAll(id2.getId());
		return data;
	}
	public static LinkPacket byState(InputStream i) throws IOException
	{
		return new LinkPacket(ID.byState(i), ID.byState(i));
	}
}
