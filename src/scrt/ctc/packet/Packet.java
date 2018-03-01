package scrt.ctc.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet implements Serializable
{
	public ID id;
	public Packet(ID packetID)
	{
		id = packetID;
	}
	public abstract byte[] getState();
	public static Packet byState(byte[] data)
	{
		Packet p = null;
		switch(PacketType.values()[data[0]])
		{
			case Signal:
				p = SignalData.byState(data);
			default:
				break;
		}
		return p;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Packet)
		{
			return id.equals(((Packet)obj).id);
		}
		if(obj instanceof ID) return id.equals(obj);
		return super.equals(obj);
	}
}
