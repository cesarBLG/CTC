package scrt.regulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.FunctionalList;
import scrt.Orientation;
import scrt.regulation.timetable.TimetableEntry;
import scrt.regulation.train.Train;

public class Place
{
	double PK;
	public double secondPK = 0;
	double Length = 0;
	public int maxSpeed;
	public boolean isPP;
	public FunctionalList<Track> tracks = new FunctionalList<Track>();
	List<Place> odd = new ArrayList<Place>();
	List<Place> even = new ArrayList<Place>();
	String name;
	public Place(){}
	public Place(String name)
	{
		this.name = name;
	}
	public double getLength()
	{
		return Length;
	}
	public double getPK()
	{
		return PK;
	}
	public void add(Place p, Orientation dir)
	{
		if(dir == Orientation.Odd)
		{
			if(!odd.contains(p))
			{
				odd.add(p);
				p.add(this, Orientation.Even);
			}
		}
		if(dir == Orientation.Even)
		{
			if(!even.contains(p))
			{
				even.add(p);
				p.add(this, Orientation.Odd);
			}
		}
	}
	public void occupy(TimetableEntry e)
	{
		for(Track t : tracks)
		{
			t.occupation.remove(e);
		}
		int ta = getTrackIndex(e);
		Track t = tracks.get(ta);
		e.track = t;
		t.occupation.add(e);
		t.solveOverlaps();
	}
	public boolean crossingAvailable(Train arriving, Train waiting)
	{
		if(tracks.size() < 2) return false;
		TimetableEntry t1 = arriving.timetable.getEntry(this);
		TimetableEntry t2 = waiting.timetable.getEntry(this);
		if(t1.track == null)
		{
			int ta = getTrackIndex(t1);
			Track t = tracks.get(ta);
			t1.track = t;
			t.occupation.add(t1);
		}
		Date d = t2.getExit();
		t2.timetable.valid = false;
		t2.setExit(new Date(t1.getEntry().getTime() + 10000));
		Track t = t2.track;
		t2.track.occupation.remove(t2);
		t2.track = null;
		boolean r = false;
		if(t1.track.number!=getTrackIndex(t2)) r = true;
		int odd = 0;
		int even = 0;
		for(Track track : tracks)
		{
			if(getTrackIndex(t2) == tracks.indexOf(track))
			{
				if(t2.timetable.train.Direction == Orientation.Odd) odd++;
				else even++;
			}
			List<TimetableEntry> l = track.getOverlaps(t2);
			if(!l.isEmpty())
			{
				for(TimetableEntry e : l)
				{
					//if(e.track.number == getTrackIndex(t2) && e.getEntry().getTime() > t2.getEntry().getTime() && e.timetable.train.Direction == waiting.Direction) continue;
					if(e.timetable.train.Direction == Orientation.Odd) odd++;
					if(e.timetable.train.Direction == Orientation.Even) even++;
				}
			}
			else odd = even = -1;
		}
		if(odd!=-1 && even!=-1)
		{
			if(odd + even > tracks.size())
			{
				if(odd>1&&even>1)
				{
					r = false;
				}
			}
		}
		t2.setExit(d);
		t2.timetable.valid = true;
		t2.track = t;
		t.occupation.add(t2);
		return r;
	}
	public int getTrackIndex(TimetableEntry e)
	{
		if(tracks.size()==1) return 0;
		List<Track> avail = new ArrayList<Track>();
		for(Track t : tracks)
		{
			if(t.getOverlaps(e).isEmpty()) avail.add(t);
		}
		if(avail.size()>1)
		{
			for(Track t : avail)
			{
				if((tracks.indexOf(t) + 1) % 2 == 0 ^ e.timetable.train.Direction == Orientation.Odd) return tracks.indexOf(t);
			}
			return tracks.indexOf(avail.get(0));
		}
		else if(avail.size() == 1)
		{
			int same = -1;
			int other = -1;
			for(int i=0; i<tracks.size(); i++)
			{
				Track t = tracks.get(i);
				for(TimetableEntry te : t.getOverlaps(e))
				{
					if(te.timetable.train.Direction != e.timetable.train.Direction && other==-1) other = i;
					if(te.timetable.train.Direction == e.timetable.train.Direction && same==-1 && t != avail.get(0)) same = i;
				}
			}
			if(other != -1) return avail.get(0).number;
			if(same != -1) return same;
			return 0;
		}
		else
		{
			int same = -1;
			for(int i=0; i<tracks.size(); i++)
			{
				Track t = tracks.get(i);
				if(t.getFirstOverlap(e).timetable.train.Direction == e.timetable.train.Direction) same = i;
			}
			if(same==-1) return 0;
			return same;
		}
	}
	public List<Place> path(Place destination, Orientation dir, boolean start)
	{
		if(destination == this && !start)
		{
			List<Place> l = new ArrayList<Place>();
			l.add(this);
			return l;
		}
		for(Place p : dir == Orientation.Odd ? odd : even)
		{
			List<Place> l = p.path(destination, dir, false);
			if(l!=null)
			{
				l.add(this);
				if(start) Collections.reverse(l);
				return l;
			}
		}
		return null;
	}
	public static Place IntermediatePlace(Place odd, Place even, int numtr)
	{
		Place p = new Place();
		p.isPP = false;
		p.PK = odd.PK - (odd.PK - even.PK) / 2;
		p.Length = (odd.PK - odd.Length / 2) - (even.PK + even.Length / 2);
		if(p.Length < 0)
		{
			p.Length = (odd.PK - odd.Length / 2) - (even.secondPK + even.Length / 2);
			p.PK = odd.PK - (odd.PK - even.secondPK) / 2;
		}
		p.maxSpeed = 10;
		while(numtr > 0)
		{
			p.tracks.add(new Track(p, (int) p.Length));
			numtr--;
		}
		p.add(odd, Orientation.Odd);
		p.add(even, Orientation.Even);
		return p;
	}
	@Override
	public String toString()
	{
		return name;
	}
	public void solveOverlaps() {
		for(Track t : tracks) t.solveOverlaps();
	}
}
