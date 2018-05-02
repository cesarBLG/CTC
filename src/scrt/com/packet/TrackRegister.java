package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrackRegister extends StatePacket implements RegisterPacket
{
	public String Name = "";
	public int OddRotation;
	public int EvenRotation;
	public TrackRegister(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		data.addAll(toList(Name));
		data.add(OddRotation);
		data.add(EvenRotation);
		return data;
	}
	public static TrackRegister byState(InputStream i) throws IOException
	{
		i.read();
		var tr = new TrackRegister(new TrackItemID(i));
		tr.Name = toString(i);
		tr.OddRotation = (byte)i.read();
		tr.EvenRotation = (byte)i.read();
		return tr;
	}
}
