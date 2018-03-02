package scrt.com.packet;

import scrt.ctc.TrackItem;

public class TrackRegister extends Packet
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
		// TODO Auto-generated method stub
		return null;
	}
}
