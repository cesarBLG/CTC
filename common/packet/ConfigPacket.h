/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 * 
 * You should have received a copy of the GNU General License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
#pragma once
#include <string>
#include "Packet.h"
using namespace std;
class ConfigPacket : public Packet
{
	public:
	bool allowOnSight;
	int sigsAhead;
	int anuncioPrecaucion;
	bool openSignals;
	bool lockBeforeBlock;
	bool lock;
	bool trailablePoints;
	bool overlap;
	int D0;
	int D1;
	ConfigPacket() : Packet(PacketType::ConfigPacket, PacketGroup::Data){}
	ConfigPacket(string railway) : Packet(PacketType::ConfigPacket, PacketGroup::Data)
	{
		if(railway == "")
		{
			allowOnSight = true;
			sigsAhead = 2;
			anuncioPrecaucion = 2;
			openSignals = true;
			lockBeforeBlock = false;
			lock = true;
			trailablePoints = false;
			overlap = true;
			D0 = 2000;
			D1 = 10000;
		}
		else if(railway == "FCD")
		{
			allowOnSight = true;
			sigsAhead = 0;
			anuncioPrecaucion = 0;
			openSignals = true;
			lockBeforeBlock = false;
			lock = false;
			trailablePoints = true;
			overlap = false;
			D0 = 2000;
			D1 = 10000;
		}
		else if(railway == "ADIF")
		{
			allowOnSight = false;
			sigsAhead = 2;
			anuncioPrecaucion = 2;
			openSignals = true;
			lockBeforeBlock = true;
			lock = true;
			trailablePoints = false;
			overlap = true;
			D0 = 30000;
			D1 = 150000;
		}
	}
	Packet* byState(char *&data) override
	{
		data++;
		return new ConfigPacket("");
	}
	vector<char> getListState() override
	{
		vector<char> list;
		return list;
	}
};
