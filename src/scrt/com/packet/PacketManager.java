package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketManager
{
	public List<Packable> items = new ArrayList<Packable>();
	public void handlePacket(Packet p)
	{
		var l = new ArrayList<Packable>();
		l.addAll(items);
		for(Packable i : l)
		{
			i.load(p);
		}
	}
}
