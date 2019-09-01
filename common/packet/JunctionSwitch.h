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

class JunctionSwitch : public StatePacket
{
	public:
	bool force = false;
	bool muelle = false;
	JunctionSwitch(shared_ptr<JunctionID> id) : StatePacket(id, PacketType::JunctionSwitch, PacketGroup::Action){}
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		data.push_back((force ? 1 : 0) + (muelle ? 2 : 0));
		return data;
	}
	JunctionSwitch *byState(char *&data) override
	{
		JunctionSwitch *js = new JunctionSwitch(shared_ptr<JunctionID>(new JunctionID(data)));
		int state = *data++;
		int val = *data++;
		js->force = (val & 1) != 0;
		js->muelle = (val & 2) != 0;
		return js;
	}
};
