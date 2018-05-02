package scrt.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import scrt.com.packet.Packet;

public class Receiver
{
	Socket s;
	public Receiver()
	{
		new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						while(s==null)
						{
							try
							{
								s = new Socket("localhost", 300);
							}
							catch (IOException e)
							{
							}
						}
						while(!s.isConnected()) {}
						InputStream i;
						try
						{
							i = s.getInputStream();
							while(true)
							{
								CTCIcon.PacketManager.handlePacket(Packet.byState(i));
							}
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
	}
	public void send(Packet p)
	{
		write(p.getState());
	}
	public void write(byte[] data)
	{
		if(s==null||!s.isConnected()) return;
		try
		{
			s.getOutputStream().write(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
