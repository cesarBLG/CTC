package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import scrt.Orientation;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.gui.SignalIcon;
enum SignalType
{
Exit,
Entry,
Advanced,
Block,
Shunting,
Exit_Indicator,
Switch_Indicator
}
public abstract class Signal extends CTCItem
{
	public Orientation Direction;
	public String Name = "";
	SignalType Class = SignalType.Entry;
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
	public abstract void Clear();
	public abstract void Close();
	public abstract void tryClear();
	public abstract void setState();
	Aspect LastAspect = Aspect.Parada;
	public void setAspect()
	{
		Serial.send(this, true);
		LastAspect = SignalAspect;
	}
	public void update() {setAspect();}
}
