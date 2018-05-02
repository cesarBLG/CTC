package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AutomaticOrder extends StatePacket implements OrderPacket
{
	public boolean automatic = false;
	public AutomaticOrder(SignalID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		data.add(automatic ? 1 : 0);
		return data;
	}
	public static AutomaticOrder byState(InputStream i) throws IOException
	{
		i.read();
		var ao = new AutomaticOrder(new SignalID(i));
		ao.automatic = i.read() != 0;
		return ao;
	}
}
