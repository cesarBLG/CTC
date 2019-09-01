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
#include "StatePacket.h"
#include "JunctionID.h"

enum struct Posibilities
{
	Request,
	Order,
	Comprobation,
};
class JunctionPositionSwitch : public StatePacket
{
	public:
	Posibilities orderType;
	Position position;
	JunctionPositionSwitch(shared_ptr<JunctionID> id, Posibilities type) : StatePacket(id, PacketType::JunctionPositionSwitch, PacketGroup::Action)
	{
		orderType = type;
	}
	vector<char> getListState() override
	{
		vector<char> l = id->getId();
		l.push_back((int)orderType);
		l.push_back((int)position);
		return l;
	}
	JunctionPositionSwitch *byState(char *&data)
	{
		JunctionPositionSwitch *jps = new JunctionPositionSwitch(shared_ptr<JunctionID>(new JunctionID(data)), (Posibilities)*data++);
		jps->position = (Position)*data++;
		return jps;
	}

};
