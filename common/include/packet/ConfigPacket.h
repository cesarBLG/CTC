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
	ConfigPacket();
	ConfigPacket(string railway);
	Packet* byState(char *&data) override;
	vector<char> getListState() override;
};
