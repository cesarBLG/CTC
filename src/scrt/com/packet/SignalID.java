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
import scrt.ctc.Signal.Signal.SignalType;

public class SignalID extends ID
{
	public SignalType Class;
	public int Number;
	public int Track;
	public Orientation Direction;
	public String Name;
	public SignalID(InputStream b) throws IOException
	{
		super(b);
		Class = SignalType.values()[b.read()];
		Number = b.read();
		Track = b.read();
		Direction = ((Number % 2) == 0) ? Orientation.Even : Orientation.Odd;
		Name = Class.toString() + Integer.toString(Number) + (Track!=0 ? "/" + Integer.toString(Track) : "");
	}
	public SignalID(String name, int station)
	{
		Name = name;
		if(Name.charAt(0)=='S') Class = SignalType.Exit;
		else if((Name.charAt(0)=='E' && Name.charAt(1)!='\'') || Name.charAt(0) == 'F') Class = SignalType.Entry;
		else if(Name.startsWith("E'")) Class = SignalType.Advanced;
		else if(Name.charAt(0)=='M') Class = SignalType.Shunting;
		else if(Name.startsWith("IS")) Class = SignalType.Exit_Indicator;
		else Class = SignalType.Block;
		int start1 = -1;
		int end1 = 0;
		int start2 = 0;
		for(int i=0; i<Name.length();i++)
		{
			if(Name.charAt(i)<='9'&&Name.charAt(i)>='0'&&start1==-1) start1 = i;
			if(Name.charAt(i)=='/') end1 = i;
			if(Name.charAt(i)<='9'&&Name.charAt(i)>='0'&&end1!=0&&start2==0) start2 = i;
		}
		if(end1==0) end1 = Name.length();
		Number = Integer.parseInt(Name.substring(start1, end1));
		if(start2!=0) Track = Integer.parseInt(Name.substring(start2));
		else Track = 0;
		Direction = Number%2 == 0 ? Orientation.Even : Orientation.Odd;
		stationNumber = station;
		Name = Class.toString() + Integer.toString(Number) + (Track!=0 ? "/" + Integer.toString(Track) : "");
	}
	public SignalID(){}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Class.ordinal());
		l.add(Number);
		l.add(Track);
		return l;
	}
}
