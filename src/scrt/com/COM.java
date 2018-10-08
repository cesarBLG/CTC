/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scrt.com;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import scrt.com.packet.ActionPacket;
import scrt.com.packet.DataPacket;
import scrt.com.packet.ItineraryStablisher;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.RegisterPacket;
import scrt.com.packet.StatePacket;
import scrt.com.tcp.TCP;
import scrt.ctc.CTCItem;
import scrt.ctc.Itinerary;
import scrt.ctc.Loader;

public class COM
{
	static List<Device> devs = new ArrayList<>();
	public static void initialize()
	{
		comIn.start();
		comOut.start();
		new TCP().initialize();
		/*try
		{
			Class.forName("gnu.io.SerialPort");
			new Serial().begin(9600);
		}
		catch (ClassNotFoundException e){}*/
		new File();
	}
	public static synchronized void addDevice(Device d)
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
		for(Packet p : extern)
		{
			d.write(p.getState());
		}
	}
	static List<Packet> registers = new ArrayList<>();
	static List<LinkPacket> linkers = new ArrayList<>();
	static List<Packet> data = new ArrayList<>();
	static List<Packet> extern = new ArrayList<>();
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
		else if(p instanceof ActionPacket)
		{
			extern.removeIf((packet) -> packet instanceof StatePacket && ((StatePacket)packet).id == ((StatePacket)p).id);
			extern.add(p);
		}
		byte[] data = p.getState();
		for(Device dev : devs) dev.write(data);
		return;
	}
	public static Queue<Packet> inQueue = new LinkedList<>();
	static Queue<Packet> outQueue = new LinkedList<>();
	static Thread comOut = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(outQueue)
				{
					if(!outQueue.isEmpty())
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
	static Thread comIn = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(inQueue)
				{
					if(!inQueue.isEmpty())
					{
						Packet p = inQueue.poll();
						synchronized(Loader.ctcThread.tasks)
						{
							Loader.ctcThread.tasks.add(new Runnable()
									{
										@Override
										public void run()
										{
											if(p instanceof ItineraryStablisher) Itinerary.handlePacket(p);
											else CTCItem.PacketManager.handlePacket(p);
										}
									});
							Loader.ctcThread.tasks.notify();
						}
					}
					else
					{
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
		}
	});
}
