package scrt.com;

import java.io.IOException;
import java.io.InputStream;

import scrt.com.packet.Packet;

public interface Device
{
	void write(int c);
	void write(byte[] b);
	default void parse(InputStream in) throws IOException
	{
		Packet p = Packet.byState(in);
		synchronized(COM.inQueue)
		{
			COM.inQueue.add(p);
			COM.inQueue.notify();
		}
	}
}
