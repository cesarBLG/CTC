package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.TrackItem;

public class TrackRegister extends StatePacket
{
	public String Name;
	public int OddRotation;
	public int EvenRotation;
	public TrackRegister(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(OddRotation);
		data.add(EvenRotation);
		return fromList(data);
	}
}
