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
	public boolean Fixed = false;
	public boolean EoT = false;
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.addAll(id.getId());
		data.add(Fixed ? 1 : 0);
		data.add(EoT ? 1 : 0);
		return data;
	}
	public static SignalRegister byState(InputStream i) throws IOException
	{
		i.read();
		var s = new SignalRegister(new SignalID(i));
		s.Fixed = i.read() == 1;
		s.EoT = i.read() == 1;
		return s;
	}
}
