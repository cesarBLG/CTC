package scrt.gui;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import scrt.com.packet.ID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.PacketManager;
import scrt.com.packet.SignalRegister;
import scrt.com.packet.TrackRegister;

public abstract class CTCIcon implements Packable {
	public abstract void update();
	static List<CTCIcon> items = new ArrayList<CTCIcon>();
	public JPanel comp;
	public static Reader reader;
	public static CTCIcon icon;
	public static GridBagConstraints gbc;
	public static JPanel layout;
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
