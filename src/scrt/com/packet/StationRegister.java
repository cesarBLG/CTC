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

public class StationRegister extends Packet implements RegisterPacket
{
	public int associatedNumber = 0;
	public String name = "";
	public String shortName = "";
	public StationRegister(int number)
	{
		associatedNumber = number;
	}
	@Override
	public List<Integer> getListState()
	{
		var l = new ArrayList<Integer>();
		l.add(associatedNumber);
		l.addAll(toList(name));
		l.addAll(toList(shortName));
		return l;
	}
	public static StationRegister byState(InputStream i) throws IOException
	{
		var s = new StationRegister(i.read());
		s.name = toString(i);
		s.shortName = toString(i);
		return s;
	}
}
