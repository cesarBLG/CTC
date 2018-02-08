package scrt.ctc.packet;

import java.util.List;

import scrt.Orientation;
import scrt.ctc.Signal.Aspect;
import scrt.ctc.Signal.SignalType;

public class SignalData extends Packet
{
	public Aspect SignalAspect;
	public boolean Automatic;
	public SignalType Class;
	public int Number;
	public int Track;
	public Orientation Direction;
	public boolean UserRequest;
	public boolean OverrideRequest;
	public String Name;
	public boolean ClearRequest;
	public boolean Fixed = false;
	public int x;
	public int y;
	public SignalData()
	{
		type = PacketType.Signal;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Class.ordinal());
		l.add(Number);
		l.add(Track);
		l.add(x);
		l.add(y);
		return l;
	}
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
