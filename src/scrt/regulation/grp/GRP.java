package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import scrt.*;
import scrt.ctc.Station;
import scrt.event.SRCTEvent;
import scrt.regulation.train.Train;
import scrt.regulation.train.Train.TrainClass;

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
		trains.add(new Train(27001));
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
