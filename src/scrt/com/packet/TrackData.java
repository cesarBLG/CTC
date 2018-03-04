package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;

public class TrackData extends StatePacket
{
	public Orientation BlockState;
	public Orientation Occupied;
	public int OddAxles;
	public int EvenAxles;
	public boolean Acknowledged;
	public TrackData(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(Occupied.ordinal());
		data.add(OddAxles);
		data.add(EvenAxles);
		data.add(Acknowledged ? 1 : 0);
		return fromList(data);
	}

}
