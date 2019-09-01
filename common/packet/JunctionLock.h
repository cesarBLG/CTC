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
class JunctionLock : public StatePacket
{
	public:
	bool order;
	int value;
	JunctionLock(shared_ptr<JunctionID> id) : StatePacket(id, PacketType::JunctionLock, PacketGroup::Action){}
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		data.push_back(order ? 1 :0);
		data.push_back(value);
		return data;
	}
	JunctionLock *byState(char *&data)
	{
		JunctionLock *jl = new JunctionLock(shared_ptr<JunctionID>(new JunctionID(data)));
		jl->order = *data++;
		jl->value = *data++;
		return jl;
	}
};
