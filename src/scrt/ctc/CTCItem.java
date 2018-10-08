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
package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.com.packet.ID;
import scrt.com.packet.Packable;
import scrt.com.packet.PacketManager;
import scrt.event.SRCTEvent;
import scrt.event.SCRTListener;

public abstract class CTCItem implements SCRTListener, Packable {
	public List<SCRTListener> listeners = new ArrayList<>();
	protected List<SRCTEvent> Queue = new ArrayList<>();
	protected boolean EventsMuted = false;
	public static PacketManager PacketManager = new PacketManager();
	public CTCItem()
	{
		synchronized(PacketManager.items)
		{
			PacketManager.items.add(this);
		}
	}
	public abstract ID getID();
	public static CTCItem findId(ID id)
	{
		synchronized(PacketManager.items)
		{
			for(Packable p : PacketManager.items)
			{
				if(id.equals(((CTCItem)p).getID())) return (CTCItem)p;
			}
			return null;
		}
	}
}
