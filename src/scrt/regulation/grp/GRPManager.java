package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Loader;
import scrt.ctc.Station;

public class GRPManager {
	public static List<GRP> GRPs = new ArrayList<GRP>();
	public static void start()
	{
		for(Station s : Loader.stations)
		{
			if(!s.isOpen()) continue;
			GRPs.add(new GRP(s));
		}
	}
}
