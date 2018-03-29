package scrt.ctc.Signal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.Serial;
import scrt.com.packet.ID;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.ctc.CTCItem;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
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
	SignalID id = null;
	public void setLinked(TrackItem t)
	{
		Linked = t;
		Linked.SignalLinked = this;
		send(PacketType.SignalRegister);
		Linked.setSignal(this);
		send(PacketType.SignalData);
	}
	public void setAspect(){send();};
	void send()
	{
		//if(LastAspect==SignalAspect&&LastAuto.equals(Automatic)) return;
		if(Linked==null) return;
		if(LastAspect!=SignalAspect)
		{
			SignalEvent e = new SignalEvent(this);
			List<SRCTListener> list = new ArrayList<SRCTListener>(listeners);
			for(SRCTListener l : list) l.actionPerformed(e);
		}
		send(PacketType.SignalData);
		LastAspect = SignalAspect;
	}
	public void update() {setAspect();}
	public boolean protects() {return false;}
	@Override
	public SignalID getID()
	{
		if(id!=null) return id;
		id = new SignalID();
		id.Class = Class;
		id.Direction = Direction;
		id.stationNumber = Station.AssociatedNumber;
		id.Name = Name;
		id.Number = Number;
		id.Track = Track;
		return id;
	}
	@Override
	public void load(Packet p)
	{
	}
	void send(PacketType type)
	{
		Packet p;
		switch(type)
		{
			case SignalRegister:
				SignalRegister reg = new SignalRegister(getID());
				reg.Fixed = this instanceof FixedSignal;
				reg.x = Linked.x;
				reg.y = Linked.y;
				reg.EoT = this instanceof EoT;
				p = reg;
				break;
			case SignalData:
				SignalData d = new SignalData(id);
				d.Automatic = Automatic;
				d.SignalAspect = SignalAspect;
				d.OverrideRequest = OverrideRequest;
				d.ClearRequest = ClearRequest;
				if(this instanceof MainSignal) d.UserRequest = ((MainSignal)this).UserRequest;
				p = d;
				break;
			default:
				return;
		}
		COM.send(p);
	}
}
