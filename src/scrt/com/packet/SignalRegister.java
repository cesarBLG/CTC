package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Signal.SignalType;

public class SignalRegister extends StatePacket
{
	public SignalRegister(SignalID packetID)
	{
		super(packetID);
	}
	public boolean Fixed = false;
	public boolean EoT = false;
	public int x;
	public int y;
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(Fixed ? 1 : 0);
		data.add(x);
		data.add(y);
		return fromList(data);
	}
	
}
