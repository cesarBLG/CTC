package scrt.com.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scrt.Orientation;
import scrt.ctc.Position;

public class JunctionData extends StatePacket
{
	public Orientation BlockState;
	public Orientation Occupied;
	public Position Switch;
	public int Locked;
	public JunctionData(JunctionID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(Occupied.ordinal());
		data.add(Switch.ordinal());
		data.add(Locked);
		return fromList(data);
	}
}
