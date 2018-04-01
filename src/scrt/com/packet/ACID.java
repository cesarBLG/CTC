package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import scrt.Orientation;

public class ACID extends ID
{
	public int Num;
	public Orientation dir;
	public ACID() {}
	public ACID(InputStream i) throws IOException
	{
		super(i);
		Num = i.read();
		dir = Num % 2 == 0 ? Orientation.Even : Orientation.Odd;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Num);
		return l;
	}
}
