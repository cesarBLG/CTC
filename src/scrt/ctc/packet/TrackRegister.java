package scrt.ctc.packet;

public class TrackRegister extends Packet
{
	public String Name;
	public TrackRegister(ID packetID)
	{
		super(packetID);
	}
	@Override
	public byte[] getState()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
