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
#include <set>
#include <deque>
#include <mutex>
#include "event/SCRTEvent.h"
#include "event/SCRTListener.h"
#include "packet/ID.h"
#include "packet/Packable.h"
#include "packet/PacketManager.h"
using namespace std;
/*namespace ctc
{*/
class CTCItem : public SCRTListener, public Packable {
	public:
	set<SCRTListener*> listeners;
	protected:
	deque<shared_ptr<SCRTEvent>> Queue;
	bool EventsMuted = false;
	public:
	static PacketManager packetManager;
	static mutex mtx;
	CTCItem()
	{
		unique_lock<mutex> lck(mtx);
		packetManager.items.push_back(this);
	}
	virtual shared_ptr<ID> getID() = 0;
	static CTCItem* findId(ID *id)
	{
		for(Packable *p : packetManager.items)
		{
			auto itemid = ((CTCItem*)p)->getID();
			if(id->equals(itemid.get())) return (CTCItem*)p;
		}
		return nullptr;
	}
	virtual ~CTCItem(){}
};
//}