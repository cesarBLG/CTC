package scrt.ctc;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import scrt.ctc.Signal.Signal;
import scrt.ctc.packet.Packable;
import scrt.ctc.packet.Packet;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.gui.CTCIcon;

public abstract class CTCItem implements SRCTListener, Packable {
	public List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	protected List<SRCTEvent> Queue = new ArrayList<SRCTEvent>();
	protected boolean EventsMuted = false;
	public CTCIcon icon;
	static List<CTCItem> items = new ArrayList<CTCItem>();
	public CTCItem()
	{
		items.add(this);
	}
	public static void handlePacket(Packet p)
	{
		for(CTCItem t : items)
		{
			if(t instanceof Signal)
			{
				if(p.equals(t.getPacket()))
				{
					t.load(p);
				}
			}
		}
	}
}
