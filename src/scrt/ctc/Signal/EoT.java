package scrt.ctc.Signal;

import scrt.Orientation;
import scrt.ctc.Station;
import scrt.event.SRCTEvent;

public class EoT extends FixedSignal {
	public EoT(Orientation dir, Station s)
	{
		super("", dir, Aspect.Parada, s);
	}
	@Override
	public void Lock()
	{
	}
	@Override
	public void Unlock()
	{
	}
	@Override
	public void actionPerformed(SRCTEvent e){}
	@Override
	public void setState()
	{
	}
}
