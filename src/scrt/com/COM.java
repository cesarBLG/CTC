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
