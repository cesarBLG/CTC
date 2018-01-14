package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import scrt.FunctionalList;
import scrt.Orientation;
import scrt.ctc.TrackItem;
import scrt.event.UserEvent;
import scrt.regulation.train.Train;
import scrt.regulation.train.Train.TrainClass;

public class GRPRule {
	List<UserEvent> actions = new ArrayList<UserEvent>();
	public FunctionalList<TrackItem> trainsAt = new FunctionalList<TrackItem>();
	public FunctionalList<TrainClass> trainClasses = new FunctionalList<TrainClass>();
	public Orientation trainParity = Orientation.Odd;
	public GRPRule()
	{
		
	}
	public boolean set(Train t)
	{
		if(trainClasses.contains(t.Class)) return true;
		return false;
	}
	@Override
	public String toString()
	{
		return "adfd";
	}
}
