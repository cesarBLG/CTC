package scrt.ctc.packet;

import java.util.List;

import scrt.Orientation;
import scrt.ctc.Signal.Aspect;
import scrt.ctc.Signal.Signal;
import scrt.ctc.Signal.SignalType;

public class SignalData extends Packet
{
	public SignalData(ID packetID)
	{
		super(packetID);
	}
	public Aspect SignalAspect;
	public boolean Automatic;
	public boolean UserRequest;
	public boolean OverrideRequest;
	public boolean ClearRequest;
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet byState(byte[] data)
	{
		return null;
	}
}
