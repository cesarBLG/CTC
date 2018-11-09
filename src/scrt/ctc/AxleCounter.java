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
package scrt.ctc;

import static scrt.Orientation.OppositeDir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.com.packet.ACData;
import scrt.com.packet.ACID;
import scrt.com.packet.Packet;
import scrt.ctc.TrackItem.TrackComparer;
import scrt.ctc.Signal.MainSignal;
import scrt.event.AxleEvent;
import scrt.event.OccupationEvent;
import scrt.event.SCRTListener;
import scrt.event.SCRTEvent;
import scrt.train.Locomotive;
import scrt.train.Train;
import scrt.train.Wagon;

public class AxleCounter extends CTCItem
{
	public Station station;
	public int Number;
	List<Axle> evenAxles = new ArrayList<>();
	List<Axle> oddAxles = new ArrayList<>();
	public boolean Working = true;
	TrackItem linked;
	AxleCounter(ACID acid)
	{
		Number = acid.Num;
		station = Station.byNumber(acid.stationNumber);
	}
	AxleCounter(int num, Station dep)
	{
		Number = num;
		station = dep;
	}
	public void EvenPassed()
	{
		Working = true;
		Passed(Orientation.Even);
	}
	public void OddPassed()
	{
		Working = true;
		Passed(Orientation.Odd);
	}
	public List<Axle> getAxles(Orientation dir)
	{
		return dir == Orientation.Even ? evenAxles : oddAxles;
	}
	boolean release;
	TrackItem start;
	Axle axle;
	Orientation startDir;
	public void Passed(Orientation direction)
	{
		Hashtable<AxleCounter, Orientation> counters = new Hashtable<>();
		TrackItem.InverseExploration(linked, new TrackComparer()
				{
					@Override
					public boolean condition(TrackItem t, Orientation dir, TrackItem p)
					{
						if(t == null) return false;
						if(t.CounterLinked!=AxleCounter.this && t.CounterLinked!=null)
						{
							counters.put(t.CounterLinked, OppositeDir(dir));
							return false;
						}
						return true;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
					{
						return true;
					}
				}, OppositeDir(direction));
		start = null;
		axle = null;
		for(AxleCounter ac : counters.keySet())
		{
			Orientation counterDir = counters.get(ac);
			if(ac.getAxles(counterDir).size() != 0)
			{
				start = ac.linked;
				axle = ac.getAxles(counterDir).remove(0);
				startDir = counterDir;
				break;
			}
		}
		if(axle == null)
		{
			axle = new Axle();
			startDir = direction;
			if(getAxles(direction).isEmpty())
			{
				Wagon w = new Locomotive();
				w.addAxle(axle);
				Train t = new Train("Tren en pruebas");
				Loader.trains.add(t);
				t.addWagon(w);
			}
			else
			{
				Wagon w = new Wagon();
				w.addAxle(axle);
				Train t = getAxles(direction).get(getAxles(direction).size()-1).wagon.train;
				t.addWagon(w);
			}
		}
		else getAxles(OppositeDir(direction)).remove(axle);
		getAxles(direction).add(axle);
		axle.lastPosition = linked;
		if(start != null)
		{
			TrackItem.DirectExploration(start, new TrackComparer()
			{
				@Override
				public boolean condition(TrackItem t, Orientation dir, TrackItem p)
				{
					if(t == start && t.CounterDir == dir) return true;
					if(t == linked && t.CounterDir != dir) return false;
					if(p == linked) return false;
					t.actionPerformed(new AxleEvent(AxleCounter.this, dir, true, true, p, axle));
					return true;
				}
				@Override
				public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
				{
					return true;
				}
			}, startDir);
		}
		TrackItem.DirectExploration(linked, new TrackComparer()
		{
			@Override
			public boolean condition(TrackItem t, Orientation dir, TrackItem p)
			{
				if(t == linked && t.CounterDir == dir) return true;
				if(t == null)
				{
					//TODO: Remove axle from train
					return false;
				}
				if(t.CounterLinked != null && t.CounterLinked != AxleCounter.this && t.CounterDir != dir)
				{
					t.CounterLinked.getAxles(OppositeDir(dir)).add(axle);
					axle.firstPosition = p;
					axle.orientation = dir;
					return false;
				}
				if(p != null && p.CounterLinked != null && p.CounterLinked != AxleCounter.this)
				{
					p.CounterLinked.getAxles(OppositeDir(dir)).add(axle);
					axle.firstPosition = p;
					axle.orientation = dir;
					return false;
				}
				t.actionPerformed(new AxleEvent(AxleCounter.this, dir, false, true, p, axle));
				return true;
			}
			@Override
			public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
			{
				return true;
			}
		}, direction);
		axle.update();
		if(start != null)
		{
			TrackItem.DirectExploration(start, new TrackComparer()
			{
				@Override
				public boolean condition(TrackItem t, Orientation dir, TrackItem p)
				{
					if(t == start && t.CounterDir == dir) return true;
					if(t == linked && t.CounterDir != dir) return false;
					if(p == linked) return false;
					t.actionPerformed(new AxleEvent(AxleCounter.this, dir, true, false, p, axle));
					return true;
				}
				@Override
				public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
				{
					return true;
				}
			}, startDir);
		}
		TrackItem.DirectExploration(linked, new TrackComparer()
		{
			@Override
			public boolean condition(TrackItem t, Orientation dir, TrackItem p)
			{
				if(t == linked && t.CounterDir == dir) return true;
				if(t == null)
				{
					//TODO: Remove axle from train
					return false;
				}
				if(t.CounterLinked != null && t.CounterLinked != AxleCounter.this && t.CounterDir != dir)
				{
					t.CounterLinked.getAxles(OppositeDir(dir)).add(axle);
					axle.firstPosition = p;
					axle.orientation = dir;
					return false;
				}
				if(p != null && p.CounterLinked != null && p.CounterLinked != AxleCounter.this)
				{
					p.CounterLinked.getAxles(OppositeDir(dir)).add(axle);
					axle.firstPosition = p;
					axle.orientation = dir;
					return false;
				}
				t.actionPerformed(new AxleEvent(AxleCounter.this, dir, false, false, p, axle));
				return true;
			}
			@Override
			public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
			{
				return true;
			}
		}, direction);
	}
	public void Error()
	{
		Working = false;
		Passed(Orientation.Unknown);
	}
	public void addListener(SCRTListener al)
	{
		if(!listeners.contains(al)) listeners.add(al);
	}
	@Override
	public ACID getID()
	{
		ACID acid = new ACID();
		acid.stationNumber = station.AssociatedNumber;
		acid.Num = Number;
		acid.dir = (Number % 2 == 0) ? Orientation.Even : Orientation.Odd;
		return acid;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof ACData)
		{
			ACData a = (ACData)p;
			if(!a.id.equals(getID())) return;
			if(a.dir == Orientation.Odd) OddPassed();
			else EvenPassed();
		}
	}
	@Override
	public void actionPerformed(SCRTEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void muteEvents(boolean mute)
	{
		// TODO Auto-generated method stub
		
	}
}
