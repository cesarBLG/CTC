package scrt.com.packet;

import javax.swing.JOptionPane;

public class LinkPacket extends Packet
{
	public ID id2;
	public LinkPacket(ID linked1, ID linked2)
	{
		super(linked1);
		id2 = linked2;
	}
	@Override
	public byte[] getState()
	{
		return null;
	}
	@Override
	public boolean equals(Object obj)
	{
		return obj!=null && (obj.equals(id) || obj.equals(id2));
	}
}
