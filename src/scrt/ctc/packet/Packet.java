package scrt.ctc.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet implements Serializable
{
	public enum PacketType
	{
		Signal,
		Itinerary,
		AxleCounter,
		TrackItem,
		Switch
	}
	public PacketType type;
	public int stationNumber;
	public List<Integer> getId()
	{
		List<Integer> l = new ArrayList<Integer>();
		l.add(type.ordinal());
		l.add(stationNumber);
		return l;
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
	public boolean equals(Object packet)
	{
		if(packet instanceof Packet)
		{
			List<Integer> l1 = ((Packet) packet).getId();
			List<Integer> l2 = getId();
			if(l1.size()!=l2.size()) return false;
			for(int i=0; i<l1.size(); i++)
			{
				if(l1.get(i)!=l2.get(i)) return false;
			}
			return true;
		}
		return super.equals(packet);
	}
}
