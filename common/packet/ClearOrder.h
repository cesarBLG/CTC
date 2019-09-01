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
using namespace std;
class ClearOrder : public StatePacket
{
	public:
	bool clear = false;
	bool ov = false;
	bool mt = false;
	ClearOrder(shared_ptr<SignalID> id) : StatePacket(id, PacketType::ClearOrder, PacketGroup::Order) {}
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		data.push_back(booleanConvert(clear, ov, mt));
		return data;
	}
	Packet* byState(char *&data) override
	{
		ClearOrder *co = new ClearOrder(shared_ptr<SignalID>(new SignalID(data)));
		co->clear = *data & 1;
		co->ov = *data & 2;
		co->mt = *data & 3;
		data++;
		return co;
	}
};
