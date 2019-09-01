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
#include "Packable.h"
#include <vector>
class PacketManager
{
	public:
	vector<Packable*> items;
	void handlePacket(Packet* p)
	{
		if(p==nullptr) return;
		vector<Packable*> l = items;
		for(int i=0; i<l.size(); i++)
		{
			l[i]->load(p);
		}
		delete p;
	}
};
