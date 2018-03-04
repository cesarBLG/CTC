package scrt.com.packet;

import java.util.List;

public class ACID extends ID
{
	public int Num;
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
