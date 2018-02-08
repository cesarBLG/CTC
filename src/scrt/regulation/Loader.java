package scrt.regulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import scrt.FunctionalList;
import scrt.Orientation;
import scrt.gui.TrafficGraph;
import scrt.regulation.train.Train;

public class Loader
{
	public static TrafficGraph g = null;
	public static List<Train> t = new ArrayList<Train>();
	public Loader()
	{
		Place cen = new Place("Central");
		List<Track> tr = new ArrayList<Track>();
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		tr.add(new Track(cen, 100));
		cen.tracks = new FunctionalList<Track>(tr);
		cen.isPP = true;
		cen.PK = 0;
		cen.secondPK = -1000;
		//cen.Length = 100;
		cen.maxSpeed = 10;
		Place tmb = new Place("Tomás Bretón");
		tr = new ArrayList<Track>();
		tr.add(new Track(tmb, 50));
		tr.add(new Track(tmb, 50));
		tmb.isPP = true;
		tmb.PK = -300;
		//tmb.Length = 50;
		tmb.maxSpeed = 10;
		tmb.tracks = new FunctionalList<Track>(tr);
		Place.IntermediatePlace(cen, tmb, 1);
		Place arb = new Place("Arb");
		tr = new ArrayList<Track>();
		tr.add(new Track(arb, 80));
		tr.add(new Track(arb, 80));
		tr.add(new Track(arb, 80));
		arb.tracks = new FunctionalList<Track>(tr);
		arb.isPP = true;
		arb.PK = -600;
		//arb.Length = 80;
		arb.maxSpeed = 10;
		Place.IntermediatePlace(tmb, arb, 1);
		Place cdm = new Place("C. de Madera");
		tr = new ArrayList<Track>();
		tr.add(new Track(cdm, 200));
		tr.add(new Track(cdm, 200));
		cdm.isPP = true;
		cdm.PK = -700;
		//tmb.Length = 50;
		cdm.maxSpeed = 10;
		cdm.tracks = new FunctionalList<Track>(tr);
		Place.IntermediatePlace(arb, cdm, 1);
		Place.IntermediatePlace(cdm, cen, 2);
		List<Place> p = new ArrayList<Place>();
		p.add(cen);
		p.add(tmb);
		p.add(arb);
		p.add(cdm);
		Train t1 = new Train(1);
		Train t2 = new Train(2);
		Train t3 = new Train(3);
		Train t4 = new Train(4);
		Train t5 = new Train(5);
		Train t6 = new Train(6);
		Train t7 = new Train(7);
		Train t8 = new Train(8);
		t1.timetable.set(cen, cen);
		t2.timetable.set(cen, cen);
		t3.timetable.set(cen, cen);
		t4.timetable.set(cen, cen);
		t5.timetable.set(cen, cen);
		t6.timetable.set(cen, cen);
		t7.timetable.set(cen, cen);
		t8.timetable.set(cen, cen);
		t1.timetable.entries.get(0).setStop(0);
		t2.timetable.entries.get(0).setStop(40);
		t3.timetable.entries.get(0).setStop(30);
		t4.timetable.entries.get(0).setStop(70);
		t5.timetable.entries.get(0).setStop(60);
		t6.timetable.entries.get(0).setStop(60);
		t7.timetable.entries.get(0).setStop(90);
		t8.timetable.entries.get(0).setStop(90);
		t.add(t1);
		t.add(t2);
		t.add(t3);
		t.add(t4);
		t.add(t5);
		t.add(t6);
		t.add(t7);
		t.add(t8);
		g = new TrafficGraph(p);
		for(Train train: t)
		{
			train.timetable.validate();
		}
		g.updateData(t);
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
