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
#include "Wagon.h"
#include <vector>
#include "Orientation.h"
class Train 
{
	public:
	enum struct TrainClass
	{
		Empty,
		Passengers,
		Freight,
		Mixed,
		Test
	};
	string Name;
	Orientation Direction = Orientation::None;
	int NumAxles = 2;
	int length = 0;
	int priority = 0;
	TrainClass Class = TrainClass::Empty;
	vector<Wagon*> wagons;
	//Timetable timetable = null;
	int TimeStopped = 0;
	//Path* path;
	int maxSpeed = 3;
	bool isSteam = false;
	TrackItem *start;
	Train(string name)
	{
		Name = name;
		/*new Thread(new Runnable(){
			@Override
			public void run()
			{
				assign(++foo);
			}
		}).start();*/
	}
	void assign(int number)
	{
		/*if(timetable != null) return;
		for(Timetable t : Regulation.services)
		{
			if(t.number == number) timetable = t;
		}
		if(timetable == null) timetable = new Timetable(number);
		timetable.train = this;
		timetable.validate();*/
	}
	void addWagon(Wagon *w)
	{
		length+=w->length;
		wagons.push_back(w);
		w->train = this;
		bool steam = false;
		bool passengers = false;
		bool freight = false;
		/*if(w->isLocomotive)
		{
			Locomotive *e = (Locomotive)w;
			if(e->Class==PowerClass::Steam) steam = true;
			if(e->NeedsWater) priority++;
		}*/
		if(w->isFreight) freight = true;
		if(w->isCarriage) passengers = true;
		isSteam |= steam;
		if(Class == TrainClass::Empty)
		{
			if(freight) Class = TrainClass::Freight;
			else if(passengers) Class = TrainClass::Passengers;
		}
		if(Class == TrainClass::Freight && passengers) Class = TrainClass::Mixed;
		if(Class == TrainClass::Passengers && freight) Class = TrainClass::Mixed;
	}
	void setPriority()
	{
		priority = 0;
		if(isSteam) priority++;
		if(Class==TrainClass::Passengers) priority++;
		if((TimeStopped>90&&Class==TrainClass::Passengers)||TimeStopped>120) priority++;//Change TimeStopped to accumulated delay
	}
	void setPath()
	{
		/*vector<TrackItem*> pathItems;
		TrackItem *t = start->getNext(Direction);
		while(t!=null && t->BlockState == Direction && (t->SignalLinked == nullptr || !(t->SignalLinked->isMainSignal) || t->SignalLinked->Cleared || ((MainSignal*)t->SignalLinked)->Override))
		{
			t = t->getNext(Direction);
			pathItems.push_back(t);
		}
		if(pathItems.isEmpty()) path = nullptr;
		else path = new Path(pathItems);*/
	}
	/*static Train delayedTrain(Train t1, long delay1, Train t2, long delay2)
	{
		t1.setPriority();
		t2.setPriority();
		if(t1.priority < t2.priority && delay1 >= delay2) return t1;
		if(t1.priority > t2.priority && delay1 <= delay2) return t2;
		if(t1.priority == t2.priority)
		{
			if(delay1 > delay2) return t2;
			else return t1;
		}
		return null;
	}
	@Override
	public String toString()
	{
		return Name;
	}
	TimetableEntry change;
	Timer delayTimer = new Timer(30000, new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(!change.exited)
			{
				if(change.getExit().getTime() - new Date().getTime() < 10000)
				{
					change.setExit(new Date(change.getExit().getTime() + 30000));
					TimetableEntry e2 = change.getNext();
					if(e2!=null) e2.setEntry(change.getExit());
					timetable.reset();
					delayTimer.setInitialDelay((int)(change.getExit().getTime() - new Date().getTime()));
					delayTimer.start();
				}
			}
			else delayTimer.stop();
		}				
	});*/
	void updatePosition()
	{
		/*TrackItem newItem = wagons.get(0).axles.get(0).firstPosition;
		TrackItem prev = start;
		if(newItem!=null && prev!=null && prev.Station==newItem.Station) return;
		start = newItem;
		Direction = wagons.get(0).axles.get(0).orientation;
		if(timetable != null)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					for(TimetableEntry e : timetable.entries)
					{
						if(e.item.station == start.Station && !e.arrived)
						{
							if(e.getPrev()!=null)
							{
								e.getPrev().exited = true;
								e.getPrev().setExit(new Date());
							}
							e.arrived = true;
							e.setEntry(new Date());
							change = e;
							break;
						}
						if(prev!=null && e.item.station == prev.Station && !e.exited)
						{
							e.exited = true;
							e.setExit(new Date());
							if(e.getNext()!=null)
							{
								e.getNext().arrived = true;
								e.getNext().setEntry(e.getExit());
								change = e.getNext();
							}
							break;
						}
					}
					timetable.reset();
					if(change != null)
					{
						delayTimer.stop();
						delayTimer.setInitialDelay((int)(change.getExit().getTime() - new Date().getTime()));
						delayTimer.start();
						delayTimer.setRepeats(false);
					}
				}
			}).start();
		}*/
	}
};
