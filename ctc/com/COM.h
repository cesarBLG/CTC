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
#include <thread>
#include <mutex>
#include <condition_variable>
#include "packet/Device.h"
#include "packet/Packet.h"
#include "packet/LinkPacket.h"
#include "packet/StatePacket.h"
#include "tcp/TCP.h"
using namespace std;
class COM
{
	static set<Device*> devs;
	public:
	static deque<Packet*> inQueue;
	static deque<Packet*> outQueue;
	static mutex inmtx;
	static mutex outmtx;
	static condition_variable incv;
	static condition_variable outcv;
	static thread comOut;
	static thread comIn;
	static set<Packet*> registers;
	static set<LinkPacket*> linkers;
	static set<Packet*> data;
	static set<Packet*> ext;
	static void initialize()
	{
		/*comIn.start();
		comOut.start();*/
		TCP *tcp = new TCP();
		tcp->initialize();
		/*try
		{
			Class.forName("gnu.io.SerialPort");
			new Serial().begin(115200);
		}
		catch (ClassNotFoundException e){}
		new File();*/
	}
	static void addDevice(Device *d)
	{
		unique_lock<mutex> lck(outmtx);
		vector<Packet*> packets;
		packets.reserve(registers.size()+linkers.size()+data.size()+ext.size());
		packets.insert(packets.end(), registers.begin(), registers.end());
		packets.insert(packets.end(), linkers.begin(), linkers.end());
		packets.insert(packets.end(), data.begin(), data.end());
		packets.insert(packets.end(), ext.begin(), ext.end());
		for(Packet *p : packets)
		{
			d->write(p->getState());
		}
		devs.insert(d);
	}
	static void toSend(Packet *p)
	{
		unique_lock<mutex> lck(outmtx);
		outQueue.push_back(p);
		lck.unlock();
		outcv.notify_one();
	}
	static void send(Packet* p)
	{
		bool del = false;
		if(p->group == PacketGroup::Register) registers.insert(p);
		else if(p->type == PacketType::LinkPacket) linkers.insert((LinkPacket*)p);
		else if(p->group == PacketGroup::Data)
		{
			for(Packet *packet : data)
			{
				if(packet->type == p->type && packet->statePacket && p->statePacket)
				{
					auto pid = ((StatePacket*)p)->id;
					if(((StatePacket*)packet)->id->equals(pid.get()))
					{
						data.erase(packet);
						delete packet;
						break;
					}
				}
			}
			data.insert(p);
		}
		else if(p->group == PacketGroup::Action)
		{
			for(Packet *packet : ext)
			{
				if(packet->type == p->type && packet->statePacket && p->statePacket)
				{
					auto pid = ((StatePacket*)p)->id;
					if(((StatePacket*)packet)->id->equals(pid.get()))
					{
						ext.erase(packet);
						delete packet;
						break;
					}
				}
			}
			ext.insert(p);
		}
		else del = true;
		p->send(devs);
		if(del) delete p;
	}
	static void parse(Device *dev)
	{
		Packet *p = Packet::construct(dev);
		{
			unique_lock<mutex> lck(inmtx);
			inQueue.push_back(p);
		}
		incv.notify_one();
	}
};
