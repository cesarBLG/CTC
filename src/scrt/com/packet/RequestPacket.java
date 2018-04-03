package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RequestPacket extends Packet
{
	boolean registers;
	boolean links;
	boolean data;
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.add((registers ? 1 : 0) + (links ? 2 : 0) + (registers ? 4 : 0));
		return data;
	}
	public static RequestPacket byState(InputStream i) throws IOException
	{
		var rp = new RequestPacket();
		int get = i.read();
		rp.registers = (get & 1) != 0;
		rp.links = (get & 2) != 0;
		rp.data = (get & 4) != 0;
		return rp; 
	}
}
