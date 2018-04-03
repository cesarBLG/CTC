package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;

public class ACData extends StatePacket implements DataPacket
{
	public Orientation dir = Orientation.None;
	public ACData(ACID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(dir.ordinal());
		return data;
	}
	public static ACData byState(InputStream i) throws IOException
	{
		i.read();
		ACData ac = new ACData(new ACID(i));
		ac.dir = Orientation.values()[i.read()];
		return ac;
	}
}
