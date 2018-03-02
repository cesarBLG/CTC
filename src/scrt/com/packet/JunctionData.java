package scrt.com.packet;

import scrt.Orientation;
import scrt.ctc.Position;

public class JunctionData extends TrackData
{
	public Position Switch;
	public int Locked;
	public JunctionData(JunctionID id)
	{
		super(null);
		this.id = id;
	}
}
