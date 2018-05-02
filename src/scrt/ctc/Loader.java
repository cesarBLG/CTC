package scrt.ctc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.ACID;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.com.packet.StationRegister;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.ctc.Signal.EoT;
import scrt.ctc.Signal.Signal;
import scrt.gui.GUI;
import scrt.regulation.grp.GRPManager;

public class Loader {
	public List<TrackItem> items = new ArrayList<TrackItem>();
	public List<Signal> signals = new ArrayList<Signal>();
	public List<AxleCounter> counters = new ArrayList<AxleCounter>();
	public List<Itinerary> itineraries = new ArrayList<Itinerary>();
	public List<Station> stations = new ArrayList<Station>();
	public GRPManager grpManager;
	public Loader()
	{
		load(parseLayoutFile());
		grpManager = new GRPManager(this);
		new GUI(this);
	}
	public static List<Packet> parseLayoutFile()
	{
		File layout = new File("layout.bin");
		if(!layout.exists()) layout = new File("layout.txt");
		var packets = new ArrayList<Packet>();
		FileReader fr = null;
		try {
			fr = new FileReader(layout);
			if(layout.getName().contains("bin"))
			{
				FileInputStream is = new FileInputStream(layout);
				while(is.available()>0)
				{
					packets.add(Packet.byState(is));
				}
			}
			else
			{
				BufferedReader br = new BufferedReader(fr);
				String s = br.readLine();
				int Workingdep = 0;
				while(s!=null)
				{
					if(s.startsWith("["))
					{
						String full = s.substring(s.indexOf('[') + 1, s.indexOf(']'));
						String name = s.substring(s.indexOf(']')+2, s.indexOf(']')+5);
						var reg = new StationRegister();
						reg.name = full;
						reg.shortName = name;
						reg.associatedNumber = Station.getNumber(name);
						Workingdep = reg.associatedNumber;
						boolean exists = false;
						for(Packet p : packets)
						{
							if(p instanceof StationRegister && ((StationRegister) p).associatedNumber == Workingdep) exists = true;
						}
						if(!exists) packets.add(reg);
					}
					if(s.charAt(0)=='$')
					{
						String n = ReadParameter(s);
						int Number = Integer.parseInt(ReadParameter(br.readLine()));
						String coordinates = ReadParameter(br.readLine());
						int x = Integer.parseInt(coordinates.substring(0, coordinates.indexOf(',')));
						int y = Integer.parseInt(coordinates.substring(coordinates.indexOf(',')+1));
						if(Number>6)
						{
							var id = new JunctionID();
							id.Number = Integer.parseInt(n.substring(1));
							id.Name = n;
							id.stationNumber = Workingdep;
							var tid = new TrackItemID();
							tid.x = x;
							tid.y = y;
							tid.stationNumber = Workingdep;
							var reg = new JunctionRegister(id, tid);
							reg.Class = Number == 7 ? Position.Left : Position.Right;
							reg.Direction = id.Number % 2 == 0 ? Orientation.Even : Orientation.Odd;
							packets.add(reg);
							/*String m = */ReadParameter(br.readLine());
							/*if(m.contains("Desviada")) j.Muelle = 1;
							else if(m.contains("Directa")) j.Muelle = 0;
							else j.Muelle = -1;
							j.updatePosition(Position.Straight);*/
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
							var id = new TrackItemID();
							id.x = x;
							id.y = y;
							id.stationNumber = Workingdep;
							var reg = new TrackRegister(id);
							reg.EvenRotation = e;
							reg.OddRotation = o;
							reg.Name = n;
							packets.add(reg);
							if(sig.charAt(0)!='0')
							{
								var sigid = new SignalID(sig, id.stationNumber);
								var sigreg = new SignalRegister(sigid);
								sigreg.Fixed = sig.charAt(0)=='F';
								packets.add(sigreg);
								var link = new LinkPacket(id, sigid);
								packets.add(link);
							}
							if(ac.charAt(0)!='0')
							{
								int num = Integer.parseInt(ac);
								var acid = new ACID();
								acid.dir = num % 2 == 0 ? Orientation.Even : Orientation.Odd;
								acid.Num = num;
								acid.stationNumber = Workingdep;
								var link = new LinkPacket(id, acid);
								packets.add(link);
							}
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
						//itineraries.add(new Itinerary(itname, Station.byNumber(Workingdep), itsig, itsw));
					}
					s = br.readLine();
				}
			}
			fr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				fr.close();
			}
			catch(Exception ex)
			{
			}
		}
		return packets;
	}
	public void load(List<Packet> packets)
	{
		COM.initialize();
		for(var p : packets)
		{
			if(p instanceof StationRegister)
			{
				stations.add(new Station((StationRegister)p));
			}
		}
		for(var p : packets)
		{
			if(p instanceof TrackRegister)
			{
				items.add(new TrackItem((TrackRegister) p));
			}
			if(p instanceof JunctionRegister)
			{
				items.add(new Junction((JunctionRegister) p));
			}
			if(p instanceof SignalRegister)
			{
				signals.add(Signal.construct((SignalRegister) p));
			}
		}
		for(var p : packets)
		{
			if(p instanceof LinkPacket)
			{
				var link = (LinkPacket)p;
				if(link.id1 instanceof TrackItemID)
				{
					if(link.id2 instanceof SignalID) ((Signal)CTCItem.findId(link.id2)).setLinked((TrackItem)CTCItem.findId(link.id1));
					if(link.id2 instanceof ACID)
					{
						AxleCounter ac = (AxleCounter) CTCItem.findId(link.id2);
						if(ac == null)
						{
							ac = new AxleCounter((ACID)link.id2);
							counters.add(ac);
						}
						((TrackItem)CTCItem.findId(link.id1)).setCounterLinked(ac, ac.Number % 2 == 0 ? Orientation.Even : Orientation.Odd);
					}
				}
			}
		}
		resolveLinks();
	}
	public void resolveLinks()
	{
		for(TrackItem a : items)
		{
			for(TrackItem b : items)
			{
				if(a instanceof Junction)
				{
					Junction j = (Junction)a;
					if(b instanceof Junction)
					{
						Junction k = (Junction)b;
						if(j.Direction != k.Direction)
						{
							if(j.x + (j.Direction == Orientation.Even ? 1 : -1) == k.x)
							{
								if(j.Class == k.Class && j.y + (j.Class == Position.Left ? -1 : 1)*(j.Direction == Orientation.Even ? 1 : -1) == k.y)
								{
									j.FrontItems[1] = k;
									k.FrontItems[1] = j;
									j.Linked = k;
									k.Linked = j;
								}
								else if(j.Class != k.Class && j.y == k.y)
								{
									j.FrontItems[0] = k;
									k.FrontItems[0] = j;
								}
								else
								{
									if(b.connectsTo(j.Direction, a)) j.FrontItems[0] = b;
									if(b.connectsTo(j.Direction, a.x, a.y, j.Class == Position.Right ? -1 : 1)) j.FrontItems[1] = b;
									if(b.connectsTo(Orientation.OppositeDir(j.Direction), a)) j.BackItem = b;
								}
							}
							else
							{
								if(b.connectsTo(j.Direction, a)) j.FrontItems[0] = b;
								if(b.connectsTo(j.Direction, a.x, a.y, j.Class == Position.Right ? -1 : 1)) j.FrontItems[1] = b;
								if(b.connectsTo(Orientation.OppositeDir(j.Direction), a)) j.BackItem = b;
							}
						}
						else
						{
							if(b.connectsTo(j.Direction, a)) j.FrontItems[0] = b;
							if(b.connectsTo(j.Direction, a.x, a.y, j.Class == Position.Right ? -1 : 1)) j.FrontItems[1] = b;
							if(b.connectsTo(Orientation.OppositeDir(j.Direction), a)) j.BackItem = b;
							if(a.x == b.x && a.y + 1 == b.y && j.Class == Position.Right && k.Class == Position.Left)
							{
								j.CrossingLinked = k;
								k.CrossingLinked = j;
							}
						}
					}
					else
					{
						if(b.connectsTo(j.Direction, a)) j.FrontItems[0] = b;
						if(b.connectsTo(j.Direction, a.x, a.y, j.Class == Position.Right ? -1 : 1)) j.FrontItems[1] = b;
						if(b.connectsTo(Orientation.OppositeDir(j.Direction), a)) j.BackItem = b;
					}
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
					new EoT(Orientation.Even, a.Station).setLinked(a);
					signals.add(a.SignalLinked);
					a.Station.Signals.add(a.SignalLinked);
				}
				if(a.OddItem!=null) a.OddItem.setCounters(Orientation.Odd);
				else
				{
					new EoT(Orientation.Odd, a.Station).setLinked(a);
					signals.add(a.SignalLinked);
					a.Station.Signals.add(a.SignalLinked);
				}
			}
		}
	}
	static String ReadParameter(String data)
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
