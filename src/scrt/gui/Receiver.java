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
package scrt.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.SwingUtilities;

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
								s = new Socket("localhost", 5300);
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
								Packet p = Packet.byState(i);
								SwingUtilities.invokeLater(() -> CTCIcon.PacketManager.handlePacket(p));
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
