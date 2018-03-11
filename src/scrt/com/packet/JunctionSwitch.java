package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

public class JunctionSwitch extends StatePacket
{
	public boolean force = false;
	public JunctionSwitch(JunctionID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		return fromList(data);
	}
}
