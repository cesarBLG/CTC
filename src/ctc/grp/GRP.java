package ctc.grp;

import java.util.ArrayList;
import java.util.List;

import ctc.*;
import ctc.train.Train;

public abstract class GRP {
	public Station station;
	public boolean Activated = false;
	public List<Train> monitoringTrains = new ArrayList<Train>();
	public abstract void update();
}
