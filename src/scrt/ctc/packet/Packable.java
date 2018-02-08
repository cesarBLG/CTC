package scrt.ctc.packet;

public interface Packable
{
	Packet getPacket();
	void load(Packet p);
}
