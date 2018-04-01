package scrt.regulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scrt.FunctionalList;
import scrt.regulation.timetable.TimetableEntry;
import scrt.regulation.train.Train;

public class Track
{
	public Place location;
	public int Length;
	public int number;
	public FunctionalList<TimetableEntry> occupation = new FunctionalList<TimetableEntry>();
	public Track(Place loc, int length)
	{
		location = loc;
		Length = length;
		location.tracks.add(this);
		number = location.tracks.indexOf(this);
	}
	public List<Overlap> getOverlaps()
	{
		List<Overlap> l = new ArrayList<Overlap>();
		for(int i=0; i<occupation.size(); i++)
		{
			TimetableEntry a = occupation.get(i);
			l.addAll(getOverlaps(a));
		}
		return l;
	}
	public List<Overlap> getOverlaps(TimetableEntry a)
	{
		List<Overlap> l = new ArrayList<Overlap>();
		for(int j=0; j<occupation.size(); j++)
		{
			TimetableEntry b = occupation.get(j);
			if(effectiveOverlap(a,b))
			{
				Overlap o = new Overlap();
				o.entries[0] = a;
				o.entries[1] = b;
				o.track = this;
				o.startTime = new Date(Math.max(a.getEntry().getTime(), b.getEntry().getTime()));
				o.endTime = new Date(Math.min(a.getExit().getTime(), b.getExit().getTime()));
				l.add(o);
			}
			else continue;
		}
		return l;
	}
	boolean effectiveOverlap(TimetableEntry a, TimetableEntry b)
	{
		if(a!=b && a.overlapsWith(b))
		{
			if(a.timetable.train.Direction == b.timetable.train.Direction && !location.isPP)
			{
				long e1 = a.getEntry().getTime();
				long e2 = b.getEntry().getTime();
				long s1 = a.getExit().getTime();
				long s2 = b.getExit().getTime();
				if((e1<e2 && s1>s2)||(e1>e2 && s1<s2)||(e1<e2 && s1<s2 && a.getEntry().getTime() + 10000 > b.getEntry().getTime()))
				{
					return true;
				}
				return false;
			}
			return true;
		}
		return false;
	}
	public Overlap getFirstOverlap(TimetableEntry a)
	{
		occupation.sort((t1,t2) -> t2.getEntry().compareTo(t1.getEntry()));
		Overlap ov = null;
		for(int i=0; i<occupation.size(); i++)
		{
			TimetableEntry b = occupation.get(i);
			if(effectiveOverlap(a,b))
			{
				Overlap o = new Overlap();
				o.entries[0] = a;
				o.entries[1] = b;
				o.track = this;
				o.startTime = new Date(Math.max(a.getEntry().getTime(), b.getEntry().getTime()));
				o.endTime = new Date(Math.min(a.getExit().getTime(), b.getExit().getTime()));
				if(ov==null || ov.startTime.getTime() > o.startTime.getTime()) ov = o;
			}
			else continue;
		}
		return ov;
	}
	/*public void solveOverlaps()
	{
		for(int i=0; i<occupation.size(); i++)
		{
			while(!solveOverlaps(occupation.get(i)));
		}
	}*/
	public void solveOverlap(Overlap o)
	{
		TimetableEntry a = o.entries[0];
		TimetableEntry b = o.entries[1];
		if(location.getTrackIndex(a)!=number)
		{
			location.occupy(a);
			return;
		}
		if(location.getTrackIndex(b)!=number)
		{
			location.occupy(b);
			return;
		}
		if(a.timetable.train.Direction != b.timetable.train.Direction)
		{
			TimetableEntry t1 = a.getNext();
			while(t1!=null&&!t1.item.crossingAvailable(a.timetable.train, b.timetable.train)){t1 = t1.getNext();}
			TimetableEntry t2 = b.getNext();
			while(t2!=null&&!t2.item.crossingAvailable(b.timetable.train, a.timetable.train)){t2 = t2.getNext();}
			if(t1!=null||t2!=null)
			{
				if(t2==null)
				{
					b.timetable.getEntry(t1.item).getNext().setEntry(new Date(t1.getEntry().getTime() + 10000));
				}
				else if(t1==null)
				{
					a.timetable.getEntry(t2.item).getNext().setEntry(new Date(t2.getEntry().getTime() + 10000));
				}
				else
				{
					long delay1 = t2.getEntry().getTime() + 10000 - a.timetable.getEntry(t2.item).getExit().getTime();
					long delay2 = t1.getEntry().getTime() + 10000 - b.timetable.getEntry(t1.item).getExit().getTime();
					Train t = Train.delayedTrain(a.timetable.train, delay1, b.timetable.train, delay2);
					if(t == a.timetable.train)
					{
						a.timetable.getEntry(t2.item).getNext().setEntry(new Date(t2.getEntry().getTime() + 10000));
					}
					else
					{
						b.timetable.getEntry(t1.item).getNext().setEntry(new Date(t1.getEntry().getTime() + 10000));
					}
				}
			}
			else
			{
				//Suprimir uno de los trenes
			}
			return;
		}
		else if(location.isPP)
		{
			if(a.getEntry().getTime() > b.getEntry().getTime() || (a.getEntry().getTime()==b.getEntry().getTime() && a.getExit().getTime()>b.getExit().getTime()))
			{
				a.setEntry(new Date(b.getExit().getTime() + 1000));
			}
			else
			{
				b.setEntry(new Date(a.getExit().getTime() + 1000));
			}
			return;
		}
		else
		{
			long e1 = a.getEntry().getTime();
			long e2 = b.getEntry().getTime();
			long s1 = a.getExit().getTime();
			long s2 = b.getExit().getTime();
			if(e1<e2 && s1>s2)
			{
				b.getNext().setEntry(new Date(a.getExit().getTime() + 5000));
				return;
			}
			else if(e1>e2 && s1<s2)
			{
				a.getNext().setEntry(new Date(b.getExit().getTime() + 5000));
				return;
			}
			else if(e1<e2 && s1<s2 && a.getEntry().getTime() + 10000 > b.getEntry().getTime())
			{
				b.setEntry(new Date(a.getEntry().getTime() + 10000));
				b.changed();
				return;
			}
		}
	}
}
