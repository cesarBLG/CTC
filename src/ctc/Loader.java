package ctc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

public class Loader {
	public List<TrackItem> items = new ArrayList<TrackItem>();
	public List<Signal> signals = new ArrayList<Signal>();
	public List<AxleCounter> counters = new ArrayList<AxleCounter>();
	public List<Itinerary> itineraries = new ArrayList<Itinerary>();
	public List<Station> stations = new ArrayList<Station>();
	public Loader()
	{
		File layout = new File("layout.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(layout);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			Station Workingdep = null;
			while(s!=null)
			{
				if(s.startsWith("["))
				{
					String name = s.substring(s.indexOf(']')+2, s.indexOf(']')+5);
					boolean Exists = false;
					for(Station st : stations)
					{
						if(st.Name.equals(name))
						{
							Exists = true;
							Workingdep = st;
						}
					}
					if(!Exists)
					{
						Workingdep = new Station(name);
						stations.add(Workingdep);
					}
				}
				if(s.charAt(0)=='$')
				{
					String n = ReadParameter(s);
					int Number = Integer.parseInt(ReadParameter(br.readLine()));
					int x = Integer.parseInt(ReadParameter(br.readLine()));
					int y = Integer.parseInt(ReadParameter(br.readLine()));
					if(Number>6)
					{
						int SwitchNumber = Integer.parseInt(n.substring(1, 2));
						String m = ReadParameter(br.readLine());
						Junction j = new Junction(SwitchNumber, Workingdep, Number == 7 ? Position.Left : Position.Right);
						if(m.contains("Desviada")) j.Muelle = 1;
						else if(m.contains("Directa")) j.Muelle = 0;
						else j.Muelle = -1;
						j.setSwitch(Position.Straight);
						j.x = x;
						j.y = y;
						Workingdep.Items.add(j);
						items.add(j);
					}
					else
					{
						String sig = ReadParameter(br.readLine());
						String ac = ReadParameter(br.readLine());
						int o = 0, e = 0;
						switch(Number)
						{
							case 1:
								o = e = 1;
								break;
							case 2:
								o = e = -1;
								break;
							case 3:
								e = 1;
								break;
							case 4:
								e = -1;
								break;
							case 5:
								o = 1;
								break;
							case 6:
								o = -1;
								break;
							default:
								break;
						}
						TrackItem t = new TrackItem(n, Workingdep, o, e);
						t.x = x;
						t.y = y;
						if(sig.charAt(0)!='0')
						{
							Signal Sig;
							if(sig.charAt(0)=='I') Sig = new ExitIndicator(sig, Workingdep);
							else if(sig.charAt(0)=='F') Sig = new FixedSignal(sig.charAt(1)=='1' ? Orientation.Odd : Orientation.Even, Aspect.Anuncio_parada, Workingdep);
							else Sig = new MainSignal(sig, Workingdep);
							t.setSignal(Sig);
							Sig.Linked = t;
							Workingdep.Signals.add(Sig);
							signals.add(Sig);
						}
						if(ac.charAt(0)!='0')
						{
							AxleCounter Ac = new AxleCounter(Integer.parseInt(ac), Workingdep);
							t.CounterLinked = Ac;
							Ac.Linked = t;
							t.CounterDir = Ac.Number % 2 == 0 ? Orientation.Even : Orientation.Odd;
							counters.add(Ac);
						}
						for(TrackItem at : items)
						{
							if(at.x==t.x && at.y == t.y)
							{
								t.CrossingLinked = at;
								at.CrossingLinked = t;
							}
						}
						Workingdep.Items.add(t);
						items.add(t);
					}
				}
				if(s.charAt(0)=='!')
				{
					String itname = ReadParameter(s);
					Hashtable<Integer, Integer> itsw = new Hashtable<Integer, Integer>();
					List<String> itsig = new ArrayList<String>();
					int Items = Integer.parseInt(ReadParameter(br.readLine()));
					while(Items>0)
					{
						String name = ReadParameter(br.readLine());
 						if(name.charAt(0)=='A')
						{
							int Number = Integer.parseInt(name.substring(1))/10;
							int Position = Integer.parseInt(Character.toString(name.charAt(name.length()-1)));
							itsw.put(Number, Position);
						}
						else itsig.add(name);
						Items--;
					}
					itineraries.add(new Itinerary(itname, Workingdep, itsig, itsw));
				}
				s = br.readLine();
			}
			fr.close();
		}
		catch(Exception e)
		{
			try
			{
				fr.close();
			}
			catch(Exception ex)
			{
			}
		}
		for(TrackItem a : items)
		{
			for(TrackItem b : items)
			{
				if(a instanceof Junction)
				{
					Junction j = (Junction)a;
					if(b.connectsTo(j.Direction, a)) j.FrontItems[0] = b;
					if(b.connectsTo(j.Direction, a.x, a.y, j.Class == Position.Right ? -1 : 1)) j.FrontItems[1] = b;
					if(b.connectsTo(MainSignal.OppositeDir(j.Direction), a)) j.BackItem = b;
				}
				else
				{
					if(b.connectsTo(Orientation.Even, a)) a.EvenItem = b;
					if(b.connectsTo(Orientation.Odd, a)) a.OddItem = b;
				}
			}
		}
		for(TrackItem a : items)
		{
			if(a.SignalLinked!=null) a.SignalLinked.setAspect();
			if(a.CounterLinked!=null)
			{
				a.setCounters(Orientation.None);
				if(a.EvenItem!=null) a.EvenItem.setCounters(Orientation.Even);
				else
				{
					a.setSignal(new EoT(Orientation.Even, a.Station));
					a.Station.Signals.add(a.SignalLinked);
				}
				if(a.OddItem!=null) a.OddItem.setCounters(Orientation.Odd);
				else
				{
					a.setSignal(new EoT(Orientation.Odd, a.Station));
					a.Station.Signals.add(a.SignalLinked);
				}
			}
		}
	}
	String ReadParameter(String data)
	{
		String s = null;
		int End = 0;
		for(int i=0; i<data.length(); i++)
		{
			if(data.charAt(i)=='#')
			{
				End = i;
				break;
			}
			if(i+1==data.length())
			{
				End = i + 1;
				break;
			}
		}
		s = data.substring(data.charAt(0)=='$' || data.charAt(0)=='!' ? 1 : 0, End);
		return s;
	}
}
