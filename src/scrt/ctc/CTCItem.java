package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.com.packet.ID;
import scrt.com.packet.Packable;
import scrt.com.packet.PacketManager;
import scrt.event.SRCTEvent;
import scrt.event.SCRTListener;

public abstract class CTCItem implements SCRTListener, Packable {
	public List<SCRTListener> listeners = new ArrayList<SCRTListener>();
	protected List<SRCTEvent> Queue = new ArrayList<SRCTEvent>();
	protected boolean EventsMuted = false;
	public static PacketManager PacketManager = new PacketManager();
	public CTCItem()
	{
		synchronized(PacketManager.items)
		{
			PacketManager.items.add(this);
		}
	}
	public abstract ID getID();
	public static CTCItem findId(ID id)
	{
		synchronized(PacketManager.items)
		{
			for(Packable p : PacketManager.items)
			{
				if(id.equals(((CTCItem)p).getID())) return (CTCItem)p;
			}
			return null;
		}
	}
}
