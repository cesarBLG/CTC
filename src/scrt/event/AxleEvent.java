package scrt.event;

import scrt.Orientation;
import scrt.ctc.AxleCounter;

public class AxleEvent extends SRCTEvent 
{
	public Orientation dir;
	public boolean second;
	public AxleEvent(AxleCounter ac, Orientation dir, boolean second) 
	{
		super(EventType.AxleCounter, ac);
		this.second = second;
		this.dir = dir;
	}

}
