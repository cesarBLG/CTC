package scrt.ctc.packet;

import java.util.List;

import scrt.Orientation;

public class ACData extends Packet
{
	public int Num;
	public Orientation dir = Orientation.None;
	public ACData()
	{
		type = PacketType.AxleCounter;
	}
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Num);
		return l;
	}
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
