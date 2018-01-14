package scrt.ctc;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.gui.CTCIcon;

public abstract class CTCItem implements SRCTListener {
	List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	List<SRCTEvent> Queue = new ArrayList<SRCTEvent>();
	boolean EventsMuted = false;
	public CTCIcon icon;
}
