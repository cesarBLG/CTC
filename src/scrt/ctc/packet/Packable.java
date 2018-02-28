package scrt.ctc.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.CTCItem;

public interface Packable
{
	Packet getPacket();
	void load(Packet p);
}
