package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TrackItemID extends ID
{
	public int x;
	public int y;
	public TrackItemID() {}
	public TrackItemID(InputStream i) throws IOException 
	{
		super(i);
		x = (byte)i.read();
		y = (byte)i.read();
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(x);
		l.add(y);
		return l;
	}
}
