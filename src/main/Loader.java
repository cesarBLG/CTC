package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

public class Loader {
	List<TrackItem> items = new ArrayList<TrackItem>();
	List<Signal> signals = new ArrayList<Signal>();
	List<AxleCounter> counters = new ArrayList<AxleCounter>();
	Loader()
	{
		items.add(new Junction("A2/Arb", Position.Right, Orientation.Even));
		items.add(new Junction("A4/Arb", Position.Right, Orientation.Even));
		items.add(new EoB("PV1/1/Arb"));
		items.add(new EoB("PV1/2/Arb"));
		items.add(new EoT("PV1/3/Arb"));
		items.add(new TrackItem("V1/Arb"));
		items.add(new TrackItem("V2/Arb"));
		items.add(new TrackItem("V3/Arb"));
		items.add(new EoB("PV2/1/Arb"));
		items.add(new EoB("PV2/2/Arb"));
		items.add(new EoB("PV2/3/Arb"));
		items.add(new Junction("A1/Arb", Position.Left, Orientation.Odd));
		items.add(new Junction("A3/Arb", Position.Left, Orientation.Odd));
		items.add(new TrackItem("V1/Arb/CdM"));
		items.add(new Junction("A2/CdM", Position.Right, Orientation.Even));
		items.add(new TrackItem("PV1/1/CdM"));
		items.add(new TrackItem("V1/CdM"));
		items.add(new EoB("PV2/1/CdM"));
		items.add(new EoB("PV1/2/CdM"));
		signals.add(new Signal("S2/1/Arb"));
		signals.add(new Signal("S2/2/Arb"));
		signals.add(new Signal("S2/3/Arb"));
		signals.add(new Signal("S1/1/CdM"));
		counters.add(new AxleCounter("CV1/1/Arb"));
		counters.add(new AxleCounter("CV1/2/Arb"));
		counters.add(new AxleCounter("CV1/3/Arb"));
		counters.add(new AxleCounter("CV2/1/Arb"));
		counters.add(new AxleCounter("CV2/2/Arb"));
		counters.add(new AxleCounter("CV2/3/Arb"));
		counters.add(new AxleCounter("CV1/1/CdM"));
		counters.add(new AxleCounter("CV2/1/CdM"));
		for(TrackItem a : items)
		{
			if(a.Name.contains("PV"))
			{
				String c = "S";
				c = c.concat(a.Name.substring(2));
				for(Signal b : signals)
				{
					if(b.Name.equals(c))
					{
						b.Linked = a;
						a.SignalLinked = b;
					}
				}
				c = "CV";
				c = c.concat(a.Name.substring(2));
				for(AxleCounter b : counters)
				{
					if(b.Name.equals(c))
					{
						a.CounterLinked = b;
						a.CounterDir = b.Name.charAt(2) == '1' ? Orientation.Even : Orientation.Odd;
					}
				}
				c = "V";
				c = c.concat(a.Name.substring(4));
				for(TrackItem b : items)
				{
					if(b.Name.equals(c))
					{
						if(a.Name.charAt(2)=='1')
						{
							a.EvenItem = b;
							b.OddItem = a;
						}
						else
						{
							b.EvenItem = a;
							a.OddItem = b;
						}
					}
				}
				boolean Assigned = false;
				c = "A";
				Integer JunctionNumber = (Integer.parseInt(a.Name.substring(4, 5)) - 1) * 2 + (a.Name.charAt(2) == '1' ? 2 : 1);
				c = c.concat(JunctionNumber.toString());
				c = c.concat(a.Name.substring(5));
				for(TrackItem b : items)
				{
					if(b.Name.equals(c))
					{
						((Junction)b).FrontItems[0] = a;
						if(a.Name.charAt(2)=='1') a.OddItem = b;
						else a.EvenItem = b;
						Assigned = true;
					}
				}
				if(!Assigned)
				{
					c = "A";
					JunctionNumber -= 2;
					if(JunctionNumber == 0)
					{
						for(TrackItem b : items)
						{
							if(b.Name.charAt(0)=='V'&&b.Name.contains("Arb/CdM")&&((a.Name.contains("Arb")&&a.Name.charAt(2)=='2')||(a.Name.contains("CdM")&&a.Name.charAt(2)=='1')))
							{
								if(a.Name.charAt(2)=='1')
								{
									a.OddItem = b;
									b.EvenItem = a;
								}
								else
								{
									a.EvenItem = b;
									b.OddItem = a;
								}
							}
						}
					}
					else
					{
						c = c.concat(JunctionNumber.toString());
						c = c.concat(a.Name.substring(5));
						for(TrackItem b : items)
						{
							if(b.Name.equals(c))
							{
								((Junction)b).FrontItems[1] = a;
								if(a.Name.charAt(2)=='1') a.OddItem = b;
								else a.EvenItem = b;
								Assigned = true;
							}
						}
					}
				}
			}
			if(a.Name.charAt(0)=='A')
			{
				Integer Number = Integer.parseInt(a.Name.substring(1, 2));
				Number -= 2;
				if(Number <= 0)
				{
					for(TrackItem b : items)
					{
						if(b.Name.charAt(0)=='V'&&b.Name.contains("Arb/CdM")&&((a.Name.contains("Arb")&&a.Name.charAt(1)=='1')||(a.Name.contains("CdM")&&a.Name.charAt(1)=='2')))
						{
							if(Integer.parseInt(a.Name.substring(1, 2))%2 == 1)
							{
								((Junction)a).BackItem = b;
								b.OddItem = a;
							}
							else
							{
								((Junction)a).BackItem = b;
								b.EvenItem = a;
							}
						}
					}
				}
				else
				{
					String c = "A";
					c = c.concat(Number.toString());
					c = c.concat(a.Name.substring(2));
					for(TrackItem b : items)
					{
						if(b.Name.equals(c))
						{
							((Junction)b).FrontItems[1] = a;
							((Junction)a).BackItem = b;
						}
					}
				}
			}
			if(a.Name.charAt(0)=='V')
			{
				String c = "PV1/";
				c = c.concat(a.Name.substring(1));
				for(TrackItem b : items)
				{
					if(b.Name.equals(c))
					{
						a.OddItem = b;
						b.EvenItem = a;
					}
				}
				c = "PV2/";
				c = c.concat(a.Name.substring(1));
				for(TrackItem b : items)
				{
					if(b.Name.equals(c))
					{
						a.EvenItem = b;
						b.OddItem = a;
					}
				}
			}
			/*if(a instanceof Junction)
			{
				try
				{
					JOptionPane.showMessageDialog(null, ((Junction)a).BackItem.Name.concat(a.Name.concat(((Junction)a).FrontItems[0].Name.concat(((Junction)a).FrontItems[1].Name))));
				}
				catch(Exception e){}
			}
			else
			{
				try
				{
					JOptionPane.showMessageDialog(null, a.EvenItem.Name.concat(a.Name.concat(a.OddItem.Name)));
				}
				catch(Exception e){}
			}*/
		}
		for(TrackItem a : items)
		{
			if(a.Name == "A2/CdM")
			{
				((Junction)a).Muelle = 1;
			}
			if(a.CounterLinked!=null)
			{
				a.setCounters(Orientation.None);
				if(a.EvenItem!=null) a.EvenItem.setCounters(Orientation.Even);
				if(a.OddItem!=null) a.OddItem.setCounters(Orientation.Odd);
			}
		}
		Collections.reverse(items);
		/*for(TrackItem a : items)
		{
			a.setCounters(Orientation.None);
		}*/
	}
}
