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
#include "StatePacket.h"
#include "TrackItemID.h"
class TrackRegister: public StatePacket
{
    public:
	string Name = "";
	int OddRotation;
	int EvenRotation;
	TrackRegister(shared_ptr<TrackItemID> packetID) : StatePacket(packetID, PacketType::TrackRegister, PacketGroup::Register) {}
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		vector<char> namelist = toList(Name);
		data.insert(data.end(), namelist.begin(), namelist.end());
		data.push_back(OddRotation);
		data.push_back(EvenRotation);
		return data;
	}
	Packet* byState(char *&data) override
	{
		TrackRegister *tr = new TrackRegister(shared_ptr<TrackItemID>(new TrackItemID(data)));
		tr->Name = toString(data);
		tr->OddRotation = *data++;
		tr->EvenRotation = *data++;
		return tr;
	}
};
