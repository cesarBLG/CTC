package scrt.com.packet;

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
		// TODO Auto-generated method stub
		return null;
	}
}
