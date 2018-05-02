package scrt.com;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import scrt.com.packet.DataPacket;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalRegister;
import scrt.ctc.CTCItem;
import scrt.ctc.Signal.EoT;

public class File implements Device
{
	FileOutputStream out;
	public File()
	{
		var f = new java.io.File("layout.bin");
		if(f.exists()) return;
		try
		{
			out = new FileOutputStream("layout.bin");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		COM.addDevice(this);
	}
	@Override
	public void write(int c)
	{
	}

	@Override
	public void write(byte[] b)
	{
		try
		{
			Packet p = Packet.byState(new ByteArrayInputStream(b));
			if(!(p instanceof DataPacket))
			{
				if(p instanceof SignalRegister)
				{
					if(((SignalRegister)p).EoT) return;
				}
				if(p instanceof LinkPacket)
				{
					var l = (LinkPacket)p;
					if(CTCItem.findId(l.id2) instanceof EoT) return;
				}
				out.write(b);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void free()
	{
		try
		{
			out.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
