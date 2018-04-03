package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.ctc.Position;

public class JunctionData extends StatePacket implements DataPacket
{
	public Orientation BlockState;
	public Orientation Occupied;
	public Position Switch;
	public int Locked;
	public int blockPosition;
	public boolean locking;
	public JunctionData(JunctionID id)
	{
		super(id);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(BlockState.ordinal());
		data.add(Occupied.ordinal());
		data.add(Switch.ordinal());
		data.add(Locked);
		data.add(blockPosition);
		data.add(locking ? 1 : 0);
		return data;
	}
	public static JunctionData byState(InputStream i) throws IOException
	{
		i.read();
		var jd = new JunctionData(new JunctionID(i));
		jd.BlockState = Orientation.values()[i.read()];
		jd.Occupied = Orientation.values()[i.read()];
		jd.Switch = Position.values()[i.read()];
		jd.Locked = (byte)i.read();
		jd.blockPosition = (byte)i.read();
		jd.locking = i.read() == 1;
		return jd;
	}
}
