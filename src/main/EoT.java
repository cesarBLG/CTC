package main;

import main.Signal.Aspect;

public class EoT extends TrackItem
{
	public EoT(String s)
	{
		super(s);
		SignalLinked = new FixedSignal(s.contains("V1/") ? Orientation.Odd : Orientation.Even, Aspect.Parada);
		SignalLinked.Linked = this;
	}
}
