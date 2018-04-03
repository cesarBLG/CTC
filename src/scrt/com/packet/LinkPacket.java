package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LinkPacket extends Packet
{
	public ID id1;
	public ID id2;
	public LinkPacket(ID linked1, ID linked2)
	{
		id1 = linked1;
		id2 = linked2;
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id1.getId());
		data.addAll(id2.getId());
		return data;
	}
	public static LinkPacket byState(InputStream i) throws IOException
	{
		return new LinkPacket(ID.byState(i), ID.byState(i));
	}
}
