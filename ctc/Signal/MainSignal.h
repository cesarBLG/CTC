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
#include "Signal.h"
#include <set>
#include "TrackComparer.h"
#include "packet/ClearOrder.h"
#include "packet/AutomaticOrder.h"
#include "Config.h"
#include "CTCTimer.h"
#include "TrackItem.h"
class Junction;
class Station;
class AxleCounter;
using namespace std;
class MainSignal : public Signal
{
	public:
	MainSignal *NextSignal = nullptr;
	set<TrackItem*> MonitoringItems;
	long ForceClose = 0;
	bool Occupied = false;
	bool Override = false;
	bool Switches = false;
	bool allowsOnSight = false;
	bool stickClose = true;
	bool BlockSignal = false;
	MainSignal(Station *dep) : Signal(dep)
	{
		isMainSignal = true;
	}
	MainSignal(string s, Station *dep) : Signal(dep)
	{
		Name = s;
		if(Name[0]=='S')
		{
			Class = SignalType::Exit;
			Aspects.insert(Aspect::Parada);
			Aspects.insert(Aspect::Anuncio_parada);
			Aspects.insert(Aspect::Anuncio_precaucion);
			Aspects.insert(Aspect::Precaucion);
			Aspects.insert(Aspect::Rebase);
			Aspects.insert(Aspect::Via_libre);
		}
		else if(Name[0]=='E' && Name[1]!='\'')
		{
			Class = SignalType::Entry;
			Aspects.insert(Aspect::Rebase);
			Aspects.insert(Aspect::Parada);
			Aspects.insert(Aspect::Anuncio_parada);
			Aspects.insert(Aspect::Anuncio_precaucion);
			Aspects.insert(Aspect::Via_libre);
		}
		else if(Name[0]=='E' && Name[1]=='\'')
		{
			Class = SignalType::Advanced;
			Automatic = true;
			Aspects.insert(Aspect::Parada);
			Aspects.insert(Aspect::Anuncio_parada);
			Aspects.insert(Aspect::Anuncio_precaucion);
			Aspects.insert(Aspect::Precaucion);
			Aspects.insert(Aspect::Preanuncio);
			Aspects.insert(Aspect::Via_libre);
		}
		else if(Name[0]=='M')
		{
			Class = SignalType::Shunting;
			Aspects.insert(Aspect::Parada);
			Aspects.insert(Aspect::Rebase);
		}
		else
		{
			Class = SignalType::Block;
			Automatic = true;
			Aspects.insert(Aspect::Parada);
			Aspects.insert(Aspect::Anuncio_parada);
			Aspects.insert(Aspect::Precaucion);
			Aspects.insert(Aspect::Via_libre);
		}
		if(Config.sigsAhead<2) Aspects.erase(Aspect::Anuncio_parada);
		if(Config.anuncioPrecaucion == 0) Aspects.erase(Aspect::Anuncio_precaucion);
		int start1 = -1;
		int end1 = 0;
		int start2 = 0;
		for(int i=0; i<Name.size();i++)
		{
			if(Name[i]<='9'&&Name[i]>='0'&&start1==-1) start1 = i;
			if(Name[i]=='/') end1 = i;
			if(Name[i]<='9'&&Name[i]>='0'&&end1!=0&&start2==0) start2 = i;
		}
		if(end1==0) end1 = Name.size();
		Number = stoi(Name.substr(start1, end1-start1));
		if(start2!=0) Track = stoi(Name.substr(start2));
		else Track = 0;
		allowsOnSight = Config.allowOnSight && Class != SignalType::Entry;
		Direction = Number%2 == 0 ? Orientation::Even : Orientation::Odd;
		isMainSignal = true;
		start();
	}
	virtual ~MainSignal()
	{
		if(ClosingTimer!=nullptr) delete ClosingTimer;
	}
	virtual void setState();
	void start()
	{
		Signal::setAspect();
		setState();
	}
	bool UserRequest = false;
	void SetUserRequest(bool Clear)
	{
		UserRequest = Clear;
		if(Automatic && !UserRequest) ForceClose = Clock::time();
		else ForceClose = 0;
		update();
		/*if(!Cleared&&UserRequest)
		{
			CTCTimer t = new CTCTimer(30000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setCleared();
					if(!Cleared && !Automatic) UserRequest = OverrideRequest = false;
					setAspect();
				}
			});
			t.setRepeats(false);
			t.start();
		}*/
	}
	conditionfun nextSignal = [this](TrackItem *i, Orientation dir, TrackItem *p) {return i!=Linked && i!=nullptr && i->SignalLinked != nullptr && i->SignalLinked->Direction == dir && i->SignalLinked->isMainSignal;};
	conditionfun ovEnd = [this](TrackItem *i, Orientation dir, TrackItem *p)
	{
		if(i==Linked||i==nullptr||i->SignalLinked==nullptr||!(i->SignalLinked->isMainSignal)||i->SignalLinked->Direction!=dir)
		{
			if(i->station != Linked->station) return false;
			//if(i.Occupied != Orientation::None && i.Occupied != Orientation::Unknown &&(i.Occupied==dir||i.trainStopped())) return false;
			return true;
		}
		return false;
	};
	static conditionfun OverlapEnd(AxleCounter *ac)
	{
		return [ac](TrackItem *i, Orientation dir, TrackItem *p)
			{
				if(p!=nullptr && (p->CounterLinked!=nullptr && p->CounterLinked != ac)) return false;
				if(i==nullptr || (i->CounterLinked!=nullptr && i->CounterLinked != ac && i->CounterDir != dir)) return false;
				return true;
			};
	}
	class TrackAvailable : public TrackComparer
	{
		public:
		int checkBlock = 0;
		bool Next = false;
		conditionfun end;
		bool Override;
		MainSignal *lastSignal;
		TrackAvailable(bool Override, int CheckBlock, MainSignal *sig);
	};
	class OverlapAvailable : public TrackComparer
	{
		conditionfun end;
		public:
		OverlapAvailable(TrackItem *start, bool checkBlock, MainSignal *lastSignal);
	};
	class LockTrack : public TrackComparer
	{
		conditionfun end;
		public:
		LockTrack(bool Override, bool* allLocked, MainSignal *sig);
	};
	class BlockTrack : public TrackComparer
	{
		conditionfun end;
		bool Override;
		public:
		BlockTrack(bool Overrid, MainSignal *sig);
	};
	class BlockOverlap : public TrackComparer
	{
		TrackItem *overlap;
		conditionfun end;
		public:
		BlockOverlap(TrackItem *start, TrackItem *overlap) : overlap(overlap)
		{
			end = MainSignal::OverlapEnd(start->CounterLinked);
			condition = [this, overlap](TrackItem *i, Orientation dir, TrackItem *p)
			{
				if(!end(i,dir,p)) return false;
				i->setOverlap(overlap, dir);
				return true;
			};
			criticalCondition = [](TrackItem *i, Orientation dir, TrackItem *p) {return true;};
		}
	};
	void setCleared();
	bool MT = false;
	void setAspect() override;
	bool Locked = false;
	virtual void Lock();
	virtual void Unlock();
	virtual void setClearRequest()
	{
		ClearRequest = UserRequest || !stickClose;
		if(ForceClose==0 || ForceClose+30<Clock::time())
		{
			ForceClose = 0;
			ClearRequest |= ((Automatic||BlockSignal) && TrackRequest()) || (BlockSignal && BlockRequest());
		}
		if(Class == SignalType::Shunting) OverrideRequest = true;
		else if(!UserRequest) OverrideRequest = false;
	}
	bool BlockRequest()
	{
		TrackItem *i = Linked->getNext(!Direction);
		return i->BlockState == Direction && i->BlockingData.Item != nullptr;
	}
	void update()
	{
		if(Linked==nullptr) return;
		setMonitors();
		setClearRequest();
		bool avail = TrackItem::DirectExploration(Linked,  shared_ptr<TrackComparer>(new TrackAvailable(OverrideRequest, 1, this)), Direction);
		bool check = TrackItem::DirectExploration(Linked,  shared_ptr<TrackComparer>(new TrackAvailable(OverrideRequest, 2, this)), Direction);
		if(!avail || !ClearRequest) tryClose();
		else
		{
			if(ClosingTimer!=nullptr) ClosingTimer->stop();
			if(!check) Locked = false;
			Lock();
		}
		/*if(ClosingTimer!=null && ClosingTimer.isRunning())
		{
			if(Cleared) tryClose();
		}*/
		setAspect();
		if(Config.sigsAhead == 2 && NextSignal!=nullptr && !NextSignal->Locked && NextSignal->Automatic) NextSignal->update();
	}
	bool trainInProximity()
	{
		return (Direction == Orientation::Odd ? Linked->getNext(Orientation::Even)->OddAxles : Linked->getNext(Orientation::Odd)->EvenAxles)!=0;
	}
	virtual bool TrackRequest()
	{
		if(trainInProximity()) return true;
		if(Config.sigsAhead >= 2)
		{
			for(MainSignal *s : getPreviousSignals())
			{
				if(s->maxSigsAhead()>=2 && s->SigsAhead()>=1)
				{
					if(s->trainInProximity()) return true;
					for(MainSignal *s1 : s->getPreviousSignals())
					{
						if(s1->maxSigsAhead()>= 3 && s1->SigsAhead()>=2)
						{
							if(s1->trainInProximity()) return true;
						}
					}
				}
			}
		}
		return false;
	}
	bool proximity = false;
	bool affects = false;
	class CloseTrier : public TrackComparer
	{
		int signalsPassed = 0;
		bool cond=false;
		public:
		CloseTrier(MainSignal *sig)
		{
			condition = [this, sig](TrackItem *t, Orientation dir, TrackItem *p)
			{
				if(t == sig->Linked) return true;
				if(t == nullptr) return false;
				if(t->Occupied==!dir||t->Occupied == Orientation::Both)
				{
					cond = true;
				} 
				if(t->SignalLinked!=nullptr && t->SignalLinked->isMainSignal && t->SignalLinked->Direction == !dir)
				{
					MainSignal *s = (MainSignal*) t->SignalLinked;
					signalsPassed++;
					if(s->SigsAhead()<=signalsPassed) return false;
					if(cond || s->trainInProximity()) sig->proximity = true;
					cond = false;
					sig->affects = true;
				}
				return true;
			};
			criticalCondition = [](TrackItem *t, Orientation dir, TrackItem *p) {return true;};
		}
	};
	void tryClose()
	{
		if(!Locked || (ClosingTimer!=nullptr && ClosingTimer->running)) return;
		/*if(SigsAhead()==0)
		{
			Unlock();
			setAspect();
			return;
		}*/
		proximity = affects = false;
		if(trainInProximity()) proximity = true;
		TrackItem::InverseExploration(Linked, shared_ptr<TrackComparer>(new CloseTrier(this)), !Direction);
		if(proximity)
		{
			if(affects || Class == SignalType::Entry)
			{
				setClosingTimer(Config.D1);
			}
			else setClosingTimer(Config.D0);
		}
		else
		{
			Unlock();
			setAspect();
		}
	}
	CTCTimer *ClosingTimer = nullptr;
	void setClosingTimer(int time)
	{
		if(ClosingTimer==nullptr)
		{
			ClosingTimer = new CTCTimer(time, [this]() {
				Unlock();
				ClosingTimer->stop();
			});
		}
		if(ClosingTimer->running) return;
		ClosingTimer->delay = time;
		ClosingTimer->start();
	}
	set<MainSignal*> PreviousSignals;
	set<MainSignal*> getPreviousSignals()
	{
		PreviousSignals.clear();
		TrackItem::InverseExploration(Linked, shared_ptr<TrackComparer>(new TrackComparer(
				{
					[this](TrackItem *t, Orientation dir, TrackItem *p)
					{
						if(t == Linked) return true;
						if(t == nullptr) return false;
						if(t->SignalLinked!=nullptr && t->SignalLinked->isMainSignal && t->SignalLinked->Direction == !dir)
						{
							PreviousSignals.insert((MainSignal*) t->SignalLinked);
							return false;
						}
						return true;
					},
					[](TrackItem *t, Orientation dir, TrackItem *p) {return true;}
				})), !Direction);
		return PreviousSignals;
	}
	virtual void setAutomatic(bool v)
	{
		if(Automatic==v) return;
		if(v)
		{
			Automatic = true;
			setMonitors();
			setState();
			update();
		}
		else
		{
			Automatic = false;
			UserRequest = ClearRequest;
		}
		setState();
		update();
	}
	bool protects()
	{
		return (!BlockSignal || Aspects.find(Aspect::Parada)!=Aspects.end()) && SignalAspect != Aspect::Apagado;
	}
	virtual int maxSigsAhead()
	{
		int num = 1;
		if(Class!=SignalType::Shunting && NextSignal!=nullptr && Config.sigsAhead >= 2)
		{
			num++;
			if(NextSignal->Class == SignalType::Entry) num++;
		}
		return num;
	}
	virtual int SigsAhead()
	{
		if(SignalAspect == Aspect::Rebase) return 1;
		if(SignalAspect == Aspect::Parada) return 0;
		if(Config.sigsAhead > 1 && NextSignal!=nullptr && SignalAspect!=Aspect::Anuncio_parada && SignalAspect!=Aspect::Precaucion)
		{
			if(NextSignal->SignalAspect == Aspect::Via_libre && NextSignal->Class == SignalType::Entry && SignalAspect == Aspect::Via_libre) return 3;
			return 2;
		}
		else return 1;
	}
	void setMonitors()
	{
		for(TrackItem *t : MonitoringItems)
		{
			t->listeners.erase(this);
		}
		MonitoringItems.clear();
		TrackItem *last = nullptr;
		bool first = true;
		TrackItem::DirectExploration(Linked, shared_ptr<TrackComparer>(new TrackComparer(
				{
					[this, &last ,&first](TrackItem *t, Orientation dir, TrackItem *p) 
					{
						if(t==nullptr) return false;
						if(nextSignal(t, dir, p))
						{
							if(first) last = t;
							first = false;
							if(!((MainSignal*)t->SignalLinked)->BlockSignal) return false;
							last = nullptr;

						}
						MonitoringItems.insert(t);
						return true;
					},
					[](TrackItem *t, Orientation dir, TrackItem *p) {return true;}
				})), Direction);
		
		if(Config.overlap && Class == SignalType::Entry)
		{
			if(last!=nullptr)
			{
				AxleCounter *ac = last->CounterLinked;
				conditionfun ov = OverlapEnd(ac);
				TrackItem::DirectExploration(last, shared_ptr<TrackComparer>(new TrackComparer(
						{
							[this, ac, ov](TrackItem *t, Orientation dir, TrackItem *p) 
							{
								if(!ov(t,dir,p)) return false;
								MonitoringItems.insert(t);
								return true;
							},
							[](TrackItem *t, Orientation dir, TrackItem *p) {return true;} 
						})), Direction);	
			}	
		}
		for(TrackItem *t : MonitoringItems)
		{
			if(t!=nullptr) t->listeners.insert(this);
		}
	}
	long lastPass = 0;
	void actionPerformed(shared_ptr<SCRTEvent> e) override;
	void muteEvents(bool mute) {
		EventsMuted = mute;
		if(!EventsMuted)
		{
			deque<shared_ptr<SCRTEvent>> l = Queue;
			for(shared_ptr<SCRTEvent> e : l)
			{
				if(EventsMuted) return;
				else actionPerformed(e);
			}
		}
	}
	void load(Packet *p)
	{
		auto id = getID();
		if(p->type == PacketType::ClearOrder)
		{
			ClearOrder *d = (ClearOrder*)p;
			if(!d->id->equals(id.get())) return;
			OverrideRequest = d->ov;
			UserRequest = !d->clear;
			MT = d->mt;
			SetUserRequest(d->clear);
		}
		if(p->type == PacketType::AutomaticOrder)
		{
			AutomaticOrder *d = (AutomaticOrder*)p;
			if(!d->id->equals(id.get())) return;
			stickClose = d->stickClose;
			setAutomatic(d->automatic);
		}
	}
};
