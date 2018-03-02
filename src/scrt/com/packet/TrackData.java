package scrt.com.packet;

import scrt.Orientation;

public class TrackData extends Packet
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
		// TODO Auto-generated method stub
		return null;
	}

}
