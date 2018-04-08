package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;

public class ItineraryRegister extends Packet
{
	public TrackItemID start;
	public TrackItemID destination;
	public Orientation dir;
	public ItineraryRegister(TrackItemID start, TrackItemID destination)
	{
		this.start = start;
		this.destination = destination;
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(start.getId());
		data.addAll(destination.getId());
		data.add(dir.ordinal());
		return data;
	}
	public static ItineraryRegister byState(InputStream i) throws IOException
	{
		i.read();
		TrackItemID i1 = new TrackItemID(i);
		i.read();
		TrackItemID i2 = new TrackItemID(i);
		var ir = new ItineraryRegister(i1, i2);
		ir.dir = Orientation.values()[i.read()];
		return ir;
	}
}
