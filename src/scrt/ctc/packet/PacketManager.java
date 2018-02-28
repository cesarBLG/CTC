package scrt.ctc.packet;

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
			if(p.equals(i.getPacket()))
			{
				i.load(p);
				return;
			}
		}
	}
}
