package scrt.event;

import scrt.Orientation;
import scrt.ctc.AxleCounter;
import scrt.ctc.TrackItem;

public class AxleEvent extends SRCTEvent 
{
	public Orientation dir;
	public boolean release;
	public TrackItem previous;
	public AxleEvent(AxleCounter ac, Orientation dir, boolean release, TrackItem p) 
	{
		super(EventType.AxleCounter, ac);
		this.release = release;
		this.dir = dir;
		previous = p;
	}

}
