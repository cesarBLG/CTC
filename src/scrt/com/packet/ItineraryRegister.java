package scrt.com.packet;

import scrt.Orientation;

public class ItineraryRegister extends Packet
{
	public TrackItemID destination;
	public Orientation dir;
	public ItineraryRegister(TrackItemID start, TrackItemID destination)
	{
		super(start);
		this.destination = destination;
	}
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ItineraryRegister)
		{
			ItineraryRegister r = (ItineraryRegister) obj;
			return id.equals(r.id) && destination.equals(r.destination);
		}
		//return id.equals(obj) && destination.equals(obj);
		return false;
	}
}
