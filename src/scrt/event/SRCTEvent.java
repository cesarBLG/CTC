package scrt.event;

import java.util.List;

public class SRCTEvent{
	public EventType type;
	public Object creator;
	public SRCTEvent(EventType t, Object c)
	{
		type = t;
		creator = c;
	}
	public byte[] getId()
	{
		return null;
	}
	public static SRCTEvent getById(byte[] data)
	{
		return null;
	}
	static byte[] getBytes(List<Integer> data)
	{
		byte[] send = new byte[data.size() + 1];
		int control = 0;
		for(int i=0; i<data.size(); i++)
		{
			control += data.get(i) * ((i%2) + 1);
			send[i] = data.get(i).byteValue();
		}
		control = 255 - (control % 255);
		send[send.length - 1] = (byte) control;
		return send;
	}
	public static byte getControl(byte[] data)
	{
		int control = 0;
		for(int i = 0; i<data.length; i++)
		{
			control += data[i] * ((i%2) + 1);
		}
		control = 255 - (control % 255);
		return (byte)control;
	}
}
