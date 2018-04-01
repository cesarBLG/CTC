package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JunctionID extends ID
{
	public int Number;
	public String Name;
	public JunctionID() {}
	public JunctionID(InputStream i) throws IOException
	{
		super(i);
		Number = i.read();
		Name = "A" + String.valueOf(Number);
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Number);
		return l;
	}
}
