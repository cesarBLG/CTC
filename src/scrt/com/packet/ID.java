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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class ID
{
	public enum ElementType
	{
		Signal,
		AC,
		TrackItem,
		Junction
	}
	public ElementType type;
	public int stationNumber;
	public List<Integer> getId()
	{
		List<Integer> l = new ArrayList<>();
		l.add(type.ordinal());
		l.add(stationNumber);
		return l;
	}
	public ID() 
	{
		String name = this.getClass().getSimpleName();
		type = ElementType.valueOf(name.substring(0, name.length() - 2));
	}
	public ID(InputStream b) throws IOException
	{
		//type = ElementType.values()[b.read()];
		String name = this.getClass().getSimpleName();
		type = ElementType.valueOf(name.substring(0, name.length() - 2));
		stationNumber = b.read();
	}
	@Override
	public boolean equals(Object packet)
	{
		if(packet instanceof ID)
		{
			List<Integer> l1 = ((ID) packet).getId();
			List<Integer> l2 = getId();
			if(l1.size()!=l2.size()) return false;
			for(int i=0; i<l1.size(); i++)
			{
				if(!l1.get(i).equals(l2.get(i))) return false;
			}
			return true;
		}
		return super.equals(packet);
	}
	public static ID byState(InputStream i) throws IOException
	{
		ID id = null;
		try
		{
			Class<?> c = Class.forName("scrt.com.packet." + ElementType.values()[i.read()].name() + "ID");
			id = (ID) c.getConstructor(InputStream.class).newInstance(i);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
}
