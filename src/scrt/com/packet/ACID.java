package scrt.com.packet;

import java.util.List;

import scrt.Orientation;

public class ACID extends ID
{
	public int Num;
	public Orientation dir;
	public ACID()
	{
		type = ElementType.AxleCounter;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Num);
		return l;
	}
}
