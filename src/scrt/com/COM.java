package scrt.com;

import java.util.ArrayList;
import java.util.List;

import scrt.com.packet.DataPacket;
import scrt.com.packet.ItineraryRegister;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.RegisterPacket;
import scrt.com.packet.StatePacket;
import scrt.com.tcp.TCP;
import scrt.ctc.CTCItem;
import scrt.ctc.Itinerary;

public class COM
{
	static List<Device> devs = new ArrayList<Device>();
	public static void initialize()
	{
		com1.start();
		com2.start();
		new TCP().initialize();
		/*Serial serial = new Serial();
		serial.begin(9600);
		devs.add(serial);*/
	}
	public static void addDevice(Device d)
	{
		devs.add(d);
		for(Packet p : registers)
		{
			d.write(p.getState());
		}
		for(Packet p : linkers)
		{
			d.write(p.getState());
		}
		for(Packet p : data)
		{
			d.write(p.getState());
		}
	}
	public static void write(int a)
	{
		for(Device dev : devs) dev.write(a);
	}
	public static void write(byte[] data)
	{
		for(Device dev : devs) dev.write(data);
	}
	static List<Packet> registers = new ArrayList<Packet>();
	static List<LinkPacket> linkers = new ArrayList<LinkPacket>();
	static List<Packet> data = new ArrayList<Packet>();
	public static void toSend(Packet p)
	{
		synchronized(outQueue)
		{
			outQueue.add(p);
			outQueue.notify();
		}
	}
	public static void send(Packet p)
	{
		if(p instanceof RegisterPacket) registers.add(p);
		else if(p instanceof LinkPacket) linkers.add((LinkPacket) p);
		else if(p instanceof DataPacket)
		{
			data.removeIf((packet) -> packet instanceof StatePacket && ((StatePacket)packet).id == ((StatePacket)p).id);
			data.add(p);
		}
		write(p.getState());
		return;
	}
	static List<Packet> inQueue = new ArrayList<Packet>();
	static List<Packet> outQueue = new ArrayList<Packet>();
	static Thread com1 = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(outQueue)
				{
					if(outQueue.size()!=0)
					{
						send(outQueue.remove(0));
					}
					else
						try
						{
							outQueue.wait();
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		}
	});
	static Thread com2 = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(inQueue)
				{
					if(inQueue.size()!=0)
					{
						Packet p = inQueue.remove(0);
						if(p instanceof ItineraryRegister) Itinerary.handlePacket(p);
						else CTCItem.PacketManager.handlePacket(p);
					}
					else
						try
						{
							inQueue.wait();
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		}
	});
}
