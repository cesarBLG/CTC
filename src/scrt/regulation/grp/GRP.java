package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;

import scrt.FunctionalList;
import scrt.ctc.Station;
import scrt.train.Train;

public class GRP {
	public Station station;
	public boolean Activated = false;
	public FunctionalList<GRPRule> rules = new FunctionalList<GRPRule>();
	List<Train> trains = new ArrayList<Train>();
	public GRP(Station s) 
	{
		station = s;
		s.grp = this;
		rules.add(new GRPRule());
		rules.add(new GRPRule());
		//trains.add(new Train(27001));
	}
	public void update()
	{
		trains.sort((t1, t2) -> 
		{
			t1.setPriority();
			t2.setPriority();
			return new Integer(t1.Priority).compareTo(t2.Priority);
		});
		for(GRPRule rule : rules)
		{
			for(Train t : trains)
			{
				if(rule.set(t)) return;
			}
		}
	}
}
