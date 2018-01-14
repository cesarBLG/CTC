package scrt.event;

import scrt.Orientation;
import scrt.ctc.TrackItem;

public class OccupationEvent extends SRCTEvent {
	public int Axles;
	public Orientation Direction;
	public OccupationEvent(TrackItem source, Orientation dir, int current)
	{
		super(EventType.Occupation, source);
		Axles = current;
		Direction = dir;
	}
}
