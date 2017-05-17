package main;

import javax.swing.JOptionPane;

public class CommandParser {
	static void Parse(String s, Loader l)
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
			}
		}
		for(Itinerary i : l.itineraries)
		{
			if(i.Name.equalsIgnoreCase(name)&&i.Station.Name.equalsIgnoreCase(dep)) i.Establish();
		}
	}
}
