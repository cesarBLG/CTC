package scrt.com.packet;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Packet implements Serializable
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
	public static StatePacket byState(byte[] data)
	{
		try
		{
			Class<?> c = Class.forName(PacketType.values()[data[0]].name());
			if(c.isAssignableFrom(Packet.class))
			{
				c.getMethod("byState", byte[].class).invoke(data);
			}
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			
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
