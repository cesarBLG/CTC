package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.ctc.Position;

public class JunctionRegister extends StatePacket implements RegisterPacket
{
	public TrackItemID TrackId;
	public Orientation Direction;
	public Position Class;
	public JunctionRegister(JunctionID id1, TrackItemID id2)
	{
		super(id1);
		TrackId = id2;
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.addAll(TrackId.getId());
		data.add(Direction.ordinal());
		data.add(Class.ordinal());
		return fromList(data);
	}
	public static JunctionRegister byState(InputStream i) throws IOException
	{
		i.read();
		JunctionID i1 = new JunctionID(i);
		i.read();
		TrackItemID i2 = new TrackItemID(i);
		var jr = new JunctionRegister(i1, i2);
		jr.Direction = Orientation.values()[i.read()];
		jr.Class = Position.values()[i.read()];
		return jr;
	}
}
