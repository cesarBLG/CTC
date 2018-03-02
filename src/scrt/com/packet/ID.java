package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

public abstract class ID
{
	public PacketType type;
	public int stationNumber;
	public List<Integer> getId()
	{
		List<Integer> l = new ArrayList<Integer>();
		l.add(type.ordinal());
		l.add(stationNumber);
		return l;
	}
	@Override
	public boolean equals(Object packet)
	{
		if(packet instanceof ID)
		{
			List<Integer> l1 = ((ID) packet).getId();
			List<Integer> l2 = getId();
			if(l1.size()!=l2.size()) return false;
			for(int i=0; i<l1.size(); i++)
			{
				if(!l1.get(i).equals(l2.get(i))) return false;
			}
			return true;
		}
		return super.equals(packet);
	}
}
