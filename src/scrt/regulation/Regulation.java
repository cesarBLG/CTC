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
package scrt.regulation;

import java.util.ArrayList;
import java.util.List;

import scrt.FunctionalList;
import scrt.ctc.Station;
import scrt.gui.TrafficGraph;
import scrt.regulation.timetable.Timetable;

public class Regulation
{
	public static TrafficGraph g = null;
	public static FunctionalList<Timetable> services = new FunctionalList<>();
	public static List<Place> p = new ArrayList<>();
	public static void load()
	{
		Place cen = new Place(Station.byName("Cen"));
		List<Track> tr = new ArrayList<>();
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		cen.tracks = new FunctionalList<>(tr);
		cen.isPP = true;
		cen.PK = 0;
		cen.secondPK = -1000;
		cen.Length = 100;
		cen.maxSpeed = 10;
		Place tmb = new Place(Station.byName("TmB"));
		tr = new ArrayList<>();
		tr.add(new Track(tmb, 50));
		tr.add(new Track(tmb, 50));
		tmb.isPP = true;
		tmb.PK = -300;
		tmb.Length = 50;
		tmb.maxSpeed = 10;
		tmb.tracks = new FunctionalList<>(tr);
		Place.IntermediatePlace(cen, tmb, 1);
		Place arb = new Place(Station.byName("Arb"));
		tr = new ArrayList<>();
		tr.add(new Track(arb, 80));
		tr.add(new Track(arb, 80));
		tr.add(new Track(arb, 80));
		arb.tracks = new FunctionalList<>(tr);
		arb.isPP = true;
		arb.PK = -600;
		arb.Length = 80;
		arb.maxSpeed = 10;
		Place.IntermediatePlace(tmb, arb, 1);
		Place cdm = new Place(Station.byName("CdM"));
		tr = new ArrayList<>();
		tr.add(new Track(cdm, 200));
		tr.add(new Track(cdm, 200));
		cdm.isPP = true;
		cdm.PK = -700;
		tmb.Length = 50;
		cdm.maxSpeed = 10;
		cdm.tracks = new FunctionalList<>(tr);
		Place.IntermediatePlace(arb, cdm, 1);
		Place.IntermediatePlace(cdm, cen, 2);
		p.add(cen);
		p.add(tmb);
		p.add(arb);
		p.add(cdm);
		Timetable t1 = new Timetable(1);
		Timetable t2 = new Timetable(2);
		Timetable t3 = new Timetable(3);
		Timetable t4 = new Timetable(4);
		Timetable t5 = new Timetable(5);
		Timetable t6 = new Timetable(6);
		Timetable t7 = new Timetable(7);
		Timetable t8 = new Timetable(8);
		t1.set(cen, cen);
		t2.set(cen, cen);
		t3.set(cen, cen);
		t4.set(cen, cen);
		t5.set(cen, cen);
		t6.set(cen, cen);
		t7.set(cen, cen);
		t8.set(cen, cen);
		t1.entries.get(0).setStop(0);
		t2.entries.get(0).setStop(40);
		t3.entries.get(0).setStop(30);
		t4.entries.get(0).setStop(70);
		t5.entries.get(0).setStop(60);
		t6.entries.get(0).setStop(60);
		t7.entries.get(0).setStop(90);
		t8.entries.get(0).setStop(90);
		services.add(t1);
		services.add(t2);
		services.add(t3);
		services.add(t4);
		services.add(t5);
		services.add(t6);
		services.add(t7);
		services.add(t8);
		/*FileReader fr = null;
		try {
			fr = new FileReader(new File("Places.txt"));
			BufferedReader br = new BufferedReader(fr);
			while(true)
			{
				String s = br.readLine();
				if(s.contains("["))
				{
					s.split("[");
					s.s
				}
			}
		}
		catch(Exception e)
		{
			
		}*/
	}
}
