#include "Loader.h"
#include "AxleCounter.h"
#include "Config.h"
#include "Junction.h"
#include "Signal/EoT.h"
#include "Signal/Signal.h"
#include "Station.h"
#include "TrackItem.h"
#include "packet/ConfigPacket.h"
#include "packet/JunctionRegister.h"
#include "packet/StationRegister.h"
vector<TrackItem *> Loader::items;
vector<Signal *> Loader::signals;
vector<AxleCounter *> Loader::counters;
vector<Station *> Loader::stations;
vector<Train *> Loader::trains;
CTCThread Loader::ctcThread;
int main(int argc, char **argv)
{ 
	Loader::load(argc > 1 ? argv[1] : "layout.bin");
}
void Loader::load(vector<Packet *> packets)
{
	COM::initialize();
	packets.push_back(new ConfigPacket("ADIF"));
	for (int i = 0; i < packets.size(); i++)
	{
		Packet *p = packets[i];
		if (p->type == PacketType::ConfigPacket)
		{
			Config.set((ConfigPacket *)p);
		}
		if (p->type == PacketType::StationRegister)
		{
			stations.push_back(new Station((StationRegister *)p));
		}
	}
	vector<SignalRegister *> signalregs;
	for (int i = 0; i < packets.size(); i++)
	{
		Packet *p = packets[i];
		if (p->type == PacketType::TrackRegister)
		{
			items.push_back(new TrackItem((TrackRegister *)p));
		}
		if (p->type == PacketType::JunctionRegister)
		{
			items.push_back(new Junction((JunctionRegister *)p));
		}
		if (p->type == PacketType::SignalRegister)
		{
			signals.push_back(Signal::construct((SignalRegister *)p));
		}
	}
	resolveLinks();
	for (int i = 0; i < packets.size(); i++)
	{
		Packet *p = packets[i];
		if (p->type == PacketType::LinkPacket)
			CTCItem::packetManager.handlePacket(p);
	}
	for (int i = 0; i < items.size(); i++)
	{
		TrackItem *a = items[i];
		if (a->isJunction)
			continue;
		if (a->SignalLinked != nullptr)
			a->SignalLinked->update();
		if (a->getNext(Orientation::Even) == nullptr)
		{
			if (a->CounterLinked != nullptr)
			{
				auto eot = new EoT(Orientation::Even, a->station);
				eot->setLinked(a);
				signals.push_back(a->SignalLinked);
				a->station->Signals.push_back(a->SignalLinked);
			}
			// else System.err.println("Error: Final de vía sin contador asociado en " + a.x + ", " + a.y);
		}
		if (a->getNext(Orientation::Odd) == nullptr)
		{
			if (a->CounterLinked != nullptr)
			{
				auto eot = new EoT(Orientation::Odd, a->station);
				eot->setLinked(a);
				signals.push_back(a->SignalLinked);
				a->station->Signals.push_back(a->SignalLinked);
			}
			// else System.err.println("Error: Final de vía sin contador asociado en " + a.x + ", " + a.y);
		}
	}
	ctcThread.start();
}
void Loader::resolveLinks()
{
	for (TrackItem *a : items)
	{
		for (TrackItem *b : items)
		{
			if (a->isJunction)
			{
				Junction *j = (Junction *)a;
				if (b->isJunction)
				{
					Junction *k = (Junction *)b;
					if (j->Direction != k->Direction)
					{
						if (j->x + (j->Direction == Orientation::Even ? 1 : -1) == k->x)
						{
							if (j->Class == k->Class &&
								j->y + (j->Class == Position::Left ? -1 : 1) *
											(j->Direction == Orientation::Even ? 1 : -1) ==
									k->y)
							{
								j->FrontItems[1] = k;
								k->FrontItems[1] = j;
								j->Linked = k;
								k->Linked = j;
							}
							else if (j->Class != k->Class && j->y == k->y)
							{
								j->FrontItems[0] = k;
								k->FrontItems[0] = j;
							}
							else
							{
								if (b->connectsTo(j->Direction, a))
									j->FrontItems[0] = b;
								if (b->connectsTo(j->Direction, a->x, a->y,
												  j->Class == Position::Right ? -1 : 1))
									j->FrontItems[1] = b;
								if (b->connectsTo(!j->Direction, a))
									j->BackItem = b;
							}
						}
						else
						{
							if (b->connectsTo(j->Direction, a))
								j->FrontItems[0] = b;
							if (b->connectsTo(j->Direction, a->x, a->y, j->Class == Position::Right ? -1 : 1))
								j->FrontItems[1] = b;
							if (b->connectsTo(!j->Direction, a))
								j->BackItem = b;
						}
					}
					else
					{
						if (b->connectsTo(j->Direction, a))
							j->FrontItems[0] = b;
						if (b->connectsTo(j->Direction, a->x, a->y, j->Class == Position::Right ? -1 : 1))
							j->FrontItems[1] = b;
						if (b->connectsTo(!j->Direction, a))
							j->BackItem = b;
						if (a->x == b->x && a->y + 1 == b->y &&
							((j->Direction == Orientation::Even && j->Class == Position::Right &&
							  k->Class == Position::Left) ||
							 (j->Direction == Orientation::Odd && k->Class == Position::Right &&
							  j->Class == Position::Left)))
						{
							j->CrossingLinked = k;
							k->CrossingLinked = j;
						}
					}
				}
				else
				{
					if (b->connectsTo(j->Direction, a))
						j->FrontItems[0] = b;
					if (b->connectsTo(j->Direction, a->x, a->y, j->Class == Position::Right ? -1 : 1))
						j->FrontItems[1] = b;
					if (b->connectsTo(!j->Direction, a))
						j->BackItem = b;
				}
			}
			else
			{
				if (b->connectsTo(Orientation::Even, a))
					a->EvenItem = b;
				if (b->connectsTo(Orientation::Odd, a))
					a->OddItem = b;
			}
		}
	}
}
