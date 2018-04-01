package scrt.regulation.timetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scrt.FunctionalList;
import scrt.regulation.Loader;
import scrt.regulation.Overlap;
import scrt.regulation.Place;
import scrt.regulation.Track;
import scrt.regulation.train.Train;

public class Timetable
{
	public Train train;
	public FunctionalList<TimetableEntry> entries = new FunctionalList<TimetableEntry>();
	public long maxDelay = 300000;
	public boolean valid = false;
	static long a = 0;
	public void set(Place origin, Place destination)
	{
		List<Place> list = origin.path(destination, train.Direction, true);
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
		List<Timetable> timetables = new ArrayList<Timetable>();
		solveOverlaps();
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
			Loader.g.updateData(Loader.t);
			o.track.solveOverlap(o);
			solveOverlaps();
		}
	}
}
