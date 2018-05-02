package scrt.com;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import scrt.com.packet.DataPacket;
import scrt.com.packet.ItineraryStablisher;
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
		new Serial().begin(9600);
		new File();
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
		byte[] data = p.getState();
		for(Device dev : devs) dev.write(data);
		return;
	}
	static Queue<Packet> inQueue = new LinkedList<Packet>();
	static Queue<Packet> outQueue = new LinkedList<Packet>();
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
						send(outQueue.poll());
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
						Packet p = inQueue.poll();
						if(p instanceof ItineraryStablisher) Itinerary.handlePacket(p);
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
