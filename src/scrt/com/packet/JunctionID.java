package scrt.com.packet;

import java.util.List;

public class JunctionID extends ID
{
	public int Number;
	public String Name;
	public JunctionID()
	{
		type = PacketType.Switch;
	}
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Number);
		return l;
	}
}
