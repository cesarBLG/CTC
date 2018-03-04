package scrt.com.packet;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class LinkPacket extends Packet
{
	public ID id1;
	public ID id2;
	public LinkPacket(ID linked1, ID linked2)
	{
		id1 = linked1;
		id2 = linked2;
	}
	@Override
	public byte[] getState()
	{
		List<Integer> data = new ArrayList<Integer>();
		data.add(type.ordinal());
		data.addAll(id1.getId());
		data.addAll(id2.getId());
		return fromList(data);
	}
}
