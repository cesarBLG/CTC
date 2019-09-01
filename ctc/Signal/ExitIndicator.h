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
#include "Station.h"
#include "TrackItem.h"
#include "event/SCRTEvent.h"
#include "Signal.h"
#include <string>
using namespace std;
/*namespace ctc::signal
{*/
class ExitIndicator : public Signal{
	MainSignal *mainSignal = nullptr;
	public:
	ExitIndicator(string s, Station *dep) : Signal(dep)
	{
		Name = s;
		id = SignalID(s, dep->AssociatedNumber);
		Automatic = true;
		Direction = id.Direction;
		Class = Exit_Indicator;
		Number = id.Number;
		Track = id.Track;
		setAspect();
	}
	void setMain()
	{
		if(mainSignal == nullptr)
		{
			TrackItem *t = Linked;
			if(t == nullptr) return;
			while(t->SignalLinked==nullptr || !(t->SignalLinked->isMainSignal) || t->SignalLinked->Direction != Direction)
			{
				t = t->getNext(Direction);
				if(t == nullptr) return;
			}
			if(t->SignalLinked != nullptr)
			{
				mainSignal = (MainSignal*) t->SignalLinked;
				mainSignal->listeners.insert(this);
			}

		}
	}
	void update() override
	{
		setMain();
		Signal::update();
	}
	void setAspect() override
	{
		Cleared = mainSignal != nullptr && mainSignal->SignalAspect != Parada;
		if(Cleared) SignalAspect = Via_libre;
		else SignalAspect = Parada;
		Signal::setAspect();
	}
	void load(Packet *p) override {}
	void muteEvents(bool mute) override {}
	void actionPerformed(shared_ptr<SCRTEvent> e) override {}
};
//}
