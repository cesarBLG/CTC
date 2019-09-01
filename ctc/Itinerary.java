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
#pragma once
#include <map>
#include <string>
#include "Station.h"
using namespace std;
/*namespace ctc
{*/
class Itinerary {
	map<int, int> Switches;
	vector<string> Signals;
	int Number;
	enum struct Itinerary_Type
	{
		Entry,
		Exit,
		Direct
	};
	string Name;
	Itinerary_Type Class;
	Station *station;
	//Itinerary(ItineraryStablisher *r){}
	Itinerary(string name, Station *dep, vector<string> sig, map<int, int> sw)
	{
		Name = name;
		switch(name[0])
		{
			case 'P':
				Class = Itinerary_Type::Direct;
				break;
			case 'R':
				Class = Itinerary_Type::Entry;
				break;
			case 'X':
				Class = Itinerary_Type::Exit;
				break;
		}
		Number = stoi(name.substr(1));
		station = dep;
		Switches.putAll(sw);
		Signals.addAll(sig);
	}
	public void Establish()
	{
		for(TrackItem *t : station->Items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(Switches.containsKey(j.Number))
				{
					if(Switches.get(j.Number)==0)
					{
						if(!j.setSwitch(Position.Straight))
						{
							Undo();
							return;
						}
					}
					else
					{
						if(!j.setSwitch(j.Class))
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
		if(path == null) return;
		for(int i=1; i<path.size()-1; i++)
		{
			if(path.get(i) instanceof Junction)
			{
				Junction j = (Junction)path.get(i);
				j.setSwitch(path.get(i-1), path.get(i+1));
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
};
//}