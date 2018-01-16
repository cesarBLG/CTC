package scrt.ctc;

import scrt.Orientation;
import scrt.event.SRCTEvent;

public class EoT extends FixedSignal {
	public EoT(Orientation dir, Station s)
	{
		super(dir, Aspect.Parada, s);
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
