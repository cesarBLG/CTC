package scrt.com.packet;

import scrt.ctc.Signal.SignalType;

public class SignalRegister extends Packet
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
		// TODO Auto-generated method stub
		return null;
	}
	
}
