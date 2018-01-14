package scrt.event;

import scrt.Orientation;
import scrt.ctc.TrackItem;

public class BlockEvent extends SRCTEvent {
	public Orientation BlockState;
	public Orientation prevState;
	public BlockEvent(TrackItem item, Orientation state)
	{
		super(EventType.Block, item);
		BlockState = state;
	}
}
