package scrt.com.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class StatePacket extends Packet
{
	public ID id;
	public StatePacket(ID packetID)
	{
		id = packetID;
	}
}
