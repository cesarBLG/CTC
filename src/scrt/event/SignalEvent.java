package scrt.event;
import scrt.ctc.Signal.Signal;

public class SignalEvent extends SRCTEvent {
	public SignalEvent(Signal caller)
	{
		super(EventType.Signal, caller);
	}
}
