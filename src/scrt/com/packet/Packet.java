package scrt.com.packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
		ItineraryStablisher,
		ACData,
		RequestPacket,
		StationRegister,
		ClearOrder,
		AutomaticOrder
	}
	PacketType type;
	public abstract List<Integer> getListState();
	public Packet()
	{
		type = PacketType.valueOf(this.getClass().getSimpleName());
	}
	public static Packet byState(InputStream in) throws IOException
	{
		try
		{
			char[] magic = new char[4];
			for(int i=0; i<4; i++)
			{
				magic[i] = (char)in.read();
			}
			while(!String.valueOf(magic).equals("SCRT"))
			{
				int i=0;
				for(i=0; i<3; i++)
				{
					magic[i] = magic[i+1];
				}
				magic[i] = (char) in.read();
			}
			int length = in.read();
			byte[] data = new byte[length];
			in.read(data);
			if(data[length - 1] != getControl(data, 0)) return null;
			in = new ByteArrayInputStream(data);
			Class<?> c = Class.forName("scrt.com.packet.".concat(PacketType.values()[in.read()].name()));
			return (Packet) c.getMethod("byState", InputStream.class).invoke(null, in);
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public byte[] getState()
	{
		var l = getListState();
		byte[] data = new byte[l.size() + 7];
		data[0] = 'S';
		data[1] = 'C';
		data[2] = 'R';
		data[3] = 'T';
		data[4] = (byte) (l.size() + 2);
		data[5] = (byte) type.ordinal();
		for(int i=0; i<l.size(); i++)
		{
			data[i+6] = l.get(i).byteValue();
		}
		data[data.length - 1] = getControl(data, 5);
		return data;
	}
	public static byte getControl(byte[] data, int offset)
	{
		int control = 0;
		for(int i=offset; i<data.length - 1; i++)
		{
			control += data[i] * (((i-offset) % 4) == 0 ? 4 : ((i-offset) % 4));
		}
		control = 255 - (control % 255);
		return (byte)control;
	}
	public static List<Integer> toList(String s)
	{
		var l = new ArrayList<Integer>();
		for(int i=0;  i<s.length(); i++)
		{
			l.add((int) s.charAt(i));
		}
		l.add(0);
		return l;
	}
	public static String toString(InputStream in) throws IOException
	{
		String s = "";
		int c = in.read();
		while(c!=0)
		{
			s = s + (char)c;
			c = in.read();
		}
		return s;
	}
}
