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
#include "Orientation.h"
#include <vector>
#include <string>
#include "TrackComparer.h"
#include "CTCItem.h"
#include "CTCTimer.h"
#include "com/COM.h"
#include "packet/Packet.h"
#include "packet/TrackRegister.h"
#include "packet/TrackData.h"
#include "Clock.h"
#include "event/AxleEvent.h"
#include "event/BlockEvent.h"
#include "event/OccupationEvent.h"
using namespace std;
class MainSignal;
/*namespace ctc
{*/
class TrackItem : public CTCItem
{
	public:
	Orientation BlockState = Orientation::None;
	bool shunt = false;
	Orientation Occupied = Orientation::None;
	Signal *SignalLinked = nullptr;
	AxleCounter *CounterLinked = nullptr;
	Station *station;
	string Name = "";
	Orientation CounterDir = Orientation::None;
	TrackItem *EvenItem = nullptr;
	TrackItem *OddItem = nullptr;
	int EvenAxles = 0;
	int OddAxles = 0;
	int x = 0;
	int y = 0;
	int OddRotation = 0;
	int EvenRotation = 0;
	bool Acknowledged = true;
	bool invert = false;
	bool isJunction=false;
	vector<Train*> trains;
	void setSignal(Signal *sig);
	TrackItem()
	{
		
	}
	TrackItem(TrackRegister *reg);
	TrackItem(string label, Station* dep, int oddrot, int evenrot, int x, int y) : x(x), y(y)
	{
		station = dep;
		if(oddrot==2) oddrot = -1;
		if(evenrot==2) evenrot = -1;
		OddRotation = oddrot;
		EvenRotation = evenrot;
		Name = label;
		send(PacketType::TrackRegister);
		updateState();
	}
	virtual TrackItem *getNext(Orientation dir)
	{
		if(dir==Orientation::Even) return EvenItem;
		if(dir==Orientation::Odd) return OddItem;
		return nullptr;
	}
	virtual TrackItem *getNext(TrackItem *t)
	{
		if(t==OddItem) return EvenItem;
		if(t==EvenItem) return OddItem;
		return nullptr;
	}
	enum BlockDataElements
	{
		BD_None,
		BD_Signal,
		BD_Axle
	};
	struct BlockData
	{
		void *Item;
		BlockDataElements Type;
		time_t Time;
	} BlockingData;
	virtual void setBlock(Orientation o, MainSignal *blocksignal, bool shunt)
	{
		this->shunt = shunt;
		if(BlockingData.Item==nullptr)
		{
			BlockingData.Item = blocksignal;
			BlockingData.Type = BD_Signal;
		}
		BlockingData.Time = Clock::time();
		setBlock(o);
	}
	virtual void setBlock(Orientation o)
	{
		if(BlockState == o) return;
		BlockState = o;
		if(BlockState == Orientation::None)
		{
			BlockingData.Item = nullptr;
			shunt = false;
		}
		blockChanged();
	}
	virtual void blockChanged();
	virtual void updateState()
	{
		send(PacketType::TrackData);
	}
	bool wasFree = true;
	time_t OccupiedTime = 0;
	virtual void AxleActions(AxleEvent ae);
	CTCTimer trainStopTimer = CTCTimer(10000, [this]()
			{
				set<SCRTListener*> list = listeners;
				for(SCRTListener *l : list)
				{
					l->actionPerformed(shared_ptr<SCRTEvent>(new OccupationEvent(this, Orientation::Unknown, 0)));
				}
			});
	set<Axle*> axles;
	void updateOccupancy(AxleEvent ae);
	bool trainStopped();
	map<TrackItem*, Orientation> overlaps;
	void setOverlap(TrackItem *t, Orientation dir)
	{
		if(overlaps.find(t)!=overlaps.end() || (BlockState != Orientation::None && dir != BlockState && BlockingData.Item != nullptr)) return;
		t->listeners.insert(this);
		overlaps[t] = dir;
		setOverlapBlock();
	}
	void removeOverlap(TrackItem *t)
	{
		if(overlaps.find(t)!=overlaps.end())
		{
			overlaps.erase(t);
			t->listeners.erase(this);
			setOverlapBlock();
		}
	}
	bool evenOv = false;
	bool oddOv = false;
	virtual void setOverlapBlock()
	{
		if(BlockingData.Item != nullptr) return;
		bool even = false;
		bool odd = false;
		for(auto it = overlaps.begin(); it!=overlaps.end(); ++it)
		{
			if(it->second==Orientation::Even) even = true;
			if(it->second==Orientation::Odd) odd = true;
		}
		if(even&&odd) setBlock(Orientation::Both);
		else if(even) setBlock(Orientation::Even);
		else if(odd) setBlock(Orientation::Odd);
		else setBlock(Orientation::None);
		if(even!=evenOv || odd!=oddOv)
		{
			evenOv = even;
			oddOv = odd;
			blockChanged();
		}
	}
	bool free;
	virtual void tryToFree();
	/*public interface TrackComparer
	{
		boolean condition(TrackItem t, Orientation dir, TrackItem p);
		boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p);
	}*/
	static bool DirectExploration(TrackItem *start, shared_ptr<TrackComparer> tc, Orientation dir);
	static void InverseExploration(TrackItem *start, shared_ptr<TrackComparer> tc, Orientation dir);
	virtual bool connectsTo(Orientation dir, TrackItem *t)
	{
		return connectsTo(dir, t->x, t->y, dir == Orientation::Even ? t->EvenRotation : t->OddRotation);
	}
	virtual bool connectsTo(Orientation dir, int objx, int objy, int objrot)
	{
		if(dir == Orientation::Even)
		{
			if(objrot == OddRotation) return x == objx + 1 && y == objy - objrot;
		}
		if(dir == Orientation::Odd)
		{
			if(objrot == EvenRotation) return x == objx - 1 && y == objy + objrot;
		}
		return false;
	}
	void actionPerformed(shared_ptr<SCRTEvent> e) override;
	bool Muted = false;
	void muteEvents(bool mute) override
	{
		Muted = mute;
	}
	string toString()
	{
		return to_string(x) + ", " + to_string(y);
	}
	static int pathDepth;
	virtual vector<TrackItem*> path(TrackItem *destination, Orientation dir, bool start)
	{
		vector<TrackItem*> l;
		if(pathDepth>70)
		{
			if(start) pathDepth = 0;
			return l;
		}
		if(destination == this)
		{
			l.push_back(this);
			return l;
		}
		TrackItem *item = dir == Orientation::Odd ? OddItem : EvenItem;
		if(item == nullptr) return l;
		else
		{
			pathDepth++;
			l = item->path(destination, dir, false);
		}
		if(l.empty()) return l;
		l.push_back(this);
		if(start)
		{
			reverse(l.begin(), l.end());
			pathDepth = 0;
		}
		return l;
	}
	shared_ptr<ID> getID() override;
	void load(Packet *p) override;
	virtual void send(PacketType type, ...)
	{
		Packet *p;
		switch(type)
		{
			case PacketType::TrackRegister:
			{
				TrackRegister *reg = new TrackRegister(static_pointer_cast<TrackItemID>(getID()));
				reg->Name = Name;
				reg->OddRotation = OddRotation;
				reg->EvenRotation = EvenRotation;
				p = reg;
			}
				break;
			case PacketType::TrackData:
			{
				TrackData *d = new TrackData(static_pointer_cast<TrackItemID>(getID()));
				d->Acknowledged = Acknowledged;
				d->BlockState = BlockState;
				d->shunt = shunt;
				d->Occupied = Occupied;
				d->EvenAxles = EvenAxles;
				d->OddAxles = OddAxles;
				p = d;
			}
				break;
			default:
				return;
		}
		COM::toSend(p);
	}
	void setCounterLinked(AxleCounter *ac, Orientation dir);
};
//}
