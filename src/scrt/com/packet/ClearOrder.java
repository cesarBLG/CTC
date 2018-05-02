package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClearOrder extends StatePacket implements OrderPacket
{
	public boolean clear = false;
	public boolean override = false;
	public ClearOrder(SignalID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		data.add((clear ? 1 : 0) + (override ? 2 : 0));
		return data;
	}
	public static ClearOrder byState(InputStream i) throws IOException
	{
		i.read();
		var co = new ClearOrder(new SignalID(i));
		int val = i.read();
		co.clear = (val & 1) != 0;
		co.override = (val & 2) != 0;
		return co;
	}
}
