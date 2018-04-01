package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JunctionLock extends StatePacket
{
	public boolean order;
	public int value;
	public JunctionLock(JunctionID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(order ? 1 :0);
		data.add(value);
		return fromList(data);
	}
	public static JunctionLock byState(InputStream i) throws IOException
	{
		i.read();
		var jl = new JunctionLock(new JunctionID(i));
		jl.order = i.read() == 1;
		jl.value = (byte)i.read();
		return jl;
	}
}
