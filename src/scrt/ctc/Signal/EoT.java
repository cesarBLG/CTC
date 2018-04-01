package scrt.ctc.Signal;

import scrt.Orientation;
import scrt.ctc.Station;
import scrt.event.SRCTEvent;

public class EoT extends FixedSignal {
	static int evenCount = 50;
	static int oddCount = 51;
	public EoT(Orientation dir, Station s)
	{
		super("F" + String.valueOf(dir == Orientation.Even ? (evenCount+=2) : (oddCount+=2)) + "/1", dir, Aspect.Parada, s);
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
	public void setState()
	{
	}
	@Override
	public void actionPerformed(SRCTEvent e)
	{
	}
}
