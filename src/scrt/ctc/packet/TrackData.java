package scrt.ctc.packet;

import scrt.Orientation;

public class TrackData extends Packet
{
	public Orientation block;
	public Orientation occupation;
	public int OddAxles;
	public int EvenAxles;
	public TrackData(ID packetID)
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
