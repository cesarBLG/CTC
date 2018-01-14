package scrt.regulation.timetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.FunctionalList;
import scrt.Orientation;
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
		entries.get(0).changed();
	}
}
