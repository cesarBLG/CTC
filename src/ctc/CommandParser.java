package ctc;

import javax.swing.JOptionPane;

import gui.Main;

public class CommandParser {
	public static void Parse(String s, Loader l)
	{
		if(s.equals("reset")) Main.main(null);
		if(s.equals("exit")) System.exit(0);
		if(s.length()<5) return;
		String name = s.substring(0, s.length() - 4);
		String dep = s.substring(s.length() - 3);
		for(Signal sig : l.signals)
		{
			if(sig.Name.equalsIgnoreCase(name)&&sig.Station.Name.equalsIgnoreCase(dep)&&sig instanceof MainSignal)
			{
				((MainSignal) sig).UserRequest(!sig.ClearRequest);
				return;
			}
		}
		for(Itinerary i : l.itineraries)
		{
			if(i.Name.equalsIgnoreCase(name)&&i.Station.Name.equalsIgnoreCase(dep))
			{
				i.Establish();
				return;
			}
		}
		for(TrackItem t : l.items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(j.Number==Integer.parseInt(name.substring(1))&&j.Station.Name.equalsIgnoreCase(dep))
				{
					j.setSwitch(j.Switch == Position.Straight ? j.Class : Position.Straight);
					return;
				}
			}
		}
	}
}
