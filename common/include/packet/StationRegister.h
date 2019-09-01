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
#include <string>
#include "Packet.h"
class StationRegister : public Packet
{
    public:
	int associatedNumber = 0;
	string name = "";
	string shortName = "";
	StationRegister(int number) : Packet(PacketType::StationRegister, PacketGroup::Register)
	{
		associatedNumber = number;
	}
	vector<char> getListState() override
	{
		vector<char> l;
		l.push_back(associatedNumber);
		vector<char> namelist = toList(name);
		vector<char> shortlist = toList(shortName);
		l.insert(l.end(), namelist.begin(), namelist.end());
		l.insert(l.end(), shortlist.begin(), shortlist.end());
		return l;
	}
	Packet* byState(char *&data) override
	{
		StationRegister *s = new StationRegister(*data++);
		s->name = toString(data);
		s->shortName = toString(data);
		return s;
	}
};
