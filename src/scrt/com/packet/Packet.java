package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Packet
{
	public enum PacketType
	{
		EmptyPacket,
		LinkPacket,
		SignalData,
		SignalRegister,
		TrackData,
		TrackRegister,
		JunctionData,
		JunctionSwitch,
		JunctionRegister,
		JunctionLock,
		ItineraryRegister,
		ACData,
	}
	PacketType type;
	public abstract byte[] getState();
	public Packet()
	{
		type = PacketType.valueOf(this.getClass().getSimpleName());
	}
	public static Packet byState(InputStream in) throws IOException
	{
		try
		{
			Class<?> c = Class.forName("scrt.com.packet.".concat(PacketType.values()[in.read()].name()));
			return (Packet) c.getMethod("byState", InputStream.class).invoke(null, in);
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	static byte[] fromList(List<Integer> l)
	{
		byte[] data = new byte[l.size()];
		for(int i=0; i<l.size(); i++)
		{
			data[i] = l.get(i).byteValue();
		}
		return data;
	}
}
