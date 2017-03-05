package main;

import main.Signal.Aspect;

public class EoB extends TrackItem
{
	public EoB(String s)
	{
		super(s);
		SignalLinked = new FixedSignal(s.contains("V1/") ? Orientation.Odd : Orientation.Even, Aspect.Anuncio_parada);
		SignalLinked.Linked = this;
	}
}
