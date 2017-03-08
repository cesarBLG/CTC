package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import main.Signal.Aspect;

public class Loader {
	List<TrackItem> items = new ArrayList<TrackItem>();
	List<Signal> signals = new ArrayList<Signal>();
	List<AxleCounter> counters = new ArrayList<AxleCounter>();
	Loader()
	{
		File layout = new File("layout.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(layout);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			while(s!=null)
			{
				if(s.charAt(0)=='$')
				{
					String name = ReadParameter(s);
					if(name.charAt(0)=='A')
					{
						br.readLine();
						br.readLine();
						br.readLine();
						String p = ReadParameter(br.readLine());
						String m = ReadParameter(br.readLine());
						Position pos = p.contains("Izquierda") ? Position.Left : Position.Right;
						int Number = Integer.parseInt(name.substring(1, 2));
						Junction j = new Junction(name, pos, Number%2==1 ? Orientation.Odd : Orientation.Even);
						if(m.contains("Desviada")) j.Muelle = 1;
						else if(m.contains("Directa")) j.Muelle = 0;
						else j.Muelle = -1;
						j.setSwitch(Position.Straight);
						items.add(j);
					}
					else
					{
						br.readLine();
						br.readLine();
						String sig = ReadParameter(br.readLine());
						String ac = ReadParameter(br.readLine());
						TrackItem t = new TrackItem(name);
						if(sig.charAt(0)!='0')
						{
							Signal Sig = new Signal(sig);
							t.SignalLinked = Sig;
							Sig.Linked = t;
							signals.add(Sig);
						}
						if(ac.charAt(0)!='0')
						{
							AxleCounter Ac = new AxleCounter(ac);
							t.CounterLinked = Ac;
							t.CounterDir = ac.charAt(2)=='1' ? Orientation.Even : Orientation.Odd;
							counters.add(Ac);
						}
						items.add(t);
					}
				}
				s = br.readLine();
			}
			fr = new FileReader(layout);
			br = new BufferedReader(fr);
			s = br.readLine();
			while(s!=null)
			{
				if(s.charAt(0)=='$')
				{
					String name = ReadParameter(s);
					TrackItem ti = null;
					for(TrackItem t : items)
					{
						if(t.Name.equals(name)) ti = t;
					}
					if(ti instanceof Junction)
					{
						Junction j = (Junction)ti;
						String backname = ReadParameter(br.readLine());
						for(TrackItem t : items)
						{
							if(t.Name.equals(backname))
							{
								j.BackItem = t;
							}
						}
						String front0name = ReadParameter(br.readLine());
						for(TrackItem t : items)
						{
							if(t.Name.equals(front0name))
							{
								j.FrontItems[0] = t;
							}
						}
						String front1name = ReadParameter(br.readLine());
						for(TrackItem t : items)
						{
							if(t.Name.equals(front1name))
							{
								j.FrontItems[1] = t;
							}
						}
					}
					else
					{
						String oddname = ReadParameter(br.readLine());
						for(TrackItem t : items)
						{
							if(t.Name.equals(oddname))
							{
								ti.OddItem = t;
							}
						}
						String evenname = ReadParameter(br.readLine());
						for(TrackItem t : items)
						{
							if(t.Name.equals(evenname))
							{
								ti.EvenItem = t;
							}
						}
					}
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
			if(!(a instanceof Junction) && a.CounterLinked!=null)
			{
				a.setCounters(Orientation.None);
				if(a.EvenItem!=null) a.EvenItem.setCounters(Orientation.Even);
				else a.SignalLinked = new FixedSignal(Orientation.Even, Aspect.Anuncio_parada);
				if(a.OddItem!=null) a.OddItem.setCounters(Orientation.Odd);
				else a.SignalLinked = new FixedSignal(Orientation.Odd, Aspect.Anuncio_parada);
			}
		}
	}
	String ReadParameter(String data)
	{
		String s = null;
		int End = 0;
		for(int i=0; i<data.length(); i++)
		{
			if(data.charAt(i)==' ')
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
		s = data.substring(data.charAt(0)=='$' ? 1 : 0, End);
		return s;
	}
}
