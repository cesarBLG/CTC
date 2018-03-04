package scrt.com.packet;

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
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(start.getId());
		data.addAll(destination.getId());
		data.add(dir.ordinal());
		return fromList(data);
	}
}
