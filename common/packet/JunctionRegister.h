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
#include "Orientation.h"
#include "StatePacket.h"
#include "JunctionPosition.h"
#include "JunctionID.h"
#include "TrackItemID.h"

class JunctionRegister : public StatePacket
{
	public:
	shared_ptr<TrackItemID> TrackId;
	Orientation Direction;
	Position Class;
	JunctionRegister(shared_ptr<JunctionID> id1, shared_ptr<TrackItemID> id2) : StatePacket(id1, PacketType::JunctionRegister, PacketGroup::Register)
	{
		TrackId = id2;
	}
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		vector<char> tid = TrackId->getId();
		data.insert(data.end(), tid.begin(), tid.end());
		data.push_back((int)Direction);
		data.push_back((int)Class);
		return data;
	}
	JunctionRegister *byState(char *&data) override
	{
		JunctionRegister *jr = new JunctionRegister(shared_ptr<JunctionID>(new JunctionID(data)), shared_ptr<TrackItemID>(new TrackItemID(data)));
		jr->Direction = (Orientation)*data++;
		jr->Class = (Position)*data++;
		return jr;
	}
};
