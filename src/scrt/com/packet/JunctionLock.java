package scrt.com.packet;

public class JunctionLock extends StatePacket
{
	public boolean order;
	public int value;
	public JunctionLock(JunctionID id)
	{
		super(id);
	}
	@Override
	public byte[] getState()
	{
		
		return null;
	}
	
}
