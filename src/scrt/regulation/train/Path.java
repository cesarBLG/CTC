package scrt.regulation.train;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Station;
import scrt.ctc.TrackItem;

public class Path {
	public List<Station> stations = new ArrayList<Station>();
	public Path(List<TrackItem> items)
	{
		for(TrackItem t : items)
		{
			if(!stations.contains(t.Station)) stations.add(t.Station);
		}
	}
}
