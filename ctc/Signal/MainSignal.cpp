#include "MainSignal.h"
#include "Junction.h"
#include "TrackItem.h"
void MainSignal::setState()
{
	bool prev = BlockSignal;
	BlockSignal = !station->Opened || (Class == SignalType::Block /*||Class==SignalType::Advanced*/);
	if (prev == BlockSignal)
		return;
	update();
}
MainSignal::TrackAvailable::TrackAvailable(bool Override, int CheckBlock, MainSignal *sig)
	: checkBlock(CheckBlock), Override(Override), lastSignal(sig)
{
	if (!Override)
		end = [sig](TrackItem *i, Orientation dir, TrackItem *p) {
			return !sig->nextSignal(i, dir, p) || ((MainSignal *)i->SignalLinked)->BlockSignal;
		};
	else
		end = sig->ovEnd;
	condition = [this, sig](TrackItem *i, Orientation dir, TrackItem *p) {
		if (i == nullptr)
			return true;
		if (sig->nextSignal(i, dir, p))
		{
			if (!Next)
			{
				Next = true;
				sig->NextSignal = (MainSignal *)i->SignalLinked;
				sig->NextSignal->listeners.insert(sig);
				checkBlock = 0;
			}
			lastSignal = (MainSignal *)i->SignalLinked;
		}
		if (end(i, dir, p))
		{
			if (!Next)
			{
				if (i->Occupied != Orientation::None)
					sig->Occupied = true;
				if (i->Occupied == !dir && criticalCondition(i, dir, p))
					return false;
				if (i->isJunction)
				{
					Junction *j = (Junction *)i;
					if (j->Switch != Position::Straight || j->blockPosition == 1)
						sig->Switches = true;
				}
			}
			return true;
		}
		return false;
	};
	criticalCondition = [sig, this](TrackItem *i, Orientation dir, TrackItem *p) {
		if (i == nullptr ||
			(i->BlockState != dir && (i->BlockState != Orientation::None || checkBlock == 2)) ||
			(i->Occupied != Orientation::None &&
			 ((!sig->OverrideRequest && !Next && !sig->allowsOnSight && checkBlock != 0) ||
			  ((i->Occupied == !dir || i->Occupied == Orientation::Both) /* && !i->trainStopped()*/))))
		{
			return false;
		}
		if (i->isJunction)
		{
			Junction *j = (Junction *)i;
			if (p != nullptr && !j->blockedFor(p, checkBlock == 2))
				return false;
			if (Config.lock && p != nullptr && !j->lockedFor(p, checkBlock == 2))
				return false;
			if (j->CrossingLinked != nullptr)
			{
				Junction *k = j->CrossingLinked;
				if (k->Locked == 1 || (k->Switch != Position::Straight && k->BlockState != Orientation::None))
					return false;
			}
		}
		if (!this->Override && Config.overlap && sig->Class == SignalType::Entry)
		{
			p = i;
			i = i->getNext(dir);
			if (sig->nextSignal(i, dir, p) && i->SignalLinked == sig->NextSignal)
			{
				return TrackItem::DirectExploration(
					i, shared_ptr<TrackComparer>(new OverlapAvailable(i, checkBlock == 2, sig->NextSignal)),
					dir);
			}
		}
		return true;
	};
}
MainSignal::OverlapAvailable::OverlapAvailable(TrackItem *start, bool checkBlock, MainSignal *lastSignal)
	: end(MainSignal::OverlapEnd(start->CounterLinked))
{
	condition = end;
	criticalCondition = [lastSignal, checkBlock, start](TrackItem *i, Orientation dir, TrackItem *p) {
		if (i->BlockingData.Item != nullptr && i->BlockState != dir &&
			(i->BlockState != Orientation::None || checkBlock))
			return false;
		if (i->BlockingData.Item == nullptr && checkBlock && i->BlockState != dir &&
			i->BlockState != Orientation::Both)
			return false;
		if (i->Occupied != Orientation::None && i->Occupied != Orientation::Unknown &&
			(i->Occupied != dir || !lastSignal->allowsOnSight))
			return false;
		if (i->isJunction && (i->BlockingData.Item != nullptr || i->Occupied != Orientation::None) &&
			!((Junction *)i)->blockedFor(p, checkBlock))
			return false;
		return true;
	};
}
MainSignal::LockTrack::LockTrack(bool Override, bool *allLocked, MainSignal *sig)
{
	if (!Override)
		end = [sig](TrackItem *i, Orientation dir, TrackItem *p) { return !sig->nextSignal(i, dir, p); };
	else
		end = sig->ovEnd;
	*allLocked = true;
	condition = [this, sig, allLocked](TrackItem *i, Orientation dir, TrackItem *p) {
		if (end(i, dir, p))
		{
			if (i->isJunction)
			{
				Junction *j = (Junction *)i;
				j->lock(p, sig);
				if (j->Locked == -1)
					*allLocked = false;
			}
			return true;
		}
		return false;
	};
	criticalCondition = [](TrackItem *i, Orientation dir, TrackItem *p) { return true; };
}
MainSignal::BlockTrack::BlockTrack(bool Overrid, MainSignal *sig) : Override(Overrid)
{
	if (!Override)
		end = [sig](TrackItem *i, Orientation dir, TrackItem *p) { return !sig->nextSignal(i, dir, p); };
	else
		end = sig->ovEnd;
	condition = [this, sig](TrackItem *i, Orientation dir, TrackItem *p) {
		if (end(i, dir, p))
		{
			if (i->Occupied != Orientation::None && i->Occupied != Orientation::Unknown &&
				i->Occupied != dir/* && !i->trainStopped()*/)
				return false;
			if (i->isJunction)
			{
				Junction *j = (Junction *)i;
				j->block(p);
			}
			i->setBlock(dir, sig, Override);
			return true;
		}
		if (i != nullptr && !Override && Config.overlap && sig->Class == SignalType::Entry)
		{
			TrackItem::DirectExploration(i, shared_ptr<TrackComparer>(new BlockOverlap(i, p)), dir);
		}
		return false;
	};
	criticalCondition = [](TrackItem *i, Orientation dir, TrackItem *p) { return true; };
}
void MainSignal::setCleared()
{
	Cleared = Override = Switches = Occupied = false;
	bool openSignals =
		Config.openSignals && (Class == SignalType::Advanced /* || Class == SignalType::Block*/);
	for (MainSignal *s : getPreviousSignals())
	{
		if (s->SigsAhead() >= 2)
		{
			if (s->trainInProximity())
				openSignals = false;
			for (MainSignal *s1 : s->getPreviousSignals())
			{
				if (s1->SigsAhead() >= 3)
				{
					if (s1->trainInProximity())
						openSignals = false;
				}
			}
		}
	}
	if (TrackItem::DirectExploration(
			Linked, shared_ptr<TrackComparer>(new TrackAvailable(OverrideRequest, openSignals ? 1 : 2, this)),
			Direction))
	{
		Override = OverrideRequest;
		Cleared = true;
	}
}
void MainSignal::setAspect()
{
	setCleared();
	if (!Cleared)
		MT = false;
	else if (Class == SignalType::Exit && Automatic)
		MT = true;
	if (!Cleared || (ClosingTimer != nullptr && ClosingTimer->running) || (!UserRequest && ForceClose != 0))
	{
		SignalAspect = Aspect::Parada;
	}
	else if (Override)
	{
		SignalAspect = Aspect::Rebase;
	}
	else if (Occupied)
	{
		SignalAspect = Aspect::Precaucion;
	}
	else
	{
		SignalAspect = Aspect::Via_libre;
		if (Config.sigsAhead > 1 && NextSignal->SignalAspect == Aspect::Anuncio_parada &&
			NextSignal->Class == SignalType::Entry)
			SignalAspect = Aspect::Preanuncio;
		if ((Switches && Config.anuncioPrecaucion == 1) ||
			(NextSignal != nullptr && NextSignal->Switches && Config.anuncioPrecaucion == 2))
		{
			SignalAspect = Aspect::Anuncio_precaucion;
		}
		if (Config.sigsAhead > 1)
		{
			if (Switches && Config.anuncioPrecaucion == 2)
			{
				/*if(!Linked.overlaps.isEmpty()) */ SignalAspect = Aspect::Anuncio_parada;
			}
			if (NextSignal == nullptr ||
				(NextSignal->SignalAspect == Aspect::Parada &&
				 (!NextSignal->isEoT || NextSignal->station->AssociatedNumber != 0)) ||
				NextSignal->SignalAspect == Aspect::Rebase || NextSignal->SignalAspect == Aspect::Apagado)
			{
				SignalAspect = Aspect::Anuncio_parada;
			}
		}
	}
	while (Aspects.find(SignalAspect) == Aspects.end() && SignalAspect != Aspect::Apagado)
	{
		if (SignalAspect == Aspect::Via_libre)
		{
			while (Aspects.find(SignalAspect) == Aspects.end() && SignalAspect != Aspect::Apagado)
			{
				switch (SignalAspect)
				{
				case Parada:
					SignalAspect = Aspect::Apagado;
					break;
				case Rebase:
					SignalAspect = Aspect::Parada;
					break;
				case Precaucion:
					SignalAspect = Aspect::Rebase;
					break;
				case Anuncio_parada:
					SignalAspect = Aspect::Precaucion;
					break;
				default:
					SignalAspect = Aspect::Anuncio_parada;
					break;
				}
			}
			break;
		}
		else
		{
			switch (SignalAspect)
			{
			case Parada:
				SignalAspect = Aspect::Apagado;
				break;
			case Rebase:
				SignalAspect = Aspect::Precaucion;
				break;
			case Anuncio_parada:
				SignalAspect =
					Config.sigsAhead < 2
						? ((Config.anuncioPrecaucion == 1 && Switches) ? Aspect::Anuncio_precaucion
																	   : Aspect::Via_libre)
						: (Aspects.find(Aspect::Parada) != Aspects.end() ? Aspect::Parada : Aspect::Apagado);
				break;
			case Preanuncio:
				SignalAspect = Aspect::Anuncio_precaucion;
				break;
			default:
				SignalAspect = Aspect::Via_libre;
				break;
			}
		}
	}
	Signal::setAspect();
}
void MainSignal::Lock()
{
	if (Locked || Linked == nullptr)
		return;
	Locked = true;
	if (!Config.trailablePoints)
	{
		if (!TrackItem::DirectExploration(Linked,
										  shared_ptr<TrackComparer>(new TrackComparer(
											  {[this](TrackItem *t, Orientation dir, TrackItem *p) {
												   if (Override)
													   return ovEnd(t, dir, p);
												   else
													   return !nextSignal(t, dir, p);
											   },
											   [this](TrackItem *t, Orientation dir, TrackItem *p) {
												   if (t->isJunction)
												   {
													   Junction *j = (Junction *)t;
													   if (j->canGetFrom(p))
														   return true;
													   else
													   {
														   j->userChangeSwitch();
														   return false;
													   }
												   }
												   return true;
											   }})),
										  Direction))
			return;
	}
	muteEvents(true);
	if (Config.lock)
	{
		bool allLocked;
		TrackItem::DirectExploration(
			Linked, shared_ptr<TrackComparer>(new LockTrack(OverrideRequest, &allLocked, this)), Direction);
		if (Config.lockBeforeBlock && !allLocked)
		{
			muteEvents(false);
			return;
		}
	}
	TrackItem::DirectExploration(Linked, shared_ptr<TrackComparer>(new BlockTrack(OverrideRequest, this)),
								 Direction);
	muteEvents(false);
}
void MainSignal::Unlock()
{
	if (!Locked)
		return;
	Locked = false;
	muteEvents(true);
	TrackItem::DirectExploration(Linked,
								 shared_ptr<TrackComparer>(new TrackComparer(
									 {[this](TrackItem *i, Orientation dir, TrackItem *p) {
										  if (i == nullptr)
											  return false;
										  if (!nextSignal(i, dir, p))
										  {
											  if (OverrideRequest && Class == SignalType::Exit &&
												  i->station != Linked->station)
												  return false;
											  if (i->BlockingData.Item == this)
												  i->BlockingData.Item = nullptr;
											  if (i->isJunction)
											  {
												  Junction *j = (Junction *)i;
												  if (j->lockingItem.Item == this)
													  j->lockingItem.Item = nullptr;
											  }
											  i->tryToFree();
											  return true;
										  }
										  return false;
									  },
									  [](TrackItem *t, Orientation dir, TrackItem *p) { return true; }

									 })),
								 Direction);
	muteEvents(false);
	if (NextSignal != nullptr)
		NextSignal->listeners.erase(this);
}
void MainSignal::actionPerformed(shared_ptr<SCRTEvent> e)
{
	if (!EventsMuted)
	{
		if (e->type == EventType::Signal)
		{
			setAspect();
		}
		if (e->type == EventType::Block)
		{
			update();
		}
		if (e->type == EventType::Occupation)
		{
			update();
		}
		if (e->type == EventType::AxleCounter)
		{
			AxleEvent *ae = (AxleEvent *)e.get();
			if (e->creator == Linked->CounterLinked && Linked->CounterLinked->Working)
			{
				if (!ae->release && Direction == ae->dir)
				{
					if (!ae->first)
					{
						if (SignalAspect == Aspect::Parada && Clock::time() > lastPass + 10)
						{
							// Logger::trace(this, "rebasada");
							TrackItem::DirectExploration(
								Linked,
								shared_ptr<TrackComparer>(new TrackComparer(
									{[this](TrackItem *t, Orientation dir, TrackItem *p) {
										 // ToDo: Needs revision
										 if (!nextSignal(t, dir, p))
										 {
											 t->setBlock(Orientation::Unknown, this, false);
											 if (t->isJunction)
											 {
												 ((Junction *)t)->lock(p, this);
											 }
										 }
										 else
											 return false;
										 return true;
									 },
									 [](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
								ae->dir);
						}
					}
					else
					{
						if (SignalAspect != Aspect::Parada)
							lastPass = Clock::time();
						TrackItem::DirectExploration(
							Linked,
							shared_ptr<TrackComparer>(new TrackComparer(
								{[this, ae](TrackItem *t, Orientation dir, TrackItem *p) {
									 if (t == nullptr)
										 return false;
									 if (nextSignal(t, dir, p))
										 return false;
									 t->BlockingData.Item = ae->axle;
									 t->BlockingData.Type = TrackItem::BD_Axle;
									 if (t->isJunction)
									 {
										 ((Junction *)t)->lockingItem.Item = ae->axle;
										 ((Junction *)t)->lockingItem.Type = TrackItem::BD_Axle;
									 }
									 return true;
								 },
								 [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
							Direction);
						if (UserRequest &&
							(!OverrideRequest || Linked->getNext(!Direction)->Occupied == Orientation::None))
						{
							OverrideRequest = UserRequest = false;
						}
					}
				}
				else if (!ae->first)
				{
					/*CTCTimer t = new CTCTimer(1000, new ActionListener() {
					   @Override public void actionPerformed(ActionEvent e) {*/
					TrackItem::DirectExploration(
						Linked,
						shared_ptr<TrackComparer>(new TrackComparer(
							{[this](TrackItem *t, Orientation dir, TrackItem *p) {
								 if (t == nullptr)
									 return false;
								 if (nextSignal(t, dir, p))
									 return false;
								 t->tryToFree();
								 return true;
							 },
							 [this](TrackItem *t, Orientation dir, TrackItem *p) { return true; }})),
						Direction);
					/*} });
						t.setRepeats(false);
						t.start();*/
				}
			}
			else if (!ae->first)
				update();
		}
		for (auto it = Queue.begin(); it != Queue.end(); ++it)
		{
			if (*it == e)
			{
				Queue.erase(it);
				break;
			}
		}
	}
}