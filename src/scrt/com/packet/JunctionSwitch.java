package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JunctionSwitch extends StatePacket
{
	public boolean force = false;
	public boolean muelle = false;
	public JunctionSwitch(JunctionID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		data.add((force ? 1 : 0) + (muelle ? 2 : 0));
		return data;
	}
	public static JunctionSwitch byState(InputStream i) throws IOException
	{
		i.read();
		var js = new JunctionSwitch(new JunctionID(i));
		int data = i.read();
		js.force = (data & 1) != 0;
		js.muelle = (data & 2) != 0;
		return js;
	}
}
