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
#include "Aspect.h"
#include "StatePacket.h"
#include "SignalID.h"
class SignalData : public StatePacket
{
	public:
	SignalData(shared_ptr<SignalID> packetID) : StatePacket(packetID, PacketType::SignalData, PacketGroup::Data){}
	Aspect SignalAspect;
	bool Automatic;
	bool stickClose = true;
	bool UserRequest;
	bool OverrideRequest;
	bool ClearRequest;
	bool MT = false;
	vector<char> getListState() override
	{
		vector<char> data = id->getId();
		data.push_back((char)SignalAspect);
		data.push_back(booleanConvert(Automatic, stickClose));
		data.push_back(UserRequest);
		data.push_back(OverrideRequest);
		data.push_back(ClearRequest);
		data.push_back(MT);
		return data;
	}
	Packet* byState(char *&data) override
	{
		SignalData *s = new SignalData(shared_ptr<SignalID>(new SignalID(data)));
		s->SignalAspect = (Aspect)*data++;
		s->Automatic = *data & 1;
		s->stickClose = *data & 2;
		data++;
		s->UserRequest = *data++;
		s->OverrideRequest = *data++;
		s->ClearRequest = *data++;
		s->MT = *data++;
		return s;
	}
};
