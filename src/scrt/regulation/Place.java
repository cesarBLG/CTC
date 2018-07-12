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
package scrt.regulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import scrt.FunctionalList;
import scrt.Orientation;
import scrt.ctc.Station;
import scrt.regulation.timetable.TimetableEntry;
import scrt.train.Train;

public class Place
{
	double PK;
	public double secondPK = 0;
	double Length = 0;
	public int maxSpeed;
	public boolean isPP;
	public FunctionalList<Track> tracks = new FunctionalList<>();
	List<Place> odd = new ArrayList<>();
	List<Place> even = new ArrayList<>();
	public String name;
	public Station station;
	public Place(){}
	public Place(Station station)
	{
		this.station = station;
		this.name = station.FullName;
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
	}
	public boolean crossingAvailable(Train arriving, Train waiting)
	{
		if(tracks.size() < 2) return false;
		TimetableEntry t1 = arriving.timetable.getEntry(this);
		TimetableEntry t2 = waiting.timetable.getEntry(this);
		if(t1.track==null)
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
			List<Overlap> l = track.getOverlaps(t2);
			if(!l.isEmpty())
			{
				for(Overlap o : l)
				{
					TimetableEntry e = o.entries[1];
					if(e.track.number == getTrackIndex(t2) && e.getEntry().getTime() > t2.getEntry().getTime() && e.timetable.train.Direction == waiting.Direction) continue;
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
		List<Track> avail = new ArrayList<>();
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
				for(Overlap o : t.getOverlaps(e))
				{
					TimetableEntry te = o.entries[1];
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
				Overlap o = t.getFirstOverlap(e);
				if(o.entries[1].timetable.train.Direction == e.timetable.train.Direction) same = i;
			}
			if(same==-1) return 0;
			return same;
		}
	}
	public List<Place> path(Place destination, Orientation dir, boolean start)
	{
		if(destination == this && !start)
		{
			List<Place> l = new ArrayList<>();
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
	public List<Overlap> getOverlaps()
	{
		List<Overlap> l = new ArrayList<>();
		for(Track t : tracks)
		{
			l.addAll(t.getOverlaps());
		}
		return l;
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
		if(name==null) return odd.get(0).toString() + "-" + even.get(0).toString();
		return name;
	}
}
