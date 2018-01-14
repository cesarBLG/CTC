package scrt.regulation.train;

import java.util.ArrayList;
import java.util.List;

import scrt.*;
import scrt.ctc.TrackItem;
import scrt.regulation.timetable.Timetable;
import scrt.regulation.timetable.TimetableEntry;
import scrt.regulation.train.Engine.*;

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
	public List<Engine> Engines = new ArrayList<Engine>();
	public Timetable timetable;
	public int TimeStopped = 0;
	public Path path;
	public boolean EoT = false;
	public int Number;
	public int maxSpeed = 3;
	public Train(int number)
	{
		Number = number;
		if(number % 2 == 0) Direction = Orientation.Even;
		else Direction = Orientation.Odd;
		timetable = new Timetable();
		timetable.train = this;
	}
	public void setPriority()
	{
		Priority = 0;
		if(Class==TrainClass.Passengers) Priority++;
		if((TimeStopped>90&&Class==TrainClass.Passengers)||TimeStopped>120) Priority++;//Change TimeStopped to accumulated delay
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
	public static Train delayedTrain(Train t1, long delay1, Train t2, long delay2)
	{
		t1.setPriority();
		t2.setPriority();
		if(t1.Priority < t2.Priority && delay1 >= delay2) return t1;
		if(t1.Priority > t2.Priority && delay1 <= delay2) return t2;
		if(t1.Priority == t2.Priority)
		{
			if(delay1 > delay2) return t2;
			else return t1;
		}
		return null;
	}
}
