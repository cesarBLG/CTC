package scrt.event;
import scrt.ctc.Aspect;
import scrt.ctc.Signal;

public class SignalEvent extends SRCTEvent {
	public Aspect prevAspect;
	public Aspect newAspect;
	public SignalEvent(Signal caller/*, Aspect p, Aspect n*/)
	{
		super(EventType.Signal, caller);
		/*prevAspect = p;
		newAspect = n;*/
	}
}
