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
#include "Station.h"
#include "event/SCRTEvent.h"
#include "FixedSignal.h"
#include "Aspect.h"
/*namespace ctc::signal
{*/
class EoT : public FixedSignal 
{
	static int evenCount;
	static int oddCount;
	public:
	EoT(Orientation dir, Station *s) : FixedSignal(Parada, s)
	{
		isEoT = true;
		Number = dir == Orientation::Even ? (evenCount+=2) : (oddCount+=2);
		Track = 0;
		Name = "F"+to_string(Number)+"/0";
		Direction = dir;
		setAspect();
	}
	void Lock() override
	{
	}
	void Unlock() override
	{
	}
	void setState() override
	{
	}
	void actionPerformed(shared_ptr<SCRTEvent> e) override
	{
	}
};
//}
