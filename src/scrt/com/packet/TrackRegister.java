package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrackRegister extends StatePacket implements RegisterPacket
{
	public String Name;
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
		for(int i=0;  i<Name.length(); i++)
		{
			data.add((int) Name.charAt(i));
		}
		data.add(0);
		data.add(OddRotation);
		data.add(EvenRotation);
		return data;
	}
	public static TrackRegister byState(InputStream i) throws IOException
	{
		i.read();
		var tr = new TrackRegister(new TrackItemID(i));
		tr.Name = "";
		int c = i.read();
		while(c!=0)
		{
			tr.Name = tr.Name + (char)c;
			c = i.read();
		}
		tr.OddRotation = (byte)i.read();
		tr.EvenRotation = (byte)i.read();
		return tr;
	}
}
