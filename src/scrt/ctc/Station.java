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
package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.com.COM;
import scrt.com.packet.StationRegister;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;
import scrt.regulation.grp.GRP;
import scrt.train.Train;

public class Station {
	public String Name;
	public String FullName;
	public int AssociatedNumber = 0;
	public boolean Opened = true;
	public List<Signal> Signals = new ArrayList<>();
	public List<TrackItem> Items = new ArrayList<>();
	List<Train> Trains = new ArrayList<>();
	public boolean ML = false;
	public GRP grp = null;
	public static List<Station> stations = new ArrayList<>();
	public Station(StationRegister reg)
	{
		stations.add(this);
		FullName = reg.name;
		Name = reg.shortName;
		AssociatedNumber = reg.associatedNumber;
		if(AssociatedNumber == 0) Close();
		COM.toSend(reg);
	}
	@Deprecated
	public static int getNumber(String name)
	{
		if(name.equals("Cen")) return 1;
		else if(name.equals("TmB")) return 3;
		else if(name.equals("CdM")) return 4;
		else if(name.equals("Arb")) return 5;
		else if(name.equals("Los")) return 6;
		else if(name.equals("Car")) return 7;
		else if(name.equals("Tor")) return 8; 
		else return 0;
	}
	public void Open()
	{
		if(Opened||AssociatedNumber==0) return;
		Opened = true;
		for(Signal s : Signals)
		{
			if(s instanceof MainSignal) ((MainSignal)s).setState();
		}
	}
	public void Close()
	{
		if(!Opened) return;
		Opened = false;
		for(Signal s : Signals)
		{
			if(s instanceof MainSignal) ((MainSignal)s).setState();
		}
	}
	public void MandoLocal()
	{
		if(AssociatedNumber == 0||ML) return;
		ML = true;
	}
	public void Telemando()
	{
		if(!ML) return;
		ML = false;
	}
	public boolean isOpen()
	{
		return Opened;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o instanceof Station)
		{
			return this.AssociatedNumber == ((Station)o).AssociatedNumber;
		}
		return false;
	}
	@Override
	public String toString()
	{
		return FullName;
	}
	public void trainExited(Train t)
	{
		Trains.remove(t);
	}
	public static Station byNumber(int num)
	{
		for(Station s : stations)
		{
			if(s.AssociatedNumber == num) return s;
		}
		return null;
	}
	public static Station byName(String name)
	{
		for(Station s : stations)
		{
			if(s.Name.equals(name)) return s;
		}
		return null;
	}
}
