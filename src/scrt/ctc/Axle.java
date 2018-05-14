package scrt.ctc;

import scrt.Orientation;
import scrt.train.Wagon;

public class Axle
{
	public int aID;
	static int AxleCount = 0;
	public Wagon wagon = null;
	public TrackItem firstPosition = null;
	public TrackItem lastPosition = null;
	public Orientation orientation;
	public Axle()
	{
		aID = ++AxleCount;
	}
	public void update()
	{
		wagon.train.updatePosition();
	}
}
