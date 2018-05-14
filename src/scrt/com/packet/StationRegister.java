package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationRegister extends Packet implements RegisterPacket
{
	public int associatedNumber = 0;
	public String name = "";
	public String shortName = "";
	public StationRegister(int number)
	{
		associatedNumber = number;
	}
	@Override
	public List<Integer> getListState()
	{
		var l = new ArrayList<Integer>();
		l.add(associatedNumber);
		l.addAll(toList(name));
		l.addAll(toList(shortName));
		return l;
	}
	public static StationRegister byState(InputStream i) throws IOException
	{
		var s = new StationRegister(i.read());
		s.name = toString(i);
		s.shortName = toString(i);
		return s;
	}
}
