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
#include "ID.h"
#include <vector>
#include "Packet.h"
class LinkPacket : public Packet
{
	public:
	shared_ptr<ID> id1;
	shared_ptr<ID> id2;
	LinkPacket() : Packet(PacketType::LinkPacket, PacketGroup::Link){}
	LinkPacket(shared_ptr<ID> linked1, shared_ptr<ID> linked2) : Packet(PacketType::LinkPacket, PacketGroup::Link), id1(linked1), id2(linked2) {}
	vector<char> getListState() override
	{
		vector<char> data = id1->getId();
		vector<char> id2data = id2->getId();
		data.insert(data.end(), id2data.begin(), id2data.end());
		return data;
	}
	Packet* byState(char *&data) override
	{
		return new LinkPacket(shared_ptr<ID>(ID::create(data)), shared_ptr<ID>(ID::create(data)));
	}
};
