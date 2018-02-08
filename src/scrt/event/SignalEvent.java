package scrt.event;
import java.util.ArrayList;

import scrt.ctc.Signal.Signal;

public class SignalEvent extends SRCTEvent {
	public SignalEvent(Signal caller)
	{
		super(EventType.Signal, caller);
	}
	public byte[] getId()
	{
		Signal sig = (Signal)creator;
		ArrayList<Integer> data = new ArrayList<Integer>();
		data.add(0);
		data.add(sig.Class.ordinal());
		data.add(sig.Station.AssociatedNumber);
		data.add(sig.Track);
		data.add(sig.Number);
		data.add(sig.SignalAspect.ordinal() + (sig.Automatic ? 16 : 0));
		return getBytes(data);
	}
}
