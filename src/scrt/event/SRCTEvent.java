package scrt.event;

public class SRCTEvent{
	public EventType type;
	public Object creator;
	public SRCTEvent(EventType t, Object c)
	{
		type = t;
		creator = c;
	}
}
