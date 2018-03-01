package scrt.ctc.packet;

import scrt.ctc.Signal.SignalType;

public class SignalRegister extends Packet
{
	public SignalRegister(ID packetID)
	{
		super(packetID);
	}
	public boolean Fixed = false;
	public int x;
	public int y;
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
