package scrt.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import scrt.ctc.CTCItem;
import scrt.ctc.packet.Packable;
import scrt.ctc.packet.Packet;
import scrt.ctc.packet.PacketManager;
import scrt.ctc.packet.PacketType;
import scrt.ctc.packet.SignalData;
import scrt.ctc.packet.SignalRegister;
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
							return;
						}
					}
					if(p.id.type == PacketType.Signal && p instanceof SignalRegister)
					{
						CTCIcon.items.add(new SignalIcon((SignalRegister)p));
					}
				}
			};
}
