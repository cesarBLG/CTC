package scrt.com.packet;

import java.util.List;

public class TrackItemID extends ID
{
	public int x;
	public int y;
	public TrackItemID()
	{
		type = ElementType.TrackItem;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(x);
		l.add(y);
		return l;
	}
}
