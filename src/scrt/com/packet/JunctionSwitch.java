package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
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
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		return data;
	}
	public static JunctionSwitch byState(InputStream i) throws IOException
	{
		i.read();
		var js = new JunctionSwitch(new JunctionID(i));
		return js;
	}
}
