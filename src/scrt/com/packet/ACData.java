package scrt.com.packet;

import java.util.List;

import scrt.Orientation;

public class ACData extends Packet
{
	public Orientation dir = Orientation.None;
	public ACData(ACID id)
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
