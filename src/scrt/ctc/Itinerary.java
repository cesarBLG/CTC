package scrt.ctc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;

public class Itinerary {
	Hashtable<Integer, Integer> Switches = new Hashtable<Integer, Integer>();
	List<String> Signals = new ArrayList<String>();
	int Number;
	enum Itinerary_Type
	{
		Entry,
		Exit,
		Direct
	}
	String Name;
	Itinerary_Type Class;
	Station Station;
	Itinerary(String name, Station dep, List<String> sig, Hashtable<Integer, Integer> sw)
	{
		Name = name;
		switch(name.charAt(0))
		{
			case 'P':
				Class = Itinerary_Type.Direct;
				break;
			case 'R':
				Class = Itinerary_Type.Entry;
				break;
			case 'X':
				Class = Itinerary_Type.Exit;
				break;
		}
		Number = Integer.parseInt(name.substring(1));
		Station = dep;
		Switches.putAll(sw);
		Signals.addAll(sig);
	}
	public void Establish()
	{
		for(TrackItem t : Station.Items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(Switches.containsKey(j.Number))
				{
					if(Switches.get(j.Number)==0)
					{
						if(j.Muelle!=-1) j.Muelle = 0;
						j.setSwitch(Position.Straight);
						if(j.Switch!=Position.Straight)
						{
							Undo();
							return;
						}
					}
					else
					{
						if(j.Muelle!=-1) j.Muelle = 1;
						j.setSwitch(j.Class);
						if(j.Switch!=j.Class)
						{
							Undo();
							return;
						}
					}
				}
			}
		}
		for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(!(sig instanceof MainSignal)) continue;
				if(sig.Name.equalsIgnoreCase(s))
				{
					((MainSignal) sig).UserRequest(true);
					if(!sig.Cleared)
					{
						Undo();
						return;
					}
				}
			}
		}
	}
	public boolean Established()
	{
		for(TrackItem t : Station.Items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(Switches.containsKey(j.Number))
				{
					if(Switches.get(j.Number)==0)
					{
						if(j.Switch!=Position.Straight) return false;
					}
					else if(j.Switch!=j.Class) return false;
				}
			}
		}
		for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(!(sig instanceof MainSignal)) continue;
				if(sig.Name.equalsIgnoreCase(s))
				{
					if(!sig.Cleared) return false;
				}
			}
		}
		return true;
	}
	public void Undo()
	{
		/*for(String s : Signals)
		{
			for(Signal sig : Station.Signals)
			{
				if(sig.Name.equalsIgnoreCase(s) && sig.Station.equals(Station))
				{
					sig.Close();
				}
			}
		}*/
	}
	public String toString()
	{
		return Name.concat("/").concat(Station.Name);
	}
	public static void set(TrackItem s, TrackItem e, Orientation dir, boolean shunt)
	{
		List<TrackItem> path = s.path(e, dir, true);
		for(TrackItem t : path)
		{
			if(path.indexOf(t) < path.size() - 1 && !path.contains(t.getNext(dir)))
			{
				Junction j = (Junction)t;
				if(j.Muelle != -1) j.Muelle = 1-j.Muelle;
				j.setSwitch(j.Switch == Position.Straight ? j.Class : Position.Straight);
			}
		}
		for(TrackItem t : path)
		{
			if(t==e) break;
			if(t.SignalLinked != null && t.SignalLinked.Direction == dir && t.SignalLinked instanceof MainSignal)
			{
				t.SignalLinked.OverrideRequest = shunt;
				((MainSignal)t.SignalLinked).UserRequest(true);
			}
		}
	}
}
