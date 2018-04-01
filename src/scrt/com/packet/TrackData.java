package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;

public class TrackData extends StatePacket implements DataPacket
{
	public Orientation BlockState;
	public Orientation Occupied;
	public int OddAxles;
	public int EvenAxles;
	public boolean Acknowledged;
	public TrackData(TrackItemID packetID)
	{
		super(packetID);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(Occupied.ordinal());
		data.add(OddAxles);
		data.add(EvenAxles);
		data.add(Acknowledged ? 1 : 0);
		return fromList(data);
	}
	public static TrackData byState(InputStream i) throws IOException
	{
		i.read();
		var td = new TrackData(new TrackItemID(i));
		td.BlockState = Orientation.values()[i.read()];
		td.Occupied = Orientation.values()[i.read()];
		td.OddAxles = (byte)i.read();
		td.EvenAxles = (byte)i.read();
		td.Acknowledged = i.read() == 1;
		return td;
	}
}
