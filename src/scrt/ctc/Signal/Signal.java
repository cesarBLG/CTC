package scrt.ctc.Signal;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.Packet;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.ctc.CTCItem;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
import scrt.event.SRCTListener;
import scrt.event.SignalEvent;

public abstract class Signal extends CTCItem
{
	public enum SignalType
	{
		Exit,
		Entry,
		Advanced,
		Block,
		Shunting,
		Exit_Indicator,
		Switch_Indicator;
		@Override
		public String toString()
		{
			switch(this)
			{
				case Exit:
					return "S";
				case Entry:
					return "E";
				case Advanced:
					return "E'";
				case Shunting:
					return "M";
				case Exit_Indicator:
					return "IS";
				case Switch_Indicator:
					return "R";
				default:
					return "";
			}
		}
	}
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
	public Signal(Station s)
	{
		Station = s;
		Station.Signals.add(this);
	}
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
		id.Number = Number;
		id.Track = Track;
		id.Name = Name;
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
		COM.toSend(p);
	}
	public static Signal construct(SignalRegister reg)
	{
		SignalID id = (SignalID) reg.id;
		switch(id.Class)
		{
			case Exit_Indicator:
				return new ExitIndicator(id.Name, scrt.ctc.Station.byNumber(reg.id.stationNumber));
			default:
				if(reg.EoT) return new EoT(id.Direction, scrt.ctc.Station.byNumber(reg.id.stationNumber));
				if(reg.Fixed) return new FixedSignal(id.Name, id.Direction, Aspect.Anuncio_parada, scrt.ctc.Station.byNumber(reg.id.stationNumber));
				return new MainSignal(id.Name, scrt.ctc.Station.byNumber(reg.id.stationNumber));
		}
	}
}
