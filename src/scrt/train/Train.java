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
package scrt.train;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import scrt.Orientation;
import scrt.ctc.TrackItem;
import scrt.regulation.Regulation;
import scrt.regulation.timetable.Timetable;
import scrt.regulation.timetable.TimetableEntry;
import scrt.train.Locomotive.PowerClass;

public class Train {
	public enum TrainClass
	{
		Empty,
		Passengers,
		Freight,
		Mixed,
		Test
	}
	public String Name;
	public Orientation Direction = Orientation.None;
	int NumAxles = 2;
	public int length = 0;
	public int priority = 0;
	public TrainClass Class = TrainClass.Empty;
	public List<Wagon> wagons = new ArrayList<Wagon>();
	public Timetable timetable = null;
	public int TimeStopped = 0;
	public Path path;
	public int maxSpeed = 3;
	boolean isSteam = false;
	static int foo = 0;
	TrackItem start;
	public Train(String name)
	{
		Name = name;
		assign(++foo);
	}
	public void assign(int number)
	{
		if(timetable != null) return;
		for(Timetable t : Regulation.services)
		{
			if(t.number == number) timetable = t;
		}
		if(timetable == null) timetable = new Timetable(number);
		timetable.train = this;
		timetable.validate();
	}
	public void addWagon(Wagon w)
	{
		length+=w.length;
		wagons.add(w);
		w.train = this;
		boolean steam = false;
		boolean passengers = false;
		boolean freight = false;
		if(w instanceof Locomotive)
		{
			Locomotive e = (Locomotive)w;
			if(e.Class==PowerClass.Steam) steam = true;
			if(e.NeedsWater) priority++;
		}
		if(w instanceof FreightWagon) freight = true;
		if(w instanceof PassengerWagon) passengers = true;
		isSteam |= steam;
		if(Class == TrainClass.Empty)
		{
			if(freight) Class = TrainClass.Freight;
			else if(passengers) Class = TrainClass.Passengers;
		}
		if(Class == TrainClass.Freight && passengers) Class = TrainClass.Mixed;
		if(Class == TrainClass.Passengers && freight) Class = TrainClass.Mixed;
	}
	public void setPriority()
	{
		priority = 0;
		if(isSteam) priority++;
		if(Class==TrainClass.Passengers) priority++;
		if((TimeStopped>90&&Class==TrainClass.Passengers)||TimeStopped>120) priority++;//Change TimeStopped to accumulated delay
	}
	public void setPath()
	{
		List<TrackItem> pathItems = new ArrayList<TrackItem>();
		TrackItem t = start.getNext(Direction);
		while(t!=null && t.BlockState == Direction && (t.SignalLinked == null || t.SignalLinked.Cleared || t.SignalLinked.Override))
		{
			t = t.getNext(Direction);
			pathItems.add(t);
		}
		if(pathItems.isEmpty()) path = null;
		else path = new Path(pathItems);
	}
	public static Train delayedTrain(Train t1, long delay1, Train t2, long delay2)
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
					var e2 = change.getNext();
					if(e2!=null) e2.setEntry(change.getExit());
					timetable.reset();
					delayTimer.setInitialDelay((int)(change.getExit().getTime() - new Date().getTime()));
					delayTimer.start();
				}
			}
			else delayTimer.stop();
		}				
	});
	public void updatePosition()
	{
		TrackItem newItem = wagons.get(0).axles.get(0).firstPosition;
		TrackItem prev = start;
		if(newItem!=null && prev!=null && prev.Station==newItem.Station) return;
		start = newItem;
		Direction = wagons.get(0).axles.get(0).orientation;
		if(timetable != null)
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
	}
}
