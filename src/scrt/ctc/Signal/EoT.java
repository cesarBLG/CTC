package scrt.ctc.Signal;

import scrt.Orientation;
import scrt.com.packet.Packet.PacketType;
import scrt.ctc.Station;
import scrt.event.SRCTEvent;

public class EoT extends FixedSignal {
	static int count = 50;
	public EoT(Orientation dir, Station s)
	{
		super("F" + String.valueOf(count++) + "/1", dir, Aspect.Parada, s);
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
