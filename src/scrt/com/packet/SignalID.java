package scrt.com.packet;

import java.util.List;

import scrt.Orientation;
import scrt.ctc.Signal.SignalType;

public class SignalID extends ID
{
	public SignalType Class;
	public int Number;
	public int Track;
	public String Name;
	public Orientation Direction;
	public SignalID()
	{
		type = ElementType.Signal;
	}
	@Override
	public List<Integer> getId()
	{
		List<Integer> l = super.getId();
		l.add(Class.ordinal());
		l.add(Number);
		l.add(Track);
		return l;
	}
}
