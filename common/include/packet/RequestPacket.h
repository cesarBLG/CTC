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
#include "Packet.h"
#include <vector>
class RequestPacket : public Packet
{
	bool registers;
	bool links;
	bool data;
	public:
	RequestPacket() : Packet(PacketType::RequestPacket, PacketGroup::None){}
	vector<char> getListState() override
	{
		vector<char> d;
		d.push_back(booleanConvert(registers, links, data));
		return d;
	}
	Packet* byState(char *&d) override
	{
		RequestPacket *rp = new RequestPacket();
		rp->registers = *d & 1;
		rp->links = *d & 2;
		rp->data = *d & 4;
		return rp; 
	}
};
