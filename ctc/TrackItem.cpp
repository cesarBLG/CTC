#include "TrackItem.h"
#include "Station.h"
#include "Junction.h"
int TrackItem::pathDepth = 0;
TrackItem::TrackItem(TrackRegister *reg)
{
	TrackItemID *id = (TrackItemID *)reg->id.get();
	x = id->x;
	y = id->y;
	OddRotation = reg->OddRotation;
	EvenRotation = reg->EvenRotation;
	Name = reg->Name;
	station = Station::byNumber(id->stationNumber);
	send(PacketType::TrackRegister);
	updateState();
}
void TrackItem::setSignal(Signal *sig)
{
	SignalLinked = sig;
	COM::toSend(new LinkPacket(getID(), SignalLinked->getID()));
}
void TrackItem::updateOccupancy(AxleEvent ae)
{
	if (ae.release)
		axles.erase(ae.axle);
	else
		axles.insert(ae.axle);
	if (ae.release && ae.dir == Orientation::Even)
	{
		if (EvenAxles > 0)
			EvenAxles--;
		else if (OddAxles > 0)
			OddAxles--;
		//else if(getNext(ae.dir)==null || getNext(ae.dir).getReleaseCounter(ae.dir) != a) OddAxles = EvenAxles = -1;
	}
	else if (ae.release && ae.dir == Orientation::Odd)
	{
		if (OddAxles > 0)
			OddAxles--;
		else if (EvenAxles > 0)
			EvenAxles--;
		//else if(getNext(ae.dir)==null || getNext(ae.dir).getReleaseCounter(ae.dir) != a) OddAxles = EvenAxles = -1;
	}
	if (!ae.release && ae.dir == Orientation::Even)
	{
		if (EvenAxles == 0)
		{
			OccupiedTime = Clock::time();
			trainStopTimer.delay = station->AssociatedNumber == 0 ? 20000 : 10000;
			trainStopTimer.start();
		}
		EvenAxles++;
	}
	else if (!ae.release && ae.dir == Orientation::Odd)
	{
		if (OddAxles == 0)
		{
			OccupiedTime = Clock::time();
			trainStopTimer.delay = station->AssociatedNumber == 0 ? 20000 : 10000;
			trainStopTimer.start();
		}
		OddAxles++;
	}
	/*if(OddAxles == -1 && EvenAxles == -1)
		{
			OddAxles = EvenAxles = 0;
			List<AxleCounter> acs = new ArrayList<AxleCounter>(getOccupierCounter(ae.dir));
			for(AxleCounter ac : acs)
			{
				ac.Working = false;
				ac.Passed(ae.dir);
			}
			return;
		}*/
	if (EvenAxles > 0 && OddAxles > 0)
		Occupied = Orientation::Both;
	else if (EvenAxles > 0)
		Occupied = Orientation::Even;
	else if (OddAxles > 0)
		Occupied = Orientation::Odd;
	else if (Occupied != Orientation::None && Occupied != Orientation::Unknown)
		Occupied = Orientation::None;
	if (wasFree && ((Occupied == Orientation::Even && EvenItem != nullptr && !EvenItem->station->equals(station) && EvenItem->station->isOpen()) || (Occupied == Orientation::Odd && OddItem != nullptr && !OddItem->station->equals(station) && OddItem->station->isOpen())))
	{
		/*if(!trains.isEmpty())
			{
				GRP grp = (Occupied == Orientation.Even ? EvenItem : OddItem).Station.grp;
				grp.update();
			}*/
		Acknowledged = false;
	}
	if (Occupied == Orientation::None)
		Acknowledged = true;
	/*if(CrossingLinked != null)
		{
			if(Occupied == Orientation.None && CrossingLinked.Occupied == Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.None;
				CrossingLinked.updateOccupancy();
				List<SRCTListener> list = new ArrayList<SRCTListener>();
				list.addAll(CrossingLinked.listeners);
				for(SRCTListener l : list)
				{
					l.actionPerformed(new OccupationEvent(CrossingLinked, Orientation.None, 0));
				}
			}
			if(Occupied != Orientation.None && Occupied != Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.Unknown;
				CrossingLinked.updateState();
				List<SRCTListener> list = new ArrayList<SRCTListener>();
				list.addAll(CrossingLinked.listeners);
				for(SRCTListener l : list)
				{
					l.actionPerformed(new OccupationEvent(CrossingLinked, Orientation.None, 0));
				}
			}
		}*/
	updateState();
}
bool TrackItem::trainStopped()
{
	return Occupied == Orientation::None || (Clock::time() - OccupiedTime) >= (station->AssociatedNumber == 0 ? 20 : 10);
}
bool TrackItem::DirectExploration(TrackItem *start, shared_ptr<TrackComparer> tc, Orientation dir)
{
	if (tc->condition == nullptr)
		return false;
	TrackItem *t = start;
	TrackItem *p = nullptr;
	while (true)
	{
		if (!tc->condition(t, dir, p))
			break;
		if (tc->criticalCondition != nullptr && !tc->criticalCondition(t, dir, p))
			return false;
		if (t->invert && dir == Orientation::Even && p != nullptr && p->invert)
			dir = Orientation::Odd;
		p = t;
		t = t->getNext(dir);
	}
	return true;
}
void TrackItem::InverseExploration(TrackItem *start, shared_ptr<TrackComparer> tc, Orientation dir)
{
	if (tc->condition == nullptr)
		return;
	TrackItem *t = start;
	TrackItem *prev = nullptr;
	while (true)
	{
		if (!tc->condition(t, dir, prev))
			break;
		if (tc->criticalCondition != nullptr && !tc->criticalCondition(t, dir, prev))
			return;
		if (t->isJunction)
		{
			Junction *j = (Junction *)t;
			if (j->Direction == dir)
			{
				int index = j->Switch != Position::Straight;
				if (tc->condition(j->FrontItems[index], dir, prev))
					InverseExploration(j->FrontItems[index], tc, dir);
				if (tc->condition(j->FrontItems[1 - index], dir, prev))
					InverseExploration(j->FrontItems[1 - index], tc, dir);
				break;
			}
		}
		if (t->invert && dir == Orientation::Even && prev != nullptr && prev->invert)
			dir = Orientation::Odd;
		prev = t;
		t = t->getNext(dir);
		if (t == nullptr)
			continue;
		if (!(t->invert && prev->invert) && prev != t->getNext(!dir))
			break;
	}
}
shared_ptr<ID> TrackItem::getID()
{
	TrackItemID *id = new TrackItemID();
	id->x = x;
	id->y = y;
	id->stationNumber = station->AssociatedNumber;
	return shared_ptr<TrackItemID>(id);
}
void TrackItem::load(Packet *p)
{
	auto id = getID();
	if (p->type == PacketType::TrackData)
	{
		TrackData *d = (TrackData *)p;
		if (!d->id->equals(id.get()))
			return;
		if (d->BlockState == Orientation::None && BlockState == Orientation::Unknown)
			setBlock(Orientation::None);
	}
	if (p->type == PacketType::LinkPacket)
	{
		LinkPacket *link = (LinkPacket *)p;
		if (link->id1->equals(id.get()))
		{
			if (link->id2->type == ElementType::Signal)
			{
				Signal *sig = (Signal *)CTCItem::findId(link->id2.get());
				if (sig != nullptr)
					sig->setLinked(this);
			}
			if (link->id2->type == ElementType::AC)
			{
				AxleCounter *ac = (AxleCounter *)CTCItem::findId(link->id2.get());
				if (ac == nullptr)
				{
					ac = new AxleCounter(*((ACID *)link->id2.get()));
					//counters.add(ac);
				}
				if (ac->Number == 13 && ac->station->Name == "Cen")
					return;
				setCounterLinked(ac, ac->Number % 2 == 0 ? Orientation::Even : Orientation::Odd);
			}
			if (link->id2->type == ElementType::TrackItem)
			{
				TrackItem *t = (TrackItem *)CTCItem::findId(link->id2.get());
				if (EvenItem == nullptr && t->EvenItem == nullptr)
				{
					t->EvenItem = this;
					EvenItem = t;
					invert = true;
					t->invert = true;
				}
				else if (OddItem == nullptr && t->OddItem == nullptr)
				{
					t->OddItem = this;
					OddItem = t;
					invert = true;
					t->invert = true;
				}
				else if (OddItem == nullptr && t->EvenItem == nullptr)
				{
					t->EvenItem = this;
					OddItem = t;
				}
				else if (EvenItem == nullptr && t->OddItem == nullptr)
				{
					t->OddItem = this;
					EvenItem = t;
				}
			}
		}
	}
}
void TrackItem::blockChanged()
{
	if (EvenItem != nullptr && EvenItem->SignalLinked != nullptr)
	{
		EvenItem->SignalLinked->actionPerformed(shared_ptr<SCRTEvent>(new BlockEvent(this, BlockState)));
	}
	if (OddItem != nullptr && OddItem->SignalLinked != nullptr)
	{
		OddItem->SignalLinked->actionPerformed(shared_ptr<SCRTEvent>(new BlockEvent(this, BlockState)));
	}
	set<SCRTListener *> list = listeners;
	for (SCRTListener *l : list)
	{
		l->actionPerformed(shared_ptr<SCRTEvent>(new BlockEvent(this, BlockState)));
	}
	updateState();
}
void TrackItem::AxleActions(AxleEvent ae)
{
	tryToFree();
	updateState();
	shared_ptr<SCRTEvent> ev(new AxleEvent(ae));
	if (EvenItem != nullptr && EvenItem->SignalLinked != nullptr && EvenItem->SignalLinked->isMainSignal)
	{
		EvenItem->SignalLinked->actionPerformed(ev);
	}
	if (OddItem != nullptr && OddItem->SignalLinked != nullptr && OddItem->SignalLinked->isMainSignal)
	{
		OddItem->SignalLinked->actionPerformed(ev);
	}
	set<SCRTListener *> list = listeners;
	for (SCRTListener *l : list)
	{
		l->actionPerformed(shared_ptr<SCRTEvent>(new OccupationEvent(this, ae.dir, 0)));
	}
}
void TrackItem::tryToFree()
{
	if (BlockState == Orientation::None || BlockState == Orientation::Unknown)
		return;
	if (BlockingData.Item != nullptr && BlockingData.Type == BD_Signal)
		return;
	if (BlockingData.Item != nullptr && BlockingData.Type == BD_Axle && axles.find((Axle *)BlockingData.Item) == axles.end())
	{
		free = true;
		InverseExploration(this, shared_ptr<TrackComparer>(new TrackComparer({[this](TrackItem *t, Orientation dir, TrackItem *p) {
																				  if (t->axles.find((Axle *)BlockingData.Item) != t->axles.end())
																				  {
																					  free = false;
																					  return false;
																				  }
																				  if (t != nullptr && t->SignalLinked != nullptr && t->SignalLinked->isMainSignal && t->SignalLinked->Direction != dir)
																					  return false;
																				  return true;
																			  },
																			  [](TrackItem *t, Orientation dir, TrackItem *p) { return t != nullptr; }})),
						   !BlockState);
		if (!free)
			return;
	}
	BlockingData.Item = nullptr;
	shunt = false;
	set<TrackItem *> removableOverlaps;
	for (auto it = overlaps.begin(); it != overlaps.end(); ++it)
	{
		TrackItem *t = it->first;
		if (t->BlockingData.Item != nullptr || !t->trainStopped())
			continue;
		removableOverlaps.insert(t);
	}
	for (TrackItem *t : removableOverlaps)
	{
		removeOverlap(t);
	}
	setOverlapBlock();
	blockChanged();
}
void TrackItem::actionPerformed(shared_ptr<SCRTEvent> e)
{
	if (!Muted)
	{
		if (e->type == EventType::AxleCounter)
		{
			AxleEvent ae = *((AxleEvent *)e.get());
			if (ae.first)
				updateOccupancy(ae);
			else
				AxleActions(ae);
			if (SignalLinked != nullptr)
				SignalLinked->actionPerformed(e);
		}
		if (e->creator != nullptr && overlaps.find((TrackItem *)e->creator) != overlaps.end())
			tryToFree();
	}
}
void TrackItem::setCounterLinked(AxleCounter *ac, Orientation dir)
{
	ac->linked = this;
	CounterLinked = ac;
	CounterDir = dir;
	COM::toSend(new LinkPacket(getID(), ac->getID()));
}