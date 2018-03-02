package scrt.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import scrt.com.packet.ID;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.PacketManager;
import scrt.com.packet.PacketType;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalRegister;
import scrt.com.packet.TrackRegister;
import scrt.ctc.CTCItem;
import scrt.ctc.Signal.Signal;
import scrt.ctc.Signal.SignalType;
import scrt.event.SRCTEvent;

public abstract class CTCIcon implements Packable {
	public abstract void update();
	static List<CTCIcon> items = new ArrayList<CTCIcon>();
	public Component comp;
	public static PacketManager PacketManager = new PacketManager()
			{
				@Override
				public void handlePacket(Packet p)
				{
					for(CTCIcon i : CTCIcon.items)
					{
						if(p.equals(i.getId()))
						{
							i.load(p);
						}
					}
					if(p instanceof SignalRegister)
					{
						CTCIcon.items.add(new SignalIcon((SignalRegister)p));
					}
					if(p instanceof TrackRegister)
					{
						CTCIcon.items.add(new TrackIcon((TrackRegister)p));
					}
				}
			};
	public static CTCIcon findId(ID id)
	{
		for(CTCIcon i : CTCIcon.items)
		{
			if(id.equals(i.getId())) return i;
		}
		return null;
	}
}
