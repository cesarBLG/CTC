package scrt.com.packet;

public abstract class StatePacket extends Packet
{
	public ID id;
	public StatePacket(ID packetID)
	{
		id = packetID;
	}
}
