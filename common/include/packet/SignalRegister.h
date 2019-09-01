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
#include <vector>
#include "SignalID.h"
class SignalRegister : public StatePacket
{
    public:
	SignalRegister(shared_ptr<SignalID> packetID) : StatePacket(packetID, PacketType::SignalRegister, PacketGroup::Register) {}
	bool Fixed = false;
	bool EoT = false;
	vector<char> getListState()
	{
		vector<char> data = id->getId();
		data.push_back(Fixed);
		data.push_back(EoT);
		return data;
	}
	Packet* byState(char *&data) override
	{
		SignalRegister *s = new SignalRegister(shared_ptr<SignalID>(new SignalID(data)));
		s->Fixed = *data++;
		s->EoT = *data++;
		return s;
	}
};
