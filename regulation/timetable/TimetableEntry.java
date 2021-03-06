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

import java.util.Date;

import scrt.regulation.OccupationInterval;
import scrt.regulation.Place;
import scrt.regulation.Track;

public class TimetableEntry extends OccupationInterval{
	public Place item;
	public Track track;
	private Date time;
	private long stopTime = 0;
	public long minStop = 0;
	public Date minExit = null;
	public boolean arrived = false;
	public boolean exited = false;
	public Timetable timetable;
	public TimetableEntry getNext()
	{
		int i = timetable.entries.indexOf(this);
		if(i>=0 && i<timetable.entries.size() - 1) return timetable.entries.get(i + 1);
		return null;
	}
	public TimetableEntry getPrev()
	{
		int i = timetable.entries.indexOf(this);
		if(i>0 && i<timetable.entries.size()) return timetable.entries.get(i - 1);
		return null;
	}
	public TimetableEntry(Timetable timetable, Place item)
	{
		this.timetable = timetable;
		this.item = item;
	}
	@Override
	public void setEntry(Date d)
	{
		setTime(d);
		TimetableEntry p = getPrev();
		if(p!=null) p.changed();
		else changed();
	}
	void setTime(Date d)
	{
		TimetableEntry p = getPrev();
		TimetableEntry n = getNext();
		stopTime = minStop;
		if(p!=null && !arrived)
		{
			p.setExit(d);
			time = p.getExit();
		}
		else time = d;
		if(n!=null) n.setTime(getExit());
	}
	@Override
	public Date getEntry()
	{
		return time;
	}
	@Override
	public Date getExit()
	{
		return new Date(time.getTime() + stopTime + (long)((item.getLength() / Math.min(timetable.speed, item.maxSpeed)) * 1000));
	}
	@Override
	public void setExit(Date d)
	{
		long t = d.getTime() - getExit().getTime();
		stopTime += t;
		if(!exited)
		{
			stopTime = Math.max(stopTime, minStop);
			if(minExit != null) stopTime += Math.max(minExit.getTime() - getExit().getTime(), 0);
		}
	}
	public void setStop(long seconds)
	{
		minStop = seconds * 1000;
		TimetableEntry n = getNext();
		if(n!=null) n.setEntry(getExit());
	}
	public void setStop(Date exitDate)
	{
		minExit = exitDate;
		setExit(exitDate);
		TimetableEntry n = getNext();
		if(n!=null) n.setEntry(getExit());
	}
	public void changed()
	{
		if(!timetable.valid) return;
		item.occupy(this);
		TimetableEntry n = getNext();
		if(n!=null) n.changed();
	}
	@Override
	public String toString()
	{
		return item.name + (track!=null ?  " vía " + track.number + ", " : " ") + getEntry() + " " + getExit();
	}
}
