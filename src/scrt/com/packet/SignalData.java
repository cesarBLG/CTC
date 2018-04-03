package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Signal.Aspect;

public class SignalData extends StatePacket implements DataPacket
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
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id.getId());
		data.add(SignalAspect.ordinal());
		data.add(Automatic ? 1 : 0);
		data.add(UserRequest ? 1 : 0);
		data.add(OverrideRequest ? 1 : 0);
		data.add(ClearRequest ? 1 : 0);
		return data;
	}
	public static SignalData byState(InputStream d) throws IOException
	{
		d.read();
		var s = new SignalData(new SignalID(d));
		s.SignalAspect = Aspect.values()[d.read()];
		s.Automatic = d.read() == 1;
		s.UserRequest = d.read() == 1;
		s.OverrideRequest = d.read() == 1;
		s.ClearRequest = d.read() == 1;
		return s;
	}
}
