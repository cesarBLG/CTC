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
import java.util.List;

import scrt.Orientation;

public class ACID extends ID
{
	public int Num;
	public Orientation dir;
	public ACID() {}
	public ACID(InputStream i) throws IOException
	{
		super(i);
		Num = i.read();
		dir = Num % 2 == 0 ? Orientation.Even : Orientation.Odd;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Num);
		return l;
	}
}
