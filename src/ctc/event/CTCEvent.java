package ctc.event;

import ctc.*;

public class CTCEvent{
	public enum SignalEventType
	{
		AxleCounter_1,
		AxleCounter_2,
		Signal,
		Train
	}
	Orientation direction;
	Object creator;
}
