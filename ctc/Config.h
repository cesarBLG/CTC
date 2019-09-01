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
#include "packet/ConfigPacket.h"
//namespace ctc
//{
struct config
{
	bool allowOnSight = true;
	int sigsAhead = 2;
	int anuncioPrecaucion = 2;
	bool openSignals = true;
	bool lockBeforeBlock = false;
	bool lock = true;
	bool trailablePoints = true;
	bool overlap = true;
	int D0 = 2000;
	int D1 = 10000;
	void set(ConfigPacket *p)
	{
		allowOnSight = p->allowOnSight;
		sigsAhead = p->sigsAhead;
		anuncioPrecaucion = p->anuncioPrecaucion;
		openSignals = p->openSignals;
		lockBeforeBlock = p->lockBeforeBlock;
		lock = p->lock;
		trailablePoints = p->trailablePoints;
		overlap = p->overlap;
		D0 = p->D0;
		D1 = p->D1;
		
	}
};
extern config Config;
//}