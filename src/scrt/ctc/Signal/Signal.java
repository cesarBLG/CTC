package scrt.ctc.Signal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.Serial;
import scrt.ctc.CTCItem;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
import scrt.ctc.packet.Packet;
import scrt.ctc.packet.Packet.PacketType;
import scrt.ctc.packet.SignalData;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.event.SignalEvent;
import scrt.gui.CTCIcon;
import scrt.gui.SignalIcon;

public abstract class Signal extends CTCItem
{
	public Orientation Direction;
	public String Name = "";
	public SignalType Class;
	public boolean Automatic = false;
	public boolean BlockSignal = false;
	public boolean Cleared = false;
	boolean Occupied = false;
	public boolean Override = false;
	public boolean ClearRequest = false;
	public boolean OverrideRequest = false;
	boolean Switches = false;
	boolean allowsOnSight = false;
	public Aspect SignalAspect = Aspect.Parada;
	public List<Aspect> Aspects = new ArrayList<Aspect>();
	public Station Station;
	public int Track;
	public int Number;
	public TrackItem Linked;
	public abstract void Lock();
	public abstract void Unlock();
	public abstract void setState();
	Aspect LastAspect = null;
	public void setLinked(TrackItem t)
	{
		Linked = t;
		Linked.SignalLinked = this;
		setAspect();
		t.setSignal(this);
	}
	public void setAspect(){send();};
	void send()
	{
		//if(LastAspect==SignalAspect&&LastAuto.equals(Automatic)) return;
		if(Linked==null) return;
		SignalEvent e = new SignalEvent(this);
		for(SRCTListener l : listeners) l.actionPerformed(e);
		COM.send(getPacket());
		LastAspect = SignalAspect;
	}
	public void update() {setAspect();}
	@Override
	public Packet getPacket()
	{
		if(Linked==null) return null;
		SignalData d = new SignalData();
		d.Automatic = Automatic;
		d.SignalAspect = SignalAspect;
		d.Class = Class;
		d.Direction = Direction;
		d.stationNumber = Station.AssociatedNumber;
		d.OverrideRequest = OverrideRequest;
		d.ClearRequest = ClearRequest;
		d.Name = Name;
		d.Number = Number;
		d.Track = Track;
		d.Fixed = this instanceof FixedSignal;
		d.x = Linked.x;
		d.y = Linked.y;
		return d;
	}
	@Override
	public void load(Packet p)
	{
	}
}
