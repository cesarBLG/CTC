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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet
{
	public enum PacketType
	{
		EmptyPacket,
		LinkPacket,
		SignalData,
		SignalRegister,
		TrackData,
		TrackRegister,
		JunctionData,
		JunctionSwitch,
		JunctionRegister,
		JunctionLock,
		ItineraryStablisher,
		ACData,
		RequestPacket,
		StationRegister,
		ClearOrder,
		AutomaticOrder,
		JunctionPositionSwitch,
		ConfigPacket,
	}
	PacketType type;
	public abstract List<Integer> getListState();
	public Packet()
	{
		type = PacketType.valueOf(this.getClass().getSimpleName());
	}
	public static Packet byState(InputStream in) throws IOException
	{
		try
		{
			char[] magic = new char[4];
			for(int i=0; i<4; i++)
			{
				magic[i] = (char)in.read();
			}
			while(!String.valueOf(magic).equals("SCRT"))
			{
				int i=0;
				for(i=0; i<3; i++)
				{
					magic[i] = magic[i+1];
				}
				magic[i] = (char) in.read();
			}
			int length = in.read();
			byte[] data = new byte[length];
			in.read(data);
			if(data[length - 1] != getControl(data, 0)) return null;
			in = new ByteArrayInputStream(data);
			Class<?> c = Class.forName("scrt.common.packet.".concat(PacketType.values()[in.read()].name()));
			return (Packet) c.getMethod("byState", InputStream.class).invoke(null, in);
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public byte[] getState()
	{
		List<Integer> l = getListState();
		byte[] data = new byte[l.size() + 7];
		data[0] = 'S';
		data[1] = 'C';
		data[2] = 'R';
		data[3] = 'T';
		data[4] = (byte) (l.size() + 2);
		data[5] = (byte) type.ordinal();
		for(int i=0; i<l.size(); i++)
		{
			data[i+6] = l.get(i).byteValue();
		}
		data[data.length - 1] = getControl(data, 5);
		return data;
	}
	public static byte getControl(byte[] data, int offset)
	{
		int control = 0;
		for(int i=offset; i<data.length - 1; i++)
		{
			control += data[i] * (((i-offset) % 4) == 0 ? 4 : ((i-offset) % 4));
		}
		control = 255 - (control % 255);
		return (byte)control;
	}
	public static int booleanConvert(boolean... val)
	{
		int b = 0;
		for(int i=0; i<Math.min(8, val.length); i++)
		{
			b += (val[i] ? 1 : 0)<<i;
		}
		return b;
	}
	public static boolean[] booleanRead(int val)
	{
		boolean b[] = new boolean[8];
		for(int i = 0; i<8; i++)
		{
			b[i] = ((val>>i) & 1) == 1;
		}
		return b;
	}
	public static List<Integer> toList(String s)
	{
		ArrayList<Integer> l = new ArrayList<>();
		for(int i=0;  i<s.length(); i++)
		{
			l.add((int) s.charAt(i));
		}
		l.add(0);
		return l;
	}
	public static String toString(InputStream in) throws IOException
	{
		String s = "";
		int c = in.read();
		while(c!=0)
		{
			s = s + (char)c;
			c = in.read();
		}
		return s;
	}
}
