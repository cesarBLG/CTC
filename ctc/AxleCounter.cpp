#include "AxleCounter.h"
#include "event/AxleEvent.h"
AxleCounter::AxleCounter(ACID acid)
{
	Number = acid.Num;
	station = Station::byNumber(acid.stationNumber);
}
void AxleCounter::Passed(Orientation direction)
{
	map<AxleCounter *, Orientation> counters;
	TrackItem::InverseExploration(linked, shared_ptr<TrackComparer>(new TrackComparer({[this, &counters](TrackItem *t, Orientation dir, TrackItem *p) {
																						   if (t == nullptr)
																							   return false;
																						   if (t->CounterLinked != this && t->CounterLinked != nullptr)
																						   {
																							   counters[t->CounterLinked] = !dir;
																							   return false;
																						   }
																						   return true;
																					   },
																					   [](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
								  !direction);
	start = nullptr;
	axle = nullptr;
	for (auto it = counters.begin(); it != counters.end(); ++it)
	{
		AxleCounter *ac = it->first;
		Orientation counterDir = it->second;
		if (ac->getAxles(counterDir).size() != 0)
		{
			start = ac->linked;
			startDir = counterDir;
			axle = ac->getAxles(counterDir).front();
			ac->getAxles(counterDir).pop_front();
			break;
		}
	}
	if (axle == nullptr)
	{
		if (getAxles(!direction).empty())
		{
			axle = new Axle();
			startDir = direction;
			if (getAxles(direction).empty())
			{
				//Wagon *w = new Locomotive();
				Wagon *w = new Wagon();
				w->addAxle(axle);
				Train *t = new Train("Tren en pruebas");
				//Loader.trains.add(t);
				t->addWagon(w);
			}
			else
			{
				Wagon *w = new Wagon();
				w->addAxle(axle);
				Train *t = getAxles(direction).back()->wagon->train;
				t->addWagon(w);
			}
		}
		else
		{
			axle = getAxles(!direction).front();
			getAxles(!direction).pop_front();
		}
	}
	else
	{
		auto &v = getAxles(!direction);
		for (int i = 0; i < v.size(); i++)
		{
			if (v[i] == axle)
			{
				v.erase(v.begin() + i);
				break;
			}
		}
	}
	getAxles(direction).push_back(axle);
	axle->lastPosition = linked;
	if (start != nullptr)
	{
		TrackItem::DirectExploration(start, shared_ptr<TrackComparer>(new TrackComparer({[this](TrackItem *t, Orientation dir, TrackItem *p) {
																							 if (t == start && t->CounterDir == dir)
																								 return true;
																							 if (t == linked && t->CounterDir != dir)
																								 return false;
																							 if (p == linked)
																								 return false;
																							 t->actionPerformed(shared_ptr<SCRTEvent>(new AxleEvent(dir, true, true, p, axle, this)));
																							 return true;
																						 },
																						 [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
									 startDir);
	}
	TrackItem::DirectExploration(linked, shared_ptr<TrackComparer>(new TrackComparer({[this, direction](TrackItem *t, Orientation dir, TrackItem *p) {
																						  if (t == linked && t->CounterDir == dir)
																							  return true;
																						  if (p != nullptr && p->CounterLinked != nullptr && p->CounterLinked != this)
																						  {
																							  p->CounterLinked->getAxles(!dir).push_back(axle);
																							  axle->firstPosition = p;
																							  axle->orientation = dir;
																							  return false;
																						  }
																						  if (t == nullptr)
																						  {
																							  //TODO: Remove axle from train
																							  getAxles(direction).pop_front();
																							  return false;
																						  }
																						  if (t->CounterLinked != nullptr && t->CounterLinked != this && t->CounterDir != dir)
																						  {
																							  t->CounterLinked->getAxles(!dir).push_back(axle);
																							  axle->firstPosition = t;
																							  axle->orientation = dir;
																							  return false;
																						  }
																						  t->actionPerformed(shared_ptr<SCRTEvent>(new AxleEvent(dir, false, true, p, axle, this)));
																						  return true;
																					  },
																					  [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
								 direction);
	axle->update();
	if (start != nullptr)
	{
		TrackItem::DirectExploration(start, shared_ptr<TrackComparer>(new TrackComparer({[this](TrackItem *t, Orientation dir, TrackItem *p) {
																							 if (t == start && t->CounterDir == dir)
																								 return true;
																							 if (t == linked && t->CounterDir != dir)
																								 return false;
																							 if (p == linked)
																								 return false;
																							 t->actionPerformed(shared_ptr<SCRTEvent>(new AxleEvent(dir, true, false, p, axle, this)));
																							 return true;
																						 },
																						 [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
									 startDir);
	}
	TrackItem::DirectExploration(linked, shared_ptr<TrackComparer>(new TrackComparer({[this](TrackItem *t, Orientation dir, TrackItem *p) {
																						  if (t == linked && t->CounterDir == dir)
																							  return true;
																						  if (p != nullptr && p->CounterLinked != nullptr && p->CounterLinked != this)
																						  {
																							  return false;
																						  }
																						  if (t == nullptr)
																							  return false;
																						  if (t->CounterLinked != nullptr && t->CounterLinked != this && t->CounterDir != dir)
																						  {
																							  return false;
																						  }
																						  t->actionPerformed(shared_ptr<SCRTEvent>(new AxleEvent(dir, false, false, p, axle, this)));
																						  return true;
																					  },
																					  [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
								 direction);
}
shared_ptr<ID> AxleCounter::getID()
{
	ACID *acid = new ACID();
	acid->stationNumber = station->AssociatedNumber;
	acid->Num = Number;
	acid->dir = (Number % 2 == 0) ? Orientation::Even : Orientation::Odd;
	return shared_ptr<ACID>(acid);
}