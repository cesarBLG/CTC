package scrt.com.packet;

public class LinkPacket extends Packet
{
	ID id2;
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

}
