package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.gui.CTCIcon;
import scrt.gui.TrackIcon;

public class ACData extends StatePacket
{
	public Orientation dir = Orientation.None;
	public ACData(ACID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(dir.ordinal());
		return fromList(data);
	}

}
