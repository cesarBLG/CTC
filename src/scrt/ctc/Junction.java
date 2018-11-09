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
import java.util.Collections;
import java.util.List;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.JunctionData;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionLock;
import scrt.com.packet.JunctionPositionSwitch;
import scrt.com.packet.JunctionPositionSwitch.Posibilities;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.JunctionSwitch;
import scrt.com.packet.Packet;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.StatePacket;
import scrt.com.packet.TrackItemID;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;
import scrt.ctc.TrackItem.TrackComparer;
import scrt.event.AxleEvent;
import scrt.event.BlockEvent;
import scrt.event.OccupationEvent;
import scrt.event.SCRTListener;

public class Junction extends TrackItem 
{
	public enum Position
	{
		Straight,
		Left,
		Right, 
		Unknown,
	}
	public int Number;
	public Orientation Direction;
	public Position Switch = Position.Unknown;
	Position target = Position.Unknown;
	public Position Class;
	public int blockPosition = -1;
	public int Locked = -1;
	int lockTarget = -1;
	TrackItem BackItem;
	TrackItem FrontItems[] = new TrackItem[2];
	public Junction CrossingLinked = null;
	Junction Linked = null;
	public Junction(JunctionRegister reg)
	{
		x = reg.TrackId.x;
		y = reg.TrackId.y;
		JunctionID id = (JunctionID)reg.id;
		Number = id.Number;
		Class = reg.Class;
		Direction = reg.Direction;
		Station = scrt.ctc.Station.byNumber(id.stationNumber);
		send(PacketType.JunctionRegister);
		send(PacketType.JunctionPositionSwitch, false);
		updateState();
	}
	public Junction(int num, Station dep, Position p, int x, int y)
	{
		this.x = x;
		this.y = y;
		Number = num;
		Station = dep;
		Class = p;
		Direction = num%2==0 ? Orientation.Even : Orientation.Odd;
		send(PacketType.JunctionRegister);
		send(PacketType.JunctionPositionSwitch, false);
		updateState();
	}
	@Override
	void send(PacketType type, Object... extra)
	{
		Packet p;
		switch(type)
		{
			case JunctionRegister:
				TrackItemID id = new TrackItemID();
				id.x = x;
				id.y = y;
				id.stationNumber = Station.AssociatedNumber;
				JunctionRegister reg = new JunctionRegister(getID(), id);
				reg.Direction = Direction;
				reg.Class = Class;
				p = reg;
				break;
			case JunctionData:
				JunctionData d = new JunctionData(getID());
				d.BlockState = BlockState;
				d.shunt = shunt;
				d.Occupied = Occupied;
				d.Locked = Locked;
				d.blockPosition = blockPosition;
				d.Switch = Switch;
				d.locking = locking;
				p = d;
				break;
			case JunctionPositionSwitch:
				if(extra.length!=1 || !(extra[0] instanceof Boolean)) throw new IllegalArgumentException("Extra parameter must be a boolean");
				JunctionPositionSwitch jps = new JunctionPositionSwitch(getID(), (boolean)extra[0] ? Posibilities.Order : Posibilities.Request);
				jps.position = target;
				p = jps;
				break;
			case JunctionLock:
				JunctionLock jl = new JunctionLock(getID());
				jl.order = true;
				jl.value = lockTarget;
				p = jl;
				break;
			default:
				return;
		}
		COM.toSend(p);
	}
	public void userChangeSwitch()
	{
		if(Locked!=-1) return;
		//TODO: Spring needs redefinition. Maybe it is unnecessary
		/*if(Muelle!=-1)
		{
			Muelle = 1-Muelle;
			updatePosition(Position.Straight);
			return;
		}*/
		if(Switch==Position.Straight) setSwitch(Class);
		else setSwitch(Position.Straight);
	}
	@Override
	public TrackItem getNext(Orientation o)
	{
		if(Switch==Position.Unknown) send(PacketType.JunctionPositionSwitch, false);
		if(Direction!=o) return BackItem;
		if(Switch==Position.Unknown || target!=Switch) return null;
		return FrontItems[Switch==Position.Straight ? 0 : 1];
	}
	@Override
	public TrackItem getNext(TrackItem t)
	{
		if((t==FrontItems[0])||(t==FrontItems[1])) return BackItem;
		if(Switch==Position.Unknown || target!=Switch) return null;
		if(t==BackItem) return FrontItems[Switch==Position.Straight ? 0 : 1];
		return null;
	}
	public boolean canGetFrom(TrackItem prev)
	{
		if(prev == BackItem) return true;
		if((prev == FrontItems[0]) ^ (Switch==Position.Straight)) return false;
		return true;
	}
	public boolean blockedFor(TrackItem t, boolean check)
	{
		if(blockPosition == -1 && !check) return true;
		if(blockPosition!=-1&&((t==FrontItems[0]&&blockPosition==0)||(t==FrontItems[1]&&blockPosition==1)||t==BackItem)) return true;
		return false;
	}
	public boolean lockedFor(TrackItem t, boolean check)
	{
		if(Locked == -1 && !check) return true;
		if(Locked!=-1&&((t==FrontItems[0]&&Locked==0)||(t==FrontItems[1]&&Locked==1)||t==BackItem)) return true;
		return false;
	}
	boolean locking = false;
	public void block(TrackItem t)
	{
		if(blockPosition == -1)
		{
			if(t==BackItem) blockPosition = Switch==Position.Straight ? 0 : 1;
			else
			{
				if(FrontItems[0]==t) blockPosition = 0;
				else blockPosition = 1;
			}
		}
	}
	public void lock(TrackItem t)
	{
		int val;
		if(t==BackItem) val = Switch==Position.Straight ? 0 : 1;
		else
		{
			if(FrontItems[0]==t) val = 0;
			else val = 1;
		}
		lock(val);
	}
	public void lock(int val)
	{
		if(Locked == -1)
		{
			if(locking) return;
			locking = true;
			lockTarget = val;
			send(PacketType.JunctionLock);
			if(val == 0 && Linked != null) Linked.lock(val);
			updateState();
		}
	}
	void updateLock(int lock)
	{
		if(Locked == lock) return;
		locking = false;
		Locked = lock;
		blockChanged();
	}
	public void tryToUnlock()
	{
		if(Locked == -1) return;
		if((BlockState==Orientation.None||BlockingItem==null)&&(Occupied==Orientation.None||Occupied==Orientation.Unknown))
		{
			if(Linked == null || ((Linked.BlockState==Orientation.None||Linked.BlockingItem==null)&&(Linked.Occupied==Orientation.None||Linked.Occupied==Orientation.Unknown)))
			{
				Locked = -1;
				if(Linked != null)
				{
					Linked.tryToUnlock();
					Linked.blockChanged(); //TODO: I hope doing this doesn't produce strange bugs
				}
				blockChanged();
			}
		}
	}
	@Override
	public void setBlock(Orientation o) 
	{
		if(o == Orientation.None && Occupied == Orientation.None) blockPosition = -1;
		if(BlockState == o) return;
		BlockState = o;
		if(BlockState == Orientation.None) BlockingItem = null;
		tryToUnlock();
		blockChanged();
	}
	@Override
	public void tryToFree(AxleEvent ae)
	{
		super.tryToFree(ae);
		tryToUnlock();
	}
	@Override
	void setOverlapBlock()
	{
		if(BlockingItem != null) return;
		boolean str = false;
		boolean desv = false;
		for(TrackItem overlap : overlaps.keySet())
		{
			if(FrontItems[0].overlaps.containsKey(overlap)) str = true;
			if(FrontItems[1].overlaps.containsKey(overlap)) desv = true;
		}
		if(str && desv) blockPosition = 2;
		else if(str) blockPosition = 0;
		else if(desv) blockPosition = 1;
		else block(BackItem);
		super.setOverlapBlock();
	}
	@Override
	public void blockChanged()
	{
		super.blockChanged();
		if(CrossingLinked!=null)
		{
			List<SCRTListener> list = new ArrayList<>(); 
			list.addAll(CrossingLinked.listeners);
			for(SCRTListener l : list)
			{
				l.actionPerformed(new BlockEvent(this, BlockState));
			}
		}
	}
	@Override
	public void AxleActions(AxleEvent ae)
	{
		if(Occupied==Direction)
		{
			blockPosition = Switch==Position.Straight ? 0 : 1;
			if(Config.lock && Locked == -1) Locked = Switch==Position.Straight ? 0 : 1;
		}
		else if(Occupied==Orientation.OppositeDir(Direction))
		{
			if(wasFree && !ae.release && ae.dir != Direction)
			{
				if(ae.previous == FrontItems[0])
				{
					blockPosition = 0;
					updatePosition(Position.Straight);
				}
				else if(ae.previous == FrontItems[1])
				{
					blockPosition = 1;
					updatePosition(Class);
				}
				if(Config.lock && Locked == -1) Locked = Switch==Position.Straight ? 0 : 1;
			}
		}
		super.AxleActions(ae);
		if(Occupied==Orientation.None && !wasFree)
		{
			Switch = Position.Unknown;
			send(PacketType.JunctionPositionSwitch, false);
		}
		if(Occupied==Orientation.None && BlockState == Orientation.None) blockPosition = -1;
		tryToUnlock();
	}
	@Override
	public void updateState()
	{
		send(PacketType.JunctionData);
	}
	public void updatePosition(Position p)
	{
		Switch = p;
		target = p;
		OddItem = getNext(Orientation.Odd);
		EvenItem = getNext(Orientation.Even);
		updateState();
		List<SCRTListener> list = new ArrayList<>();
		list.addAll(listeners);
		for(SCRTListener l : list)
		{
			l.actionPerformed(new OccupationEvent(this, Orientation.None, 0));
		}
		if(!overlaps.isEmpty())
		{
			if(BlockState == Direction)
			{
				blockPosition = Switch == Position.Straight ? 0 : 1;
				blockChanged();
			}
			TrackItem.DirectExploration(FrontItems[Switch == Position.Straight ? 1 : 0], new TrackComparer()
			{
				@Override
				public boolean condition(TrackItem t, Orientation dir, TrackItem p) 
				{
					if(t == null) return false;
					boolean contains = false;
					for(TrackItem i : overlaps.keySet())
					{
						if(overlaps.get(i)==Direction && t.overlaps.containsKey(i))
						{
							contains = true;
							t.removeOverlap(i);
						}
					}
					return contains;
				}
				@Override
				public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) 
				{
					return true;
				}
				
			}, BlockState);
			for(TrackItem i : overlaps.keySet())
			{
				if(overlaps.get(i) == Direction) TrackItem.DirectExploration(FrontItems[blockPosition], new MainSignal.BlockOverlap(FrontItems[blockPosition], i), BlockState);
			}
		}
	}
	public boolean setSwitch(TrackItem from, TrackItem to)
	{
		if(from==FrontItems[0] || to==FrontItems[0]) return setSwitch(Position.Straight);
		if(from==FrontItems[1] || to==FrontItems[1]) return setSwitch(Class);
		return false;
	}
	public boolean setSwitch(Position p)
	{
		//if(!Station.Opened) return false;
		if(Switch==p||Occupied!=Orientation.None||Locked!=-1) return false;
		if(BlockingItem != null) return false;
		if(!overlaps.isEmpty())
		{
			if(BlockState == Direction || BlockState == Orientation.Both)
			{
				TrackItem next = FrontItems[p == Position.Straight ? 0 : 1];
				for(TrackItem overlap : overlaps.keySet())
				{
					if(overlaps.get(overlap) == Direction)
					{
						Signal lastsig = overlap.getNext(overlap.BlockState).SignalLinked;
						if(TrackItem.DirectExploration(next, new MainSignal.OverlapAvailable(next, false, lastsig), BlockState)==null) return false;
					}
				}
			}
		}
		target = p;
		send(PacketType.JunctionPositionSwitch, true);
		if(Linked!=null&&Linked.Switch != p && Linked.target != p) Linked.setSwitch(p);
		return true;
	}
	@Override
	public boolean connectsTo(Orientation dir, TrackItem t)
	{
		return connectsTo(dir, t.x, t.y, dir == Orientation.Even ? t.EvenRotation : t.OddRotation);
	}
	@Override
	public boolean connectsTo(Orientation dir, int objx, int objy, int objrot)
	{
		if(super.connectsTo(dir, objx, objy, objrot)) return true;
		if(dir!=Direction)
		{
			if(dir == Orientation.Even)
			{
				return x == objx + 1 && y == objy - (Class == Position.Left ? 1 : -1);
			}
			if(dir == Orientation.Odd)
			{
				return x == objx - 1 && y == objy + (Class == Position.Left ? 1 : -1);
			}
		}
		return false;
	}
	@Override
	public List<TrackItem> path(TrackItem destination, Orientation dir, boolean start)
	{
		List<TrackItem> l = null;
		if(destination == this)
		{
			l = new ArrayList<>();
			l.add(this);
			return l;
		}
		if(dir != Direction)
		{
			if(BackItem==null) return null;
			l = BackItem.path(destination, dir, false);
		}
		else
		{
			int d = pathDepth;
			l = FrontItems[Switch == Position.Straight ? 0 : 1].path(destination, dir, false);
			pathDepth = d;
			List<TrackItem> x = null;
			if(FrontItems[Switch == Position.Straight ? 1 : 0]!=null) x = FrontItems[Switch == Position.Straight ? 1 : 0].path(destination, dir, false);
			pathDepth = d;
			if(l == null || (x!=null && x.size() + 5 < l.size())) l = x;
		}
		if(l==null) return null;
		l.add(this);
		if(start) Collections.reverse(l);
		return l;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof StatePacket)
		{
			if(!((StatePacket)p).id.equals(getID())) return;
			if(p instanceof JunctionLock)
			{
				JunctionLock l = (JunctionLock)p;
				if(!l.order) updateLock(l.value);
			}
			if(p instanceof JunctionSwitch)
			{
				/*if(((JunctionSwitch) p).force)
				{
					updatePosition(Switch == Position.Straight ? Class : Position.Straight);
				}
				else if(((JunctionSwitch) p).muelle)
				{
					if(Muelle == -1) Muelle = Switch == Position.Straight ? 0 : 1;
					else Muelle = -1;
				}
				else */userChangeSwitch();
			}
			if(p instanceof JunctionData)
			{
				JunctionData d = (JunctionData)p;
				if(d.BlockState == Orientation.None && BlockState == Orientation.Unknown) setBlock(Orientation.None);
			}
			if(p instanceof JunctionPositionSwitch)
			{
				if((((JunctionPositionSwitch) p).orderType == Posibilities.Comprobation) && (Occupied==Orientation.None || Occupied==Orientation.Unknown)) updatePosition(((JunctionPositionSwitch) p).position);
			}
		}
	}
	@Override
	public JunctionID getID()
	{
		JunctionID i = new JunctionID();
		i.stationNumber = Station.AssociatedNumber;
		i.Number = Number;
		i.Name = Name;
		return i;
	}
}
