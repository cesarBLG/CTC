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
#include "event/SCRTEvent.h"
#include "Signal.h"
#include "MainSignal.h"
#include "Orientation.h"
#include <string>
using namespace std;
/*namespace ctc::signal
{*/
class FixedSignal : public MainSignal 
{
	public:
	bool prevClear = false;
	protected:
	FixedSignal(Aspect a, Station *dep) : MainSignal(dep)
	{
		isFixed = true;
		Class = Entry;
		Automatic = true;
		Aspects.insert(a);
		allowsOnSight = true;
		Cleared = a != Aspect::Parada;
		prevClear = !Cleared;
	}
	public:
	FixedSignal(string s, Orientation dir, Aspect a, Station *dep) : MainSignal(dep)
	{
		isFixed = true;
		Name = s;
		if(Name.size()!=0)
		{
			Number = stoi(Name.substr(1,Name.find_first_of('/')-1));
			Track = 0;
		}
		//else 
		/*if(Name.charAt(1)=='S') Class = Exit;
		else if(Name[1]=='E' && Name.charAt[1]!='\'')Class = Entry;
		else if(Name[1]=='E' && Name.charAt[1]=='\'') Class = Advanced;
		else if(Name[1]=='M') Class = Shunting;
		else */Class = Entry;
		Automatic = true;
		Direction = dir;
		Aspects.insert(a);
		allowsOnSight = true;
		Cleared = a != Aspect::Parada;
		prevClear = !Cleared;
		setState();
		setAspect();
	}
	void setAutomatic(bool b) override{}
	void setClearRequest() override
	{
		if(!BlockRequest() && !TrackRequest()) UserRequest = false;
		MainSignal::setClearRequest();
	}
	bool TrackRequest() override
	{
		return trainInProximity();
	}
	void setAspect() override
	{
		SignalAspect = *Aspects.begin();
		send();
	}
	int maxSigsAhead() override
	{
		return SigsAhead();
	}
	int SigsAhead() override
	{
		if(!Cleared) return 0;
		return 1;
	}
};
//}
