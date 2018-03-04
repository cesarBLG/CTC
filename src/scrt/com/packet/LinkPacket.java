package scrt.com.packet;

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
		return null;
	}
}
