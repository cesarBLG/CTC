package scrt.ctc.packet;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.CTCItem;

public interface Packable
{
	ID getId();
	void load(Packet p);
}
