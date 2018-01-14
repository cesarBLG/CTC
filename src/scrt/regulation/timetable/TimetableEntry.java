package scrt.regulation.timetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.ctc.TrackItem;
import scrt.regulation.OccupationInterval;
import scrt.regulation.Place;
import scrt.regulation.Track;

public class TimetableEntry extends OccupationInterval{
	public Place item;
	public Track track;
	private Date time;
	private long stopTime = 0;
	public long minStop = 0;
	public boolean arrived = false;
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
		if(p!=null)
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
		return new Date(time.getTime() + stopTime + (long)((item.getLength() / Math.min(timetable.train.maxSpeed, item.maxSpeed)) * 1000));
	}
	@Override
	public void setExit(Date d)
	{
		long t = d.getTime() - getExit().getTime();
		stopTime += t;
		stopTime = Math.max(stopTime, minStop);
	}
	public void setStop(long seconds)
	{
		minStop = seconds * 1000;
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
}
