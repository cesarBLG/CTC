package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.gui.CTCIcon;

public class PacketManager
{
	public List<Packable> items = new ArrayList<Packable>();
	public void handlePacket(Packet p)
	{
		for(Packable i : items)
		{
			i.load(p);
		}
	}
}
