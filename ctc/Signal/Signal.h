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
#include "Orientation.h"
#include "SignalType.h"
#include "com/COM.h"
#include "packet/Packet.h"
#include "packet/SignalData.h"
#include "packet/SignalID.h"
#include "packet/SignalRegister.h"
#include "CTCItem.h"
#include "event/SCRTEvent.h"
#include "event/SCRTListener.h"
#include "event/SignalEvent.h"
#include "Aspect.h"
using namespace std;
/*namespace ctc::signal
{*/
class Station;
class TrackItem;
class Signal : public CTCItem
{
	public:
	Orientation Direction;
	string Name = "";
	SignalType Class;
	bool isMainSignal = false;
	bool isFixed = false;
	bool isEoT = false;
	bool Automatic = false;
	bool Cleared = false;
	bool ClearRequest = false;
	bool OverrideRequest = false;
	Aspect SignalAspect = Parada;
	set<Aspect> Aspects;
	Station* station;
	int Track;
	int Number;
	TrackItem* Linked;
	Aspect LastAspect;
	SignalID id;
	Signal(Station *s);
	void setLinked(TrackItem *t);
	virtual void setAspect(){send();};
	protected:
	void send()
	{
		//if(LastAspect==SignalAspect&&LastAuto==Automatic) return;
		if(Linked==nullptr) return;
		if(LastAspect!=SignalAspect)
		{
			//Logger.trace(this, SignalAspect.name());
			shared_ptr<SCRTEvent> e(new SignalEvent(this));
			set<SCRTListener*> list = listeners;
			for(SCRTListener *l : list) l->actionPerformed(e);
		}
		send(PacketType::SignalData);
		LastAspect = SignalAspect;
	}
	public:
	virtual void update() {setAspect();}
	virtual bool protects() {return false;}
	private:
	bool idCreated = false;
	public:
	shared_ptr<ID> getID() override;
	virtual void send(PacketType type);
	static Signal* construct(SignalRegister *reg);
	/*@Override
	public string toString()
	{
		return "Señal " + Name + " de " + Station.FullName;
	}*/
	void actionPerformed(shared_ptr<SCRTEvent> e) override
	{
		update();
	}
};
//}
