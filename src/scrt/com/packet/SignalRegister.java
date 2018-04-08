package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SignalRegister extends StatePacket implements RegisterPacket
{
	public SignalRegister(SignalID packetID)
	{
		super(packetID);
	}
	public String Name;
	public boolean Fixed = false;
	public boolean EoT = false;
	public int x;
	public int y;
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		for(int i=0;  i<Name.length(); i++)
		{
			data.add((int) Name.charAt(i));
		}
		data.add(0);
		data.add(Fixed ? 1 : 0);
		data.add(EoT ? 1 : 0);
		data.add(x);
		data.add(y);
		return data;
	}
	public static SignalRegister byState(InputStream i) throws IOException
	{
		i.read();
		var s = new SignalRegister(new SignalID(i));
		s.Name = "";
		int c = i.read();
		while(c!=0)
		{
			s.Name = s.Name + (char)c;
			c = i.read();
		}
		s.Fixed = i.read() == 1;
		s.EoT = i.read() == 1;
		s.x = (byte)i.read();
		s.y = (byte)i.read();
		return s;
	}
}
