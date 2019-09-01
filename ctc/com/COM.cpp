#include "COM.h"
#include "Loader.h"
#include "CTCItem.h"
set<Device*> COM::devs;
set<Packet*> COM::registers;
set<LinkPacket*> COM::linkers;
set<Packet*> COM::data;
set<Packet*> COM::ext;
deque<Packet*> COM::inQueue;
deque<Packet*> COM::outQueue;
mutex COM::inmtx;
mutex COM::outmtx;
condition_variable COM::incv;
condition_variable COM::outcv;
thread COM::comOut([]()
{
	while(true)
	{
		unique_lock<mutex> lck(outmtx);
		if(!outQueue.empty())
		{
			send(outQueue.front());
			outQueue.pop_front();
		}
		else outcv.wait(lck);
	}
});
thread COM::comIn([]()
{
    while(true)
	{
		unique_lock<mutex> lck(inmtx);
		if(!inQueue.empty())
		{
			Packet *p = inQueue.front();
			inQueue.pop_front();
			unique_lock<mutex> ctclck(Loader::ctcThread.mtx);
			Loader::ctcThread.tasks.push_back([p] {
				/*if(p->Type == PacketType::ItineraryStablisher) Itinerary::handlePacket(p);
				else */CTCItem::packetManager.handlePacket(p);
			});
			ctclck.unlock();
			Loader::ctcThread.cv.notify_one();
		}
		else incv.wait(lck);
	}
});