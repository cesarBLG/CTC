/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import scrt.ctc.Junction.Position;
import scrt.ctc.Signal.EoT;
import scrt.ctc.Signal.Signal;
import scrt.train.Train;

public class Loader {
	public static List<TrackItem> items = new ArrayList<>();
	public static List<Signal> signals = new ArrayList<>();
	public static List<AxleCounter> counters = new ArrayList<>();
	public static List<Itinerary> itineraries = new ArrayList<>();
	public static List<Station> stations = new ArrayList<>();
	public static List<Train> trains = new ArrayList<>();
	public static CTCThread ctcThread = new CTCThread();
	public static void load()
	{
		//Config.set("FCD");
		load(parseLayoutFile());
	}
	public static List<Packet> parseLayoutFile()
	{
		File layout = new File("layout.bin");
		if(!layout.exists()) layout = new File("layout.txt");
		ArrayList<Packet> packets = new ArrayList<>();
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
						StationRegister reg = new StationRegister(Station.getNumber(name));
						reg.name = full;
						reg.shortName = name;
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
							JunctionID id = new JunctionID();
							id.Number = Integer.parseInt(n.substring(1));
							id.Name = n;
							id.stationNumber = Workingdep;
							TrackItemID tid = new TrackItemID();
							tid.x = x;
							tid.y = y;
							tid.stationNumber = Workingdep;
							JunctionRegister reg = new JunctionRegister(id, tid);
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
							TrackItemID id = new TrackItemID();
							id.x = x;
							id.y = y;
							id.stationNumber = Workingdep;
							TrackRegister reg = new TrackRegister(id);
							reg.EvenRotation = e;
							reg.OddRotation = o;
							reg.Name = n;
							packets.add(reg);
							if(sig.charAt(0)!='0')
							{
								SignalID sigid = new SignalID(sig, id.stationNumber);
								SignalRegister sigreg = new SignalRegister(sigid);
								sigreg.Fixed = sig.charAt(0)=='F';
								packets.add(sigreg);
								LinkPacket link = new LinkPacket(id, sigid);
								packets.add(link);
							}
							if(ac.charAt(0)!='0')
							{
								int num = Integer.parseInt(ac);
								ACID acid = new ACID();
								acid.dir = num % 2 == 0 ? Orientation.Even : Orientation.Odd;
								acid.Num = num;
								acid.stationNumber = Workingdep;
								LinkPacket link = new LinkPacket(id, acid);
								packets.add(link);
							}
						}
					}
					if(s.charAt(0)=='!')
					{
						String itname = ReadParameter(s);
						Hashtable<Integer, Integer> itsw = new Hashtable<>();
						List<String> itsig = new ArrayList<>();
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
						itineraries.add(new Itinerary(itname, Station.byNumber(Workingdep), itsig, itsw));
					}
					s = br.readLine();
				}
				br.close();
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
	public static void load(List<Packet> packets)
	{
		COM.initialize();
		for(Packet p : packets)
		{
			if(p instanceof StationRegister)
			{
				stations.add(new Station((StationRegister)p));
			}
		}
		for(Packet p : packets)
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
		resolveLinks();
		for(Packet p : packets)
		{
			if(p instanceof LinkPacket) CTCItem.PacketManager.handlePacket(p);
		}
		for(TrackItem a : items)
		{
			if(a instanceof Junction) continue;
			if(a.SignalLinked!=null) a.SignalLinked.setAspect();
			if(a.getNext(Orientation.Even) == null)
			{
				if(a.CounterLinked != null)
				{
					new EoT(Orientation.Even, a.Station).setLinked(a);
					signals.add(a.SignalLinked);
					a.Station.Signals.add(a.SignalLinked);
				}
				else System.err.println("Error: Final de vía sin contador asociado en " + a.x + ", " + a.y);
			}
			if(a.getNext(Orientation.Odd) == null)
			{
				if(a.CounterLinked != null)
				{
					new EoT(Orientation.Odd, a.Station).setLinked(a);
					signals.add(a.SignalLinked);
					a.Station.Signals.add(a.SignalLinked);
				}
				else System.err.println("Error: Final de vía sin contador asociado en " + a.x + ", " + a.y);
			}
		}
		ctcThread.start();
	}
	public static void resolveLinks()
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
