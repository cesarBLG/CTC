package scrt.ctc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.Serial;
import scrt.com.packet.ID;
import scrt.com.packet.JunctionData;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.JunctionSwitch;
import scrt.com.packet.Packet;
import scrt.com.packet.TrackItemID;
import scrt.event.SRCTListener;
import scrt.event.OccupationEvent;
import scrt.gui.JunctionIcon;

public class Junction extends TrackItem 
{
	public int Number;
	public Orientation Direction;
	public Position Switch = Position.Straight;
	public Position Class;
	public int Locked = -1;
	public int Muelle = -1;
	TrackItem BackItem;
	TrackItem FrontItems[] = new TrackItem[2];
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
		TrackItemID id = new TrackItemID();
		id.x = x;
		id.y = y;
		JunctionRegister reg = new JunctionRegister((JunctionID) getId(), id);
		reg.Direction = Direction;
		reg.Class = Class;
		icon = new JunctionIcon(reg);
		updateState();
	}
	public void userChangeSwitch()
	{
		if(Locked!=-1) return;
		if(Muelle!=-1) Muelle = 1-Muelle;
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
	public boolean LockedFor(TrackItem t)
	{
		if(BlockState == Orientation.None&&Locked == -1) return true;
		if(Locked!=-1&&((t==FrontItems[0]&&Locked==0)||(t==FrontItems[1]&&Locked==1)||t==BackItem)) return true;
		return false;
	}
	boolean locking = false;
	public void lock(TrackItem t)
	{
		if(Locked == -1)
		{
			if(locking) return;
			locking = true;
			Timer timer = new Timer(2000, new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent arg0)
						{
							if(t==BackItem) Locked = Switch==Position.Straight ? 0 : 1;
							else
							{
								if(FrontItems[0]==t) Locked = 0;
								else Locked = 1;
							}
							locking = false;
							Orientation o = BlockState;
							BlockState = Orientation.None;
							setBlock(o);
						}
					});
			timer.setRepeats(false);
			timer.start();
		}
	}
	@Override
	public void setBlock(Orientation o) 
	{
		if(o==Orientation.None&&Occupied==Orientation.None) Locked = -1;
		super.setBlock(o);
	}
	boolean wasFree = true;
	@Override
	public void AxleDetected(AxleCounter c, Orientation dir)
	{
		wasFree = Occupied == Orientation.None;
		super.AxleDetected(c, dir);
	}
	@Override
	public void PerformAction(AxleCounter c, Orientation dir)
	{
		super.PerformAction(c, dir);
		if(Occupied==Direction) Locked = Switch==Position.Straight ? 0 : 1;
		else if(Occupied!=Orientation.None)
		{
			if(wasFree)
			{
				if(StraightOccupier.contains(c) && dir != Direction)
				{
					Switch = Position.Straight;
					updatePosition();
					Locked = 0;
				}
				else if(CurveOccupier.contains(c) && dir != Direction)
				{
					Switch = Class;
					updatePosition();
					Locked = 1;
				}
			}
		}
		if(Occupied==Orientation.None && Muelle!=-1) setSwitch(Muelle == 0 ? Position.Straight : Class);
		if(BlockState==Orientation.None&&Occupied==Orientation.None) Locked = -1;
		updateState();
	}
	@Override
	public void updateState()
	{
		JunctionData d = new JunctionData((JunctionID) getId());
		d.Acknowledged = Acknowledged;
		d.BlockState = BlockState;
		d.Occupied = Occupied;
		d.EvenAxles = EvenAxles;
		d.OddAxles = OddAxles;
		d.Locked = Locked;
		d.Switch = Switch;
		COM.send(d);
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
	public void updatePosition()
	{
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
		if(Occupied==Orientation.None&&Muelle!=-1) Switch = Muelle == 0 ? Position.Straight : Class;
		else if(Switch!=p&&Occupied==Orientation.None&&BlockState==Orientation.None&&Locked==-1)
		{
			Switch = p;
			if(Linked!=null&&Linked.Switch != Switch) Linked.setSwitch(p);
		}
		else return false;
		updatePosition();
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
		if(p instanceof JunctionSwitch) userChangeSwitch();
	}
	@Override
	public ID getId()
	{
		JunctionID i = new JunctionID();
		i.stationNumber = Station.AssociatedNumber;
		i.Number = Number;
		i.Name = Name;
		return i;
	}
}
