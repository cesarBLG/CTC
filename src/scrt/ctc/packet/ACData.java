package scrt.ctc.packet;

import java.util.List;

import scrt.Orientation;

public class ACData extends Packet
{
	public Orientation dir = Orientation.None;
	public ACData(ID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
