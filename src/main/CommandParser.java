package main;

public class CommandParser {
	static void Parse(String s, Loader l)
	{
		for(Signal sig : l.signals)
		{
			if(sig.Name.equals(s)) sig.Clear();
		}
		if(s.equals("reset")) Main.main(null);
	}
}
