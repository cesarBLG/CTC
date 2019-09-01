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
#include <vector>
#include <map>
#include "CTCItem.h"
#include "Axle.h"
#include "packet/ACID.h"
#include "packet/ACData.h"
#include "train/Train.h"
#include "train/Wagon.h"
#include "Station.h"
using namespace std;
/*namespace ctc
{*/
class Station;
class AxleCounter : public CTCItem
{
	public:
	deque<Axle*> evenAxles;
	deque<Axle*> oddAxles;
	Station *station;
	int Number;
	bool Working = true;
	TrackItem* linked;
	AxleCounter(ACID acid);
	AxleCounter(int num, Station *dep)
	{
		Number = num;
		station = dep;
	}
	void Passed(Orientation direction);
	void EvenPassed()
	{
		Working = true;
		Passed(Orientation::Even);
	}
	void OddPassed()
	{
		Working = true;
		Passed(Orientation::Odd);
	}
	deque<Axle*> &getAxles(Orientation dir)
	{
		return dir == Orientation::Even ? evenAxles : oddAxles;
	}
	bool release;
	TrackItem *start;
	Axle *axle;
	Orientation startDir;
	void Error()
	{
		Working = false;
		Passed(Orientation::Unknown);
	}
	void addListener(SCRTListener *al)
	{
		listeners.insert(al);
	}
	shared_ptr<ID> getID() override;
	void load(Packet *p) override
	{
		if(p->type == PacketType::ACData)
		{
			ACData a = *(ACData*)p;
			auto id = getID();
			if(!a.id->equals(id.get())) return;
			if(a.dir == Orientation::Odd) OddPassed();
			else EvenPassed();
		}
	}
	void muteEvents(bool mute) override {}
	void actionPerformed(shared_ptr<SCRTEvent> e) override {}
};
//}
