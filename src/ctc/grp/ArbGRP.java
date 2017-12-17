package ctc.grp;

import java.util.Comparator;

import ctc.*;
import ctc.train.*;
import ctc.train.Train.TrainClass;

public class ArbGRP extends GRP 
{
	@Override
	public void update()
	{
		monitoringTrains.forEach(x -> x.setPriority());
		monitoringTrains.sort(new Comparator<Train>(){
			@Override
			public int compare(Train t1, Train t2) {
				// TODO Auto-generated method stub
				if(t1 == t2) return 0; 
				if(t1 == null) return -1;
				if(t2 == null) return 1;
				if(t1.Priority > t2.Priority) return 1;
				if(t1.Priority < t2.Priority) return -1;
				return 0;
			}
			
		});
		for(Train t : monitoringTrains)
		{
			StationStop stop = null;
			for(StationStop s : t.timetable.stop.keySet()){if(s.station == station) stop = s;}
			if(stop == null) return;
			if(t.TimeStopped == 0 && (t.path==null || !t.path.stations.contains(station)))
			{
				if(stop.type != StationStop.StopType.No_stop)
				{
					for(Engine e : t.Engines) 
					{
						if(e.NeedsWater) ;//Entrada vía 3
					}
					if(t.EoT && t.Direction == Orientation.Odd)
					{
						if(t.Length < 1.5f) ;//Culatón
						//Paso directo a losilla
					}
					if(t.Direction == Orientation.Even)
					{
						//Entrada vía 1
						//Entrada vía 3
						//Entrada vía 2
					}
					else
					{
						//Entrada vía 2
						//Entrada vía 3
						//Entrada vía 1
					}
				}
				else
				{
					if(t.Direction == Orientation.Even)
					{
						//Paso directo vía 2
						//Paso directo vía 3
						//Paso directo vía 1
						//Entrada vía 2
						//Entrada vía 3
						//Entrada vía 1
					}
					else
					{
						//Paso directo vía 1
						//Paso directo vía 3
						//Paso directo vía 2
						//Entrada vía 1
						//Entrada vía 3
						//Entrada vía 2
					}
				}
			}
			else if(t.path == null && stop.stopTime != 0 && (stop.type == StationStop.StopType.No_stop || stop.stopTime<t.TimeStopped))
			{
				if(t.Direction == Orientation.Even)
				{
					//Calcular itinerario de salida
				}
				else
				{
					//Calcular itinerario de salida
				}
			}
		}
	}
}
