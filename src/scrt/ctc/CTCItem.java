package scrt.ctc;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.PacketManager;
import scrt.com.packet.SignalData;
import scrt.ctc.Signal.Signal;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.gui.CTCIcon;

public abstract class CTCItem implements SRCTListener, Packable {
	public List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	protected List<SRCTEvent> Queue = new ArrayList<SRCTEvent>();
	protected boolean EventsMuted = false;
	public CTCIcon icon;
	public static PacketManager PacketManager = new PacketManager();
	public CTCItem()
	{
		PacketManager.items.add(this);
	}
}
