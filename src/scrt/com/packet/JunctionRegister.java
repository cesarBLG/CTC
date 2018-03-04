package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.ctc.Junction;
import scrt.ctc.Position;

public class JunctionRegister extends StatePacket
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

}
