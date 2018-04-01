package scrt.ctc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.JunctionData;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionLock;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.JunctionSwitch;
import scrt.com.packet.Packet;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.StatePacket;
import scrt.com.packet.TrackItemID;
import scrt.event.BlockEvent;
import scrt.event.OccupationEvent;
import scrt.event.SRCTListener;

public class Junction extends TrackItem 
{
	public int Number;
	public Orientation Direction;
	public Position Switch = Position.Straight;
	public Position Class;
	public int blockPosition = -1;
	public int Locked = -1;
	public int Muelle = -1;
	TrackItem BackItem;
	TrackItem FrontItems[] = new TrackItem[2];
	public Junction CrossingLinked = null;
	AxleCounter ReleaseCounter[] = new AxleCounter[2];
	List<AxleCounter> StraightOccupier = new ArrayList<AxleCounter>();
	List<AxleCounter> CurveOccupier = new ArrayList<AxleCounter>();
	Junction Linked = null;
	public Junction(int num, Station dep, Position p, int x, int y)
	{
		this.x = x;
		this.y = y;
		Number = num;
		Station = dep;
		Class = p;
		Direction = num%2==0 ? Orientation.Even : Orientation.Odd;
		send(PacketType.JunctionRegister);
		updateState();
	}
	void send(PacketType type)
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
				d.Occupied = Occupied;
				d.Locked = Locked;
				d.blockPosition = blockPosition;
				d.Switch = Switch;
				d.locking = locking;
				p = d;
				break;
			default:
				return;
		}
		COM.toSend(p);
	}
	public void userChangeSwitch()
	{
		if(Locked!=-1) return;
		if(Muelle!=-1)
		{
			Muelle = 1-Muelle;
			updatePosition(Position.Straight);
			return;
		}
		if(Switch==Position.Straight) setSwitch(Class);
		else setSwitch(Position.Straight);
	}
	@Override
	public TrackItem getNext(Orientation o)
	{
		if(Direction!=o) return BackItem;
		else return FrontItems[Switch==Position.Straight ? 0 : 1];
	}
	@Override
	public TrackItem getNext(TrackItem t)
	{
		if((t==FrontItems[0])||(t==FrontItems[1])) return BackItem;
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
	public boolean LockedFor(TrackItem t, boolean check)
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
			sendLockOrder(val);
			if(val == 0 && Linked != null) Linked.lock(val);
			updateState();
		}
	}
	void sendLockOrder(int val)
	{
		Timer timer = new Timer(1000, new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						JunctionLock l = new JunctionLock(getID());
						l.order = false;
						l.value = val;
						load(l);
					}
				});
		timer.setRepeats(false);
		timer.start();
	}
	void updateLock(int lock)
	{
		if(Locked == lock) return;
		locking = false;
		Locked = lock;
		blockChanged();
	}
	void tryToUnlock()
	{
		if(Locked == -1) return;
		if(BlockState==Orientation.None&&(Occupied==Orientation.None||Occupied==Orientation.Unknown))
		{
			if(Linked == null || (Linked.BlockState==Orientation.None&&(Linked.Occupied==Orientation.None||Linked.Occupied==Orientation.Unknown)))
			{
				Locked = -1;
				if(Linked != null) Linked.tryToUnlock();
				blockChanged();
			}
		}
	}
	@Override
	public void setBlock(Orientation o) 
	{
		if(o == Orientation.None && Occupied == Orientation.None) blockPosition = -1;
		super.setBlock(o);
		tryToUnlock();
	}
	@Override
	public void blockChanged()
	{
		super.blockChanged();
		if(CrossingLinked!=null)
		{
			List<SRCTListener> list = new ArrayList<SRCTListener>(); 
			list.addAll(CrossingLinked.listeners);
			for(SRCTListener l : list)
			{
				l.actionPerformed(new BlockEvent(this, BlockState));
			}
		}
	}
	@Override
	public void PerformAction(AxleCounter c, Orientation dir)
	{
		if(Occupied==Direction)
		{
			blockPosition = Switch==Position.Straight ? 0 : 1;
		}
		else if(Occupied==Orientation.OppositeDir(Direction))
		{
			if(wasFree)
			{
				if(StraightOccupier.contains(c) && dir != Direction)
				{
					blockPosition = 0;
					updatePosition(Position.Straight);
				}
				else if(CurveOccupier.contains(c) && dir != Direction)
				{
					blockPosition = 1;
					updatePosition(Class);
				}
			}
		}
		super.PerformAction(c, dir);
		if(Occupied==Orientation.None && Muelle!=-1) updatePosition(Muelle == 0 ? Position.Straight : Class);
		if(Occupied==Orientation.None && BlockState == Orientation.None) blockPosition = -1;
		tryToUnlock();
		updateState();
	}
	@Override
	public void updateState()
	{
		send(PacketType.JunctionData);
	}
	@Override
	public void setCounters(Orientation dir)
	{
		List<AxleCounter> l = new ArrayList<AxleCounter>();
		l.addAll(EvenOccupier);
		l.addAll(OddOccupier);
		l.add(EvenRelease);
		l.add(OddRelease);
		EvenOccupier.clear();
		OddOccupier.clear();
		StraightOccupier.clear();
		CurveOccupier.clear();
		ReleaseCounter[0] = ReleaseCounter[1] = null;
		EvenRelease = null;
		OddRelease = null;
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.listeners.remove(this);
		}
		if(BackItem!=null)
		{
			if(BackItem.CounterLinked!=null && BackItem.CounterDir==Direction)
			{
				if(Direction==Orientation.Even)
				{
					if(BackItem.getNext(Direction)==this) EvenOccupier.add(BackItem.CounterLinked);
					OddRelease = BackItem.CounterLinked;
				}
				else
				{
					if(BackItem.getNext(Direction)==this) OddOccupier.add(BackItem.CounterLinked);
					EvenRelease = BackItem.CounterLinked;
				}
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(BackItem.getNext(Direction)==this) EvenOccupier.addAll(BackItem.EvenOccupier);
					OddRelease = BackItem.OddRelease;
				}
				else
				{
					if(BackItem.getNext(Direction)==this) OddOccupier.addAll(BackItem.OddOccupier);
					EvenRelease = BackItem.EvenRelease;
				}
			}
		}
		if(FrontItems[0]!=null)
		{
			if(FrontItems[0].CounterLinked!=null && FrontItems[0].CounterDir!=Direction)
			{
				if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.add(FrontItems[0].CounterLinked);
				ReleaseCounter[0] = FrontItems[0].CounterLinked;
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.addAll(FrontItems[0].OddOccupier);
					ReleaseCounter[0] = FrontItems[0].EvenRelease;
				}
				else
				{
					if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.addAll(FrontItems[0].EvenOccupier);
					ReleaseCounter[0] = FrontItems[0].OddRelease;
				}
			}
		}
		if(FrontItems[1]!=null)
		{
			if(FrontItems[1].CounterLinked!=null && FrontItems[1].CounterDir!=Direction)
			{
				if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.add(FrontItems[1].CounterLinked);
				ReleaseCounter[1] = FrontItems[1].CounterLinked;
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.addAll(FrontItems[1].OddOccupier);
					ReleaseCounter[1] = FrontItems[1].EvenRelease;
				}
				else
				{
					if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.addAll(FrontItems[1].EvenOccupier);
					ReleaseCounter[1] = FrontItems[1].OddRelease;
				}
			}
		}
		if(Direction==Orientation.Even)
		{
			if(Switch==Position.Straight) EvenRelease = ReleaseCounter[0];
			else EvenRelease = ReleaseCounter[1];
			OddOccupier.addAll(StraightOccupier);
			OddOccupier.addAll(CurveOccupier);
		}
		else
		{
			if(Switch==Position.Straight) OddRelease = ReleaseCounter[0];
			else OddRelease = ReleaseCounter[1];
			EvenOccupier.addAll(StraightOccupier);
			EvenOccupier.addAll(CurveOccupier);
		}
		l.clear();
		l.add(EvenRelease);
		l.addAll(EvenOccupier);
		l.add(OddRelease);
		l.addAll(OddOccupier);
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.addListener(this);
		}
		if(dir==Orientation.Both)
		{
			if(FrontItems[0]!=null) FrontItems[0].setCounters(Direction);
			if(FrontItems[1]!=null) FrontItems[1].setCounters(Direction);
			if(BackItem!=null) BackItem.setCounters(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even);
		}
		else if(dir==Direction)
		{
			if(FrontItems[0]!=null) FrontItems[0].setCounters(Direction);
			if(FrontItems[1]!=null) FrontItems[1].setCounters(Direction);
		}
		else if(dir!=Orientation.None)
		{
			if(BackItem!=null) BackItem.setCounters(dir);
		}
	}
	public void updatePosition(Position p)
	{
		if(Occupied==Orientation.None&&Muelle!=-1) Switch = Muelle == 0 ? Position.Straight : Class;
		else Switch = p;
		updateState();
		setCounters(Orientation.Both);
		List<SRCTListener> list = new ArrayList<SRCTListener>();
		list.addAll(listeners);
		for(SRCTListener l : list)
		{
			l.actionPerformed(new OccupationEvent(this, Orientation.None, 0));
		}
	}
	public boolean setSwitch(Position p)
	{
		//if(!Station.Opened) return false;
		if(!(Switch!=p&&Occupied==Orientation.None&&BlockState==Orientation.None&&Locked==-1)) return false;
		updatePosition(p);
		if(Linked!=null&&Linked.Switch != p) Linked.setSwitch(p);
		return true;
	}
	public boolean connectsTo(Orientation dir, TrackItem t)
	{
		return connectsTo(dir, t.x, t.y, dir == Orientation.Even ? t.EvenRotation : t.OddRotation);
	}
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
			l = new ArrayList<TrackItem>();
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
			l = getNext(dir).path(destination, dir, false);
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
				if(((JunctionSwitch) p).force)
				{
					updatePosition(Switch == Position.Straight ? Class : Position.Straight);
				}
				else userChangeSwitch();
			}
			if(p instanceof JunctionData)
			{
				JunctionData d = (JunctionData)p;
				if(d.BlockState == Orientation.None && BlockState == Orientation.Unknown) setBlock(Orientation.None);
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
