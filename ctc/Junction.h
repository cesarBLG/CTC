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
#include "TrackItem.h"
#include "JunctionPosition.h"
#include "Config.h"
#include "packet/JunctionData.h"
#include "packet/JunctionRegister.h"
#include "packet/JunctionLock.h"
#include "packet/JunctionPositionSwitch.h"
#include <cstdarg>
/*namespace ctc
{*/
class Station;
class Junction : public TrackItem 
{
	public:
	int Number;
	Orientation Direction;
	Position Switch = Position::Unknown;
	Position target = Position::Unknown;
	Position Class;
	int blockPosition = -1;
	int Locked = -1;
	int lockTarget = -1;
	BlockData lockingItem;
	TrackItem *BackItem;
	TrackItem *FrontItems[2];
	Junction *CrossingLinked = nullptr;
	Junction *Linked = nullptr;
	Junction(JunctionRegister *reg)
	{
		isJunction = true;
		x = reg->TrackId->x;
		y = reg->TrackId->y;
		JunctionID *id = (JunctionID*)reg->id.get();
		Number = id->Number;
		Class = reg->Class;
		Direction = reg->Direction;
		station = Station::byNumber(id->stationNumber);
		send(PacketType::JunctionRegister);
		send(PacketType::JunctionPositionSwitch, false);
		updateState();
	}
	Junction(int num, Station *dep, Position p, int x, int y)
	{
		isJunction = true;
		this->x = x;
		this->y = y;
		Number = num;
		station = dep;
		Class = p;
		Direction = num%2==0 ? Orientation::Even : Orientation::Odd;
		send(PacketType::JunctionRegister);
		send(PacketType::JunctionPositionSwitch, false);
		updateState();
	}
	void send(PacketType type, ...) override
	{
		Packet *p;
		switch(type)
		{
			case PacketType::JunctionRegister:
			{
				TrackItemID *id = new TrackItemID();
				id->x = x;
				id->y = y;
				id->stationNumber = station->AssociatedNumber;
				JunctionRegister *reg = new JunctionRegister(static_pointer_cast<JunctionID>(getID()), shared_ptr<TrackItemID>(id));
				reg->Direction = Direction;
				reg->Class = Class;
				p = reg;
			}
				break;
			case PacketType::JunctionData:
			{
				JunctionData *d = new JunctionData(static_pointer_cast<JunctionID>(getID()));
				d->BlockState = BlockState;
				d->shunt = shunt;
				d->Occupied = Occupied;
				d->Locked = Locked;
				d->blockPosition = blockPosition;
				d->Switch = Switch;
				d->locking = lockTarget != Locked;
				p = d;
			}
				break;
			case PacketType::JunctionPositionSwitch:
			{
				va_list ap;
				va_start(ap, type);
				JunctionPositionSwitch *jps = new JunctionPositionSwitch(static_pointer_cast<JunctionID>(getID()), va_arg(ap, int) ? Posibilities::Order : Posibilities::Request);
				jps->position = target;
				p = jps;
			}
				break;
			case PacketType::JunctionLock:
			{
				JunctionLock *jl = new JunctionLock(static_pointer_cast<JunctionID>(getID()));
				jl->order = true;
				jl->value = lockTarget;
				p = jl;
			}
				break;
			default:
				return;
		}
		COM::toSend(p);
	}
	void userChangeSwitch()
	{
		if(Locked!=-1) return;
		if(Switch==Position::Straight) setSwitch(Class);
		else setSwitch(Position::Straight);
	}
	TrackItem *getNext(Orientation o) override
	{
		if(Switch==Position::Unknown) send(PacketType::JunctionPositionSwitch, false);
		if(Direction!=o) return BackItem;
		if(Switch==Position::Unknown || target!=Switch) return nullptr;
		return FrontItems[Switch==Position::Straight ? 0 : 1];
	}
	TrackItem *getNext(TrackItem *t) override
	{
		if((t==FrontItems[0])||(t==FrontItems[1])) return BackItem;
		if(Switch==Position::Unknown || target!=Switch) return nullptr;
		if(t==BackItem) return FrontItems[Switch==Position::Straight ? 0 : 1];
		return nullptr;
	}
	bool canGetFrom(TrackItem *prev)
	{
		if(prev == BackItem) return true;
		if((prev == FrontItems[0]) ^ (Switch==Position::Straight)) return false;
		return true;
	}
	bool blockedFor(TrackItem *t, bool check)
	{
		if(blockPosition == -1 && !check) return true;
		if(blockPosition!=-1&&((t==FrontItems[0]&&blockPosition==0)||(t==FrontItems[1]&&blockPosition==1)||t==BackItem)) return true;
		return false;
	}
	bool lockedFor(TrackItem *t, bool check)
	{
		if(!Config.trailablePoints)
		{
			if((Locked == 0 && Switch!=Position::Straight) || (Locked == 1 && Switch==Position::Straight)) return false;
			if((lockTarget == 0 && Switch!=Position::Straight) || (lockTarget == 1 && Switch==Position::Straight)) return false;
		}
		if(!check) return true;
		if(Locked != lockTarget) return false;
		if(Linked != nullptr && (Linked->Locked != Locked || Linked->lockTarget != Linked->Locked)) return false;
		if(Locked!=-1&&((t==FrontItems[0]&&Locked==0)||(t==FrontItems[1]&&Locked==1)||t==BackItem)) return true;
		return false;
	}
	void block(TrackItem *t)
	{
		if(blockPosition == -1)
		{
			if(t==BackItem) blockPosition = Switch==Position::Straight ? 0 : 1;
			else
			{
				if(FrontItems[0]==t) blockPosition = 0;
				else blockPosition = 1;
			}
		}
	}
	void lock(TrackItem *t, MainSignal *sig)
	{
		int val;
		if(t==BackItem) val = Switch==Position::Straight ? 0 : 1;
		else
		{
			if(FrontItems[0]==t) val = 0;
			else val = 1;
		}
		lockingItem.Item = sig;
		lockingItem.Type = BD_Signal;
		lock(val);
	}
	void lock(int val)
	{
		if(lockTarget==val) return;
		lockTarget = val;
		send(PacketType::JunctionLock);
		if(Linked != nullptr) Linked->lock(val);
		updateState();
	}
	void updateLock(int lock)
	{
		if(Locked == lock) return;
		Locked = lock;
		if(Locked == -1 && lockTarget == -1) lockingItem.Item = nullptr;
		blockChanged();
		if(Linked!=nullptr && Linked->Locked != Locked && Linked->lockTarget != Locked) Linked->lock(Locked);
		else tryToUnlock();
	}
	void tryToUnlock()
	{
		tryToUnlock(true);
	}
	bool tryToUnlock(bool act)
	{
		if(Locked == -1 && lockTarget == -1) return true;
		if(lockingItem.Item!=nullptr && lockingItem.Type == BD_Signal) return false;
		if((BlockState==Orientation::None||BlockingData.Item==nullptr)&&(Occupied==Orientation::None||Occupied==Orientation::Unknown))
		{
			if(act && (Linked==nullptr || Linked->tryToUnlock(false))) lock(-1);
			return true;
		}
		return false;
	}
	void setBlock(Orientation o) override
	{
		if(o == Orientation::None && Occupied == Orientation::None) blockPosition = -1;
		if(BlockState == o) return;
		BlockState = o;
		if(BlockState == Orientation::None) BlockingData.Item = nullptr;
		tryToUnlock();
		blockChanged();
	}
	void tryToFree() override
	{
		TrackItem::tryToFree();
		tryToUnlock();
	}
	void setOverlapBlock() override
	{
		if(BlockingData.Item != nullptr) return;
		bool str = false;
		bool desv = false;
		for(auto it=overlaps.begin(); it!=overlaps.end(); ++it)
		{
			if(FrontItems[0]->overlaps.find(it->first)!=FrontItems[0]->overlaps.end()) str = true;
			if(FrontItems[1]->overlaps.find(it->first)!=FrontItems[1]->overlaps.end()) desv = true;
		}
		if(str && desv) blockPosition = 2;
		else if(str) blockPosition = 0;
		else if(desv) blockPosition = 1;
		else block(BackItem);
		TrackItem::setOverlapBlock();
	}
	void blockChanged() override
	{
		TrackItem::blockChanged();
		if(CrossingLinked!=nullptr)
		{
			set<SCRTListener*> list = CrossingLinked->listeners;
			for(SCRTListener *l : list)
			{
				l->actionPerformed(shared_ptr<SCRTEvent>(new BlockEvent(this, BlockState)));
			}
		}
		if(Linked!=nullptr)
		{
			set<SCRTListener*> list = Linked->listeners;
			for(SCRTListener *l : list)
			{
				l->actionPerformed(shared_ptr<SCRTEvent>(new BlockEvent(this, BlockState)));
			}
		}
	}
	void AxleActions(AxleEvent ae) override;
	void updateState() override
	{
		send(PacketType::JunctionData);
	}
	void updatePosition(Position p);
	bool setSwitch(TrackItem *from, TrackItem *to)
	{
		if(from==FrontItems[0] || to==FrontItems[0]) return setSwitch(Position::Straight);
		if(from==FrontItems[1] || to==FrontItems[1]) return setSwitch(Class);
		return false;
	}
	bool setSwitch(Position p);
	bool connectsTo(Orientation dir, TrackItem *t) override
	{
		return connectsTo(dir, t->x, t->y, dir == Orientation::Even ? t->EvenRotation : t->OddRotation);
	}
	bool connectsTo(Orientation dir, int objx, int objy, int objrot) override
	{
		if(TrackItem::connectsTo(dir, objx, objy, objrot)) return true;
		if(dir!=Direction)
		{
			if(dir == Orientation::Even)
			{
				return x == objx + 1 && y == objy - (Class == Position::Left ? 1 : -1);
			}
			if(dir == Orientation::Odd)
			{
				return x == objx - 1 && y == objy + (Class == Position::Left ? 1 : -1);
			}
		}
		return false;
	}
	vector<TrackItem*> path(TrackItem *destination, Orientation dir, bool start) override
	{
		vector<TrackItem*> l;
		if(destination == this)
		{
			l.push_back(this);
			return l;
		}
		if(dir != Direction)
		{
			if(BackItem==nullptr) return l;
			l = BackItem->path(destination, dir, false);
		}
		else
		{
			int d = pathDepth;
			l = FrontItems[Switch == Position::Straight ? 0 : 1]->path(destination, dir, false);
			pathDepth = d;
			vector<TrackItem*> x;
			if(FrontItems[Switch == Position::Straight ? 1 : 0]!=nullptr) x = FrontItems[Switch == Position::Straight ? 1 : 0]->path(destination, dir, false);
			pathDepth = d;
			if(l.empty() || (x.size() + 5 < l.size())) l = x;
		}
		if(l.empty()) return l;
		l.push_back(this);
		if(start) reverse(l.begin(), l.end());
		return l;
	}
	void load(Packet *p) override
	{
		if(p->statePacket)
		{
			auto id = getID();
			if(!((StatePacket*)p)->id->equals(id.get())) return;
			if(p->type == PacketType::JunctionLock)
			{
				JunctionLock *l = (JunctionLock*)p;
				if(!l->order) updateLock(l->value);
			}
			if(p->type == PacketType::JunctionSwitch)
			{
				/*if(((JunctionSwitch) p).force)
				{
					updatePosition(Switch == Position::Straight ? Class : Position::Straight);
				}
				else if(((JunctionSwitch) p).muelle)
				{
					if(Muelle == -1) Muelle = Switch == Position::Straight ? 0 : 1;
					else Muelle = -1;
				}
				else */userChangeSwitch();
			}
			if(p->type == PacketType::JunctionData)
			{
				JunctionData *d = (JunctionData*)p;
				if(d->BlockState == Orientation::None && BlockState == Orientation::Unknown) setBlock(Orientation::None);
			}
			if(p->type == PacketType::JunctionPositionSwitch)
			{
				if((((JunctionPositionSwitch*) p)->orderType == Posibilities::Comprobation) && (Occupied==Orientation::None || Occupied==Orientation::Unknown)) updatePosition(((JunctionPositionSwitch*) p)->position);
			}
		}
	}
	shared_ptr<ID> getID() override;
};
//}
