#include "Junction.h"
#include "Station.h"
#include "Signal/MainSignal.h"
void Junction::AxleActions(AxleEvent ae)
{
	if (Occupied == Direction)
	{
		blockPosition = Switch == Position::Straight ? 0 : 1;
		if (Config.lock && (Locked == -1 || lockTarget == -1))
			lock(Switch == Position::Straight ? 0 : 1);
	}
	else if (Occupied == !Direction)
	{
		if (wasFree && !ae.release && ae.dir != Direction)
		{
			if (ae.previous == FrontItems[0])
			{
				blockPosition = 0;
				updatePosition(Position::Straight);
			}
			else if (ae.previous == FrontItems[1])
			{
				blockPosition = 1;
				updatePosition(Class);
			}
			if (Config.lock && (Locked == -1 || lockTarget == -1))
				lock(Switch == Position::Straight ? 0 : 1);
		}
	}
	TrackItem::AxleActions(ae);
	if (Occupied == Orientation::None && !wasFree)
	{
		Switch = Position::Unknown;
		send(PacketType::JunctionPositionSwitch, false);
	}
	if (Occupied == Orientation::None && BlockState == Orientation::None)
		blockPosition = -1;
	tryToUnlock();
}
void Junction::updatePosition(Position p)
{
	Switch = p;
	target = p;
	OddItem = getNext(Orientation::Odd);
	EvenItem = getNext(Orientation::Even);
	updateState();
	set<SCRTListener *> list = listeners;
	for (SCRTListener *l : list)
	{
		l->actionPerformed(shared_ptr<SCRTEvent>(new OccupationEvent(this, Orientation::None, 0)));
	}
	if (!overlaps.empty())
	{
		if (BlockState == Direction || BlockState == Orientation::Both)
		{
			if (BlockState == Direction)
			{
				blockPosition = 1 - blockPosition;
				blockChanged();
			}
			int pos = Switch == Position::Straight ? 0 : 1;
			TrackItem::DirectExploration(FrontItems[1 - pos], shared_ptr<TrackComparer>(new TrackComparer({[this](TrackItem *t, Orientation dir, TrackItem *p) {
																											   if (t == nullptr)
																												   return false;
																											   bool contains = false;
																											   for (auto it = overlaps.begin(); it != overlaps.end(); ++it)
																											   {
																												   if (it->second == Direction && t->overlaps.find(it->first) != t->overlaps.end())
																												   {
																													   contains = true;
																													   t->removeOverlap(it->first);
																												   }
																											   }
																											   return contains;
																										   },
																										   [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
										 Direction);
			for (auto it = overlaps.begin(); it != overlaps.end(); ++it)
			{
				if (it->second == Direction)
					TrackItem::DirectExploration(FrontItems[pos], shared_ptr<TrackComparer>(new MainSignal::BlockOverlap(FrontItems[pos], it->first)), Direction);
			}
		}
	}
}
bool Junction::setSwitch(Position p)
{
	//if(!Station.Opened) return false;
	if (Switch == p)
		return true;
	if (Occupied != Orientation::None || Locked != -1)
		return false;
	if (BlockingData.Item != nullptr)
		return false;
	if (!overlaps.empty())
	{
		if (BlockState == Direction || BlockState == Orientation::Both)
		{
			TrackItem *next = FrontItems[p == Position::Straight ? 0 : 1];
			for (auto it = overlaps.begin(); it != overlaps.end(); ++it)
			{
				if (it->second == Direction)
				{
					TrackItem *t = it->first->getNext(Direction);
					if (t->overlaps.find(it->first) == t->overlaps.end())
						t = it->first->getNext(!Direction);
					MainSignal *lastsig = (MainSignal *)t->SignalLinked;
					if (!TrackItem::DirectExploration(next, shared_ptr<TrackComparer>(new MainSignal::OverlapAvailable(next, false, lastsig)), BlockState))
						return false;
				}
			}
		}
	}
	target = p;
	send(PacketType::JunctionPositionSwitch, true);
	if (Linked != nullptr && Linked->Switch != p && Linked->target != p)
		Linked->setSwitch(p);
	return true;
}
shared_ptr<ID> Junction::getID()
{
	JunctionID *i = new JunctionID();
	i->stationNumber = station->AssociatedNumber;
	i->Number = Number;
	i->Name = Name;
	return shared_ptr<JunctionID>(i);
}