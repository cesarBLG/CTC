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
#include <vector>
#include <unordered_set>
#include "com/COM.h"
#include "packet/StationRegister.h"
#include "Signal/Signal.h"
#include "TrackItem.h"
/*namespace ctc
{*/
class Signal;
class Station
{
	public:
	string Name;
	string FullName;
	int AssociatedNumber = 0;
	bool Opened = true;
	vector<Signal*> Signals;
	vector<TrackItem*> Items;
	unordered_set<Train*> Trains;
	bool ML = false;
	//GRP grp* = nullptr;
	static vector<Station*> stations;
	Station(StationRegister *reg)
	{
		stations.push_back(this);
		FullName = reg->name;
		Name = reg->shortName;
		AssociatedNumber = reg->associatedNumber;
		if(AssociatedNumber == 0) Close();
		COM::toSend(reg);
	}
	//@Deprecated
	static int getNumber(string name)
	{
		if(name == "Cen") return 1;
		else if(name == "TmB") return 3;
		else if(name == "CdM") return 4;
		else if(name == "Arb") return 5;
		else if(name == "Los") return 6;
		else if(name == "Car") return 7;
		else if(name == "Tor") return 8;
		else return 0;
	}
	void Open();
	void Close();
	void MandoLocal()
	{
		if(AssociatedNumber == 0||ML) return;
		ML = true;
	}
	void Telemando()
	{
		if(!ML) return;
		ML = false;
	}
	bool isOpen()
	{
		return Opened;
	}
	bool equals(Station *s)
	{
		if(s == nullptr) return false;
		return AssociatedNumber == s->AssociatedNumber;
	}
	string toString()
	{
		return FullName;
	}
	void trainExited(Train *t)
	{
		Trains.erase(t);
	}
	static Station *byNumber(int num)
	{
		for(Station *s : stations)
		{
			if(s->AssociatedNumber == num) return s;
		}
		return nullptr;
	}
	static Station *byName(string name)
	{
		for(Station *s : stations)
		{
			if(s->Name == name) return s;
		}
		return nullptr;
	}
};
//}