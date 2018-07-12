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
import java.util.Hashtable;
import java.util.List;

import scrt.Orientation;
import scrt.com.packet.ItineraryStablisher;
import scrt.com.packet.Packet;
import scrt.ctc.Junction.Position;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;

public class Itinerary {
	Hashtable<Integer, Integer> Switches = new Hashtable<>();
	List<String> Signals = new ArrayList<>();
	int Number;
	enum Itinerary_Type
	{
		Entry,
		Exit,
		Direct
	}
	String Name;
	Itinerary_Type Class;
	Station Station;
	Itinerary(ItineraryStablisher r){}
	Itinerary(String name, Station dep, List<String> sig, Hashtable<Integer, Integer> sw)
	{
		Name = name;
		switch(name.charAt(0))
		{
			case 'P':
				Class = Itinerary_Type.Direct;
				break;
			case 'R':
				Class = Itinerary_Type.Entry;
				break;
			case 'X':
				Class = Itinerary_Type.Exit;
				break;
		}
		Number = Integer.parseInt(name.substring(1));
		Station = dep;
		Switches.putAll(sw);
		Signals.addAll(sig);
	}
	public void Establish()
	{
		for(TrackItem t : Station.Items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(Switches.containsKey(j.Number))
				{
					if(Switches.get(j.Number)==0)
					{
						j.setSwitch(Position.Straight);
						if(j.Switch!=Position.Straight)
						{
							Undo();
							return;
						}
					}
					else
					{
						j.setSwitch(j.Class);
						if(j.Switch!=j.Class)
						{
							Undo();
							return;
						}
					}
				}
			}
		}
		for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(!(sig instanceof MainSignal)) continue;
				if(sig.Name.equalsIgnoreCase(s))
				{
					((MainSignal) sig).UserRequest(true);
					if(!sig.Cleared)
					{
						Undo();
						return;
					}
				}
			}
		}
	}
	public boolean Established()
	{
		for(TrackItem t : Station.Items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(Switches.containsKey(j.Number))
				{
					if(Switches.get(j.Number)==0)
					{
						if(j.Switch!=Position.Straight) return false;
					}
					else if(j.Switch!=j.Class) return false;
				}
			}
		}
		for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(!(sig instanceof MainSignal)) continue;
				if(sig.Name.equalsIgnoreCase(s))
				{
					if(!sig.Cleared) return false;
				}
			}
		}
		return true;
	}
	public void Undo()
	{
		/*for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(sig.Name.equalsIgnoreCase(s) && sig.Station.equals(Station))
				{
					sig.Close();
				}
			}
		}*/
	}
	@Override
	public String toString()
	{
		return Name.concat("/").concat(Station.Name);
	}
	public static void set(TrackItem s, TrackItem e, Orientation dir, boolean shunt)
	{
		List<TrackItem> path = s.path(e, dir, true);
		for(TrackItem t : path)
		{
			if(path.indexOf(t) < path.size() - 1 && !path.contains(t.getNext(dir)))
			{
				Junction j = (Junction)t;
				j.setSwitch(j.Switch == Position.Straight ? j.Class : Position.Straight);
			}
		}
		for(TrackItem t : path)
		{
			if(t==e) break;
			if(t.SignalLinked != null && t.SignalLinked.Direction == dir && t.SignalLinked instanceof MainSignal)
			{
				t.SignalLinked.OverrideRequest = shunt;
				((MainSignal)t.SignalLinked).UserRequest(true);
			}
		}
	}
	public static void handlePacket(Packet p)
	{
		if(p instanceof ItineraryStablisher)
		{
			ItineraryStablisher r = (ItineraryStablisher) p;
			set((TrackItem)CTCItem.findId(r.start), (TrackItem)CTCItem.findId(r.destination), r.dir, false);
		}
	}
}
