package ctc.train;

import ctc.*;
import ctc.train.Engine.*;

import java.util.ArrayList;
import java.util.List;

public class Train {
	public enum TrainClass
	{
		Empty,
		Passengers,
		Freight,
		Test
	}
	public String Name;
	List<TrackItem> Location = new ArrayList<TrackItem>();
	public Orientation Direction = Orientation.None;
	int NumAxles = 2;
	public int Length = 0;
	public int Priority = 0;
	public TrainClass Class;
	public List<Engine> Engines;
	public Timetable timetable = new Timetable();
	public int TimeStopped = 0;
	public Path path;
	public boolean EoT = false;
	Train(String s)
	{
		Name = s;
	}
	public void setPriority()
	{
		Priority = 0;
		if(Class==TrainClass.Passengers) Priority++;
		if((TimeStopped>90&&Class==TrainClass.Passengers)||TimeStopped>120) Priority++;
		boolean Steam = false;
		for(Engine e : Engines)
		{
			if(e.Class==PowerClass.Steam) Steam = true;
			if(e.NeedsWater) Priority++;
		}
		if(Steam) Priority++;
	}
	public void setPath()
	{
		List<TrackItem> pathItems = new ArrayList<TrackItem>();
		TrackItem t = Location.get(Location.size()-1);
		while(t!=null && t.BlockState == Direction && (t.SignalLinked == null || t.SignalLinked.Cleared || t.SignalLinked.Override))
		{
			t = t.getNext(Direction);
			pathItems.add(t);
		}
		if(pathItems.isEmpty()) path = null;
		else path = new Path(pathItems);
	}
}
