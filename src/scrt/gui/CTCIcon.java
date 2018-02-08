package scrt.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import scrt.ctc.CTCItem;
import scrt.ctc.packet.Packable;
import scrt.ctc.packet.Packet;
import scrt.ctc.packet.Packet.PacketType;
import scrt.ctc.packet.SignalData;
import scrt.ctc.Signal.Signal;
import scrt.ctc.Signal.SignalType;
import scrt.event.SRCTEvent;

public abstract class CTCIcon implements Packable {
	public abstract void update();
	static List<CTCIcon> items = new ArrayList<CTCIcon>();
	public Component comp;
	public static void handlePacket(Packet data)
	{
		for(CTCIcon i : items)
		{
			if(data.type == PacketType.Signal && i instanceof SignalIcon)
			{
				SignalIcon sig = (SignalIcon)i;
				SignalData state = (SignalData)data;
				if(state.equals(sig.getPacket()))
				{
					sig.load(data);
					return;
				}
			}
		}
		if(data.type == PacketType.Signal)
		{
			items.add(new SignalIcon((SignalData)data));
		}
	}
}
