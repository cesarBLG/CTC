package scrt.com.packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExternalPacket extends Packet 
{
	List<Integer> data = new ArrayList<>();
	@Override
	public List<Integer> getListState() 
	{
		return data;
	}
	public static ExternalPacket byState(InputStream i)
	{
		ExternalPacket p = new ExternalPacket();
		try 
		{
			while(i.available()>0)
			{
				p.data.add(i.read());
			}
		}
		catch (IOException e) {}
		return p;
	}
}
