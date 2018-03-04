package scrt.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.com.packet.ID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.com.packet.PacketManager;
import scrt.com.packet.ElementType;
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
					if(p instanceof SignalRegister)
					{
						CTCIcon.items.add(new SignalIcon((SignalRegister)p));
						return;
					}
					if(p instanceof TrackRegister)
					{
						CTCIcon.items.add(new TrackIcon((TrackRegister)p));
						return;
					}
					if(p instanceof JunctionRegister)
					{
						CTCIcon.items.add(new JunctionIcon((JunctionRegister)p));
						return;
					}
					for(CTCIcon i : CTCIcon.items)
					{
						i.load(p);
					}
				}
			};
	public abstract ID getID();
	public static CTCIcon findID(ID id)
	{
		for(CTCIcon i : CTCIcon.items)
		{
			if(id.equals(i.getID())) return i;
		}
		return null;
	}
}
