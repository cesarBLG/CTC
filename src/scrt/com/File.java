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
		java.io.File f = new java.io.File("layout.bin");
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
					LinkPacket l = (LinkPacket)p;
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
