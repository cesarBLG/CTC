package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import scrt.Orientation;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.event.SignalEvent;
import scrt.gui.SignalIcon;

public abstract class Signal extends CTCItem
{
	public Orientation Direction;
	public String Name = "";
	public SignalType Class = SignalType.Entry;
	public boolean Automatic = false;
	boolean BlockSignal = false;
	public boolean Cleared = false;
	boolean Occupied = false;
	public boolean Override = false;
	public boolean ClearRequest = false;
	public boolean OverrideRequest = false;
	boolean Switches = false;
	boolean allowsOnSight = false;
	public Aspect SignalAspect = Aspect.Parada;
	public List<Aspect> Aspects = new ArrayList<Aspect>();
	Station Station;
	int Track;
	int Number;
	TrackItem Linked;
	public abstract void Lock();
	public abstract void Unlock();
	public abstract void setState();
	Aspect LastAspect = Aspect.Parada;
	public void setAspect()
	{
		if(LastAspect==SignalAspect) return;
		for(SRCTListener l : listeners) l.actionPerformed(new SignalEvent(this));
		Serial.send(this, true);
		LastAspect = SignalAspect;
	}
	public void update() {setAspect();}
}
