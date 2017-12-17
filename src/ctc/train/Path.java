package ctc.train;

import java.util.ArrayList;
import java.util.List;

import ctc.Station;
import ctc.TrackItem;

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
