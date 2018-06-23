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
package scrt.regulation.timetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scrt.FunctionalList;
import scrt.Orientation;
import scrt.regulation.Overlap;
import scrt.regulation.Place;
import scrt.regulation.Track;
import scrt.train.Train;

public class Timetable
{
	public Train train;
	public int number;
	public int speed = 3;
	public FunctionalList<TimetableEntry> entries = new FunctionalList<TimetableEntry>();
	public long maxDelay = 300000;
	public boolean valid = false;
	public Orientation direction;
	public Timetable(int serviceNumber)
	{
		number = serviceNumber;
		direction = (serviceNumber % 2 == 0) ? Orientation.Even : Orientation.Odd;
	}
	public void set(Place origin, Place destination)
	{
		List<Place> list = origin.path(destination, direction, true);
		for(int i = 0; i<list.size(); i++)
		{
			entries.add(new TimetableEntry(this, list.get(i)));
		}
		entries.get(0).setEntry(new Date());
	}
	public void reset()
	{
		for(TimetableEntry e : entries)
		{
			if(!e.arrived)
			{
				e.setEntry(e.getEntry());
				return;
			}
		}
	}
	public TimetableEntry getEntry(Place p)
	{
		for(TimetableEntry t : entries)
		{
			if(t.item == p) return t;
		}
		return null;
	}
	public void validate()
	{
		valid = true;
		List<Timetable> tim = new ArrayList<Timetable>();
		for(TimetableEntry e : entries)
		{
			for(Track t : e.item.tracks)
			{
				for(TimetableEntry e1: t.occupation)
				{
					if(!tim.contains(e1.timetable)) tim.add(e1.timetable);
				}
			}
		}
		for(Timetable t : tim)
		{
			if(t!=this) t.reset();
		}
		entries.get(0).changed();
		//List<Timetable> timetables = new ArrayList<Timetable>();
		//solveOverlaps();
	}
	List<Overlap> getOverlapList()
	{
		List<Overlap> list = new ArrayList<Overlap>();
		for(TimetableEntry e : entries)
		{
			list.addAll(e.item.getOverlaps());
		}
		return list;
	}
	void solveOverlaps()
	{
		List<Overlap> list = getOverlapList();
		if(list.size()>0)
		{
			list.sort((o1, o2) -> o1.startTime.compareTo(o2.startTime));
			Overlap o = list.get(0);
			o.track.solveOverlap(o);
			solveOverlaps();
		}
	}
	@Override
	public String toString()
	{
		return Integer.toString(number);
	}
}
