package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Loader;
import scrt.ctc.Station;

public class GRPManager {
	public List<GRP> GRPs = new ArrayList<GRP>();
	Loader l;
	public GRPManager(Loader l)
	{
		this.l = l;
		for(Station s : l.stations)
		{
			if(!s.isOpen()) continue;
			GRPs.add(new GRP(s));
		}
	}
}
