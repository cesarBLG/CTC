package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.ctc.Signal.Aspect;
import scrt.ctc.Signal.Signal;
import scrt.ctc.Signal.SignalType;

public class SignalData extends StatePacket
{
	public SignalData(SignalID packetID)
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
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(SignalAspect.ordinal());
		data.add(Automatic ? 1 : 0);
		data.add(UserRequest ? 1 : 0);
		data.add(OverrideRequest ? 1 : 0);
		data.add(ClearRequest ? 1 : 0);
		return fromList(data);
	}
	public static StatePacket byState(byte[] data)
	{
		return null;
	}
}
