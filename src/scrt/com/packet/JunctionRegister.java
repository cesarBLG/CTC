package scrt.com.packet;

import scrt.Orientation;
import scrt.ctc.Junction;
import scrt.ctc.Position;

public class JunctionRegister extends StatePacket
{
	public TrackItemID TrackId;
	public Orientation Direction;
	public Position Class;
	public JunctionRegister(JunctionID id1, TrackItemID id2)
	{
		super(id1);
		TrackId = id2;
	}
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
