package scrt.ctc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.ACID;
import scrt.com.packet.ID;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.SignalID;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;
import scrt.event.AxleEvent;
import scrt.event.BlockEvent;
import scrt.event.EventType;
import scrt.event.OccupationEvent;
import scrt.event.SCRTListener;
import scrt.event.SRCTEvent;
import scrt.train.Train;

public class TrackItem extends CTCItem{
	public Orientation BlockState = Orientation.None;
	public Orientation Occupied = Orientation.None;
	public Signal SignalLinked = null;
	public AxleCounter CounterLinked = null;
	public Station Station;
	public String Name = "";
	Orientation CounterDir = Orientation.None;
	public TrackItem EvenItem = null;
	public TrackItem OddItem = null;
	public int EvenAxles = 0;
	public int OddAxles = 0;
	public int x = 0;
	public int y = 0;
	public int OddRotation = 0;
	public int EvenRotation = 0;
	public boolean Acknowledged = true;
	public boolean invert = false;
	List<Train> trains = new ArrayList<Train>();
	public void setSignal(Signal sig)
	{
		SignalLinked = sig;
		COM.toSend(new LinkPacket(getID(), SignalLinked.getID()));
	}
	TrackItem()
	{
		
	}
	public TrackItem(TrackRegister reg)
	{
		TrackItemID id = (TrackItemID) reg.id;
		x = id.x;
		y = id.y;
		OddRotation = reg.OddRotation;
		EvenRotation = reg.EvenRotation;
		Name = reg.Name;
		Station = scrt.ctc.Station.byNumber(id.stationNumber);
		send(PacketType.TrackRegister);
		updateState();
	}
	public TrackItem(String label, Station dep, int oddrot, int evenrot, int x, int y)
	{
		this.x = x;
		this.y = y;
		Station = dep;
		if(oddrot==2) oddrot = -1;
		if(evenrot==2) evenrot = -1;
		OddRotation = oddrot;
		EvenRotation = evenrot;
		Name = label;
		send(PacketType.TrackRegister);
		updateState();
	}
	public TrackItem getNext(Orientation dir)
	{
		if(dir==Orientation.Even) return EvenItem;
		if(dir==Orientation.Odd) return OddItem;
		return null;
	}
	public TrackItem getNext(TrackItem t)
	{
		if(t==OddItem) return EvenItem;
		if(t==EvenItem) return OddItem;
		return null;
	}
	public MainSignal BlockingSignal;
	long BlockingTime = 0;
	public void setBlock(Orientation o, MainSignal blocksignal)
	{
		BlockingSignal = blocksignal;
		BlockingTime = Clock.time();
		setBlock(o);
	}
	public void setBlock(Orientation o)
	{
		if(BlockState == o) return;
		BlockState = o;
		blockChanged();
	}
	void blockChanged()
	{
		if(EvenItem != null && EvenItem.SignalLinked!=null)
		{
			EvenItem.SignalLinked.actionPerformed(new BlockEvent(this, BlockState));
		}
		if(OddItem != null && OddItem.SignalLinked!=null)
		{
			OddItem.SignalLinked.actionPerformed(new BlockEvent(this, BlockState));
		}
		List<SCRTListener> list = new ArrayList<SCRTListener>(); 
		list.addAll(listeners);
		for(SCRTListener l : list)
		{
			l.actionPerformed(new BlockEvent(this, BlockState));
		}
		updateState();
	}
	public void updateState()
	{
		send(PacketType.TrackData);
	}
	boolean wasFree = true;
	long OccupiedTime = 0;
	public void AxleRun(AxleEvent ae)
	{
		updateOccupancy(ae);
		AxleActions(ae);
		updateState();
	}
	void AxleActions(AxleEvent ae)
	{
		tryToFree();
	}
	Timer trainStopTimer = new Timer(10000, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					List<SCRTListener> list = new ArrayList<SCRTListener>();
					list.addAll(listeners);
					for(SCRTListener l : list)
					{
						l.actionPerformed(new OccupationEvent(TrackItem.this, Orientation.Unknown, 0));
					}
				}
			});
	void updateOccupancy(AxleEvent ae)
	{
		if(ae.release&&ae.dir==Orientation.Even)
		{
			if(EvenAxles>0) EvenAxles--;
			else if(OddAxles>0) OddAxles--;
			//else if(getNext(ae.dir)==null || getNext(ae.dir).getReleaseCounter(ae.dir) != a) OddAxles = EvenAxles = -1;
		}
		else if(ae.release&&ae.dir==Orientation.Odd)
		{
			if(OddAxles>0) OddAxles--;
			else if(EvenAxles>0) EvenAxles--;
			//else if(getNext(ae.dir)==null || getNext(ae.dir).getReleaseCounter(ae.dir) != a) OddAxles = EvenAxles = -1;
		}
		if(!ae.release&&ae.dir==Orientation.Even)
		{
			if(EvenAxles==0)
			{
				OccupiedTime = Clock.time();
				trainStopTimer.setInitialDelay(Station.AssociatedNumber == 0 ? 20000 : 10000);
				trainStopTimer.setRepeats(false);
				trainStopTimer.start();
			}
			EvenAxles++;
		}
		else if(!ae.release&&ae.dir==Orientation.Odd)
		{
			if(OddAxles==0)
			{
				OccupiedTime = Clock.time();
				trainStopTimer.setInitialDelay(Station.AssociatedNumber == 0 ? 20000 : 10000);
				trainStopTimer.setRepeats(false);
				trainStopTimer.start();
			}
			OddAxles++;
		}
		/*if(OddAxles == -1 && EvenAxles == -1)
		{
			OddAxles = EvenAxles = 0;
			List<AxleCounter> acs = new ArrayList<AxleCounter>(getOccupierCounter(ae.dir));
			for(AxleCounter ac : acs)
			{
				ac.Working = false;
				ac.Passed(ae.dir);
			}
			return;
		}*/
		if(EvenAxles>0&&OddAxles>0) Occupied = Orientation.Both;
		else if(EvenAxles>0) Occupied = Orientation.Even;
		else if(OddAxles>0) Occupied = Orientation.Odd;
		else if(Occupied!=Orientation.None && Occupied!=Orientation.Unknown) Occupied = Orientation.None;
		if(wasFree && ((Occupied == Orientation.Even && EvenItem!=null && !EvenItem.Station.equals(Station) && EvenItem.Station.isOpen())||(Occupied == Orientation.Odd && OddItem!=null && !OddItem.Station.equals(Station) && OddItem.Station.isOpen())))
		{
			/*if(!trains.isEmpty())
			{
				GRP grp = (Occupied == Orientation.Even ? EvenItem : OddItem).Station.grp;
				grp.update();
			}*/
			Acknowledged = false;
		}
		if(Occupied == Orientation.None) Acknowledged = true;
		/*if(CrossingLinked != null)
		{
			if(Occupied == Orientation.None && CrossingLinked.Occupied == Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.None;
				CrossingLinked.updateOccupancy();
				List<SRCTListener> list = new ArrayList<SRCTListener>();
				list.addAll(CrossingLinked.listeners);
				for(SRCTListener l : list)
				{
					l.actionPerformed(new OccupationEvent(CrossingLinked, Orientation.None, 0));
				}
			}
			if(Occupied != Orientation.None && Occupied != Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.Unknown;
				CrossingLinked.updateState();
				List<SRCTListener> list = new ArrayList<SRCTListener>();
				list.addAll(CrossingLinked.listeners);
				for(SRCTListener l : list)
				{
					l.actionPerformed(new OccupationEvent(CrossingLinked, Orientation.None, 0));
				}
			}
		}*/
	}
	public boolean trainStopped()
	{
		return Occupied == Orientation.None || (Clock.time() - OccupiedTime) >= (Station.AssociatedNumber == 0 ? 20000 : 10000);
	}
	public TrackItem overlap = null;
	public void setOverlap(TrackItem t)
	{
		if(overlap==t || (t!=null && overlap!=null)) return;
		if(overlap==null) t.listeners.add(this);
		else overlap.listeners.remove(this);
		overlap = t;
		if(overlap == null) setBlock(Orientation.None);
		else setBlock(t.BlockState);
	}
	boolean Done = false;
	void tryToFree()
	{
		if(BlockState!=Orientation.Odd && BlockState!=Orientation.Even) return;
		if(overlap != null) return;
		if(BlockingSignal==null||!BlockingSignal.Locked)
		{
			if(Occupied == Orientation.None || BlockingTime<=OccupiedTime || BlockingSignal == null)
			{
				BlockState = Orientation.None;
				return;
			}
			/*if(TrackItem.InverseExploration(this, new TrackComparer()
					{
						@Override
						public boolean condition(TrackItem t, Orientation dir, TrackItem p)
						{
							if(t==null||(t.SignalLinked!=null&&t.SignalLinked instanceof MainSignal&&t.SignalLinked.Direction == BlockState)) return false;
							return true;
						}
						@Override
						public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
						{
							if(t.Occupied != BlockState && t.Occupied != Orientation.Both) return false;
							return true;
						}
					}, Orientation.OppositeDir(BlockState)) != null) setBlock(Orientation.None);*/
		}
		else if(BlockingSignal!=null)
		{
			if(TrackItem.DirectExploration(BlockingSignal.Linked, new TrackComparer()
					{
						@Override
						public boolean condition(TrackItem t, Orientation dir, TrackItem p)
						{
							if(t == TrackItem.this) return false;
							return true;
						}
						@Override
						public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
						{
							if(t==BlockingSignal.Linked) return true;
							if(t == null || t.SignalLinked!=null && t.SignalLinked instanceof MainSignal && t.SignalLinked.Direction == dir) return false;
							return true;
						}
					}, BlockState) != null) BlockState = Orientation.None;
		}
	}
	public interface TrackComparer
	{
		boolean condition(TrackItem t, Orientation dir, TrackItem p);
		boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p);
	}
	public static List<TrackItem> DirectExploration(TrackItem start, TrackComparer tc, Orientation dir)
	{
		List<TrackItem> list = new ArrayList<TrackItem>();
		TrackItem t = start;
		TrackItem p = null;
		while(true)
		{
			if(!tc.condition(t, dir, p)) break;
			if(!tc.criticalCondition(t, dir, p)) return null;
			list.add(t);
			if(t.invert && dir == Orientation.Even && p!=null && p.invert) dir = Orientation.Odd;
			p = t;
			t = t.getNext(dir);
		}
		return list;
	}
	public static List<TrackItem> InverseExploration(TrackItem start, TrackComparer tc, Orientation dir)
	{
		List<TrackItem> list = new ArrayList<TrackItem>();
		TrackItem t = start;
		TrackItem prev = null;
		while(true)
		{
			if(!tc.condition(t, dir, prev)) break;
			if(!tc.criticalCondition(t, dir, prev)) return null;
			list.add(t);
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(j.Direction == dir)
				{
					if(tc.condition(j.FrontItems[0], dir, prev)) list.addAll(InverseExploration(j.FrontItems[0], tc, dir));
					if(tc.condition(j.FrontItems[1], dir, prev)) list.addAll(InverseExploration(j.FrontItems[1], tc, dir));
					break;
				}
			}
			if(t.invert && dir == Orientation.Even && prev!= null && prev.invert) dir = Orientation.Odd;
			prev = t;
			t = t.getNext(dir);
			if(t == null) continue;
			if(!(t.invert && prev.invert) && prev!=t.getNext(Orientation.OppositeDir(dir))) break;
		}
		return list;
	}
	public boolean connectsTo(Orientation dir, TrackItem t)
	{
		return connectsTo(dir, t.x, t.y, dir == Orientation.Even ? t.EvenRotation : t.OddRotation);
	}
	public boolean connectsTo(Orientation dir, int objx, int objy, int objrot)
	{
		if(x==-8&&dir==Orientation.Even) return objy == y+2 && objx==48;
		if(x==48&&dir==Orientation.Odd) return objy == y-2 && objx==-8;
		if(dir == Orientation.Even)
		{
			if(objrot == OddRotation) return x == objx + 1 && y == objy - objrot;
		}
		if(dir == Orientation.Odd)
		{
			if(objrot == EvenRotation) return x == objx - 1 && y == objy + objrot;
		}
		return false;
	}
	public List<SCRTListener> listeners = new ArrayList<SCRTListener>();
	@Override
	public void actionPerformed(SRCTEvent e) 
	{
		if(!Muted)
		{
			if(e.type == EventType.AxleCounter)
			{
				var ae = (AxleEvent)e;
				if(!ae.release && SignalLinked!=null) SignalLinked.actionPerformed(ae); 
				AxleRun(ae);
			}
			if(overlap == e.creator && overlap.trainStopped() && overlap.BlockState != BlockState)
			{
				setOverlap(null);
			}
		}
	}
	boolean Muted = false;
	@Override
	public void muteEvents(boolean mute) {
		Muted = mute;
	}
	@Override
	public String toString()
	{
		return Integer.toString(x) + ", " + Integer.toString(y);
	}
	static int pathDepth = 0;
	public List<TrackItem> path(TrackItem destination, Orientation dir, boolean start)
	{
		if(pathDepth>70) return null;
		List<TrackItem> l = null;
		if(destination == this)
		{
			l = new ArrayList<TrackItem>();
			l.add(this);
			return l;
		}
		TrackItem item = dir == Orientation.Odd ? OddItem : EvenItem;
		if(item == null) return null;
		else
		{
			pathDepth++;
			l = item.path(destination, dir, false);
		}
		if(l==null) return null;
		l.add(this);
		if(start)
		{
			Collections.reverse(l);
			pathDepth = 0;
		}
		return l;
	}
	@Override
	public ID getID()
	{
		TrackItemID id = new TrackItemID();
		id.x = x;
		id.y = y;
		id.stationNumber = Station.AssociatedNumber;
		return id;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof TrackData)
		{
			TrackData d = (TrackData)p;
			if(!d.id.equals(getID())) return;
			if(d.BlockState == Orientation.None && BlockState == Orientation.Unknown) setBlock(Orientation.None);
		}
		if(p instanceof LinkPacket)
		{
			var link = (LinkPacket)p;
			if(link.id1.equals(getID()))
			{
				if(link.id2 instanceof SignalID) ((Signal)CTCItem.findId(link.id2)).setLinked(this);
				if(link.id2 instanceof ACID)
				{
					AxleCounter ac = (AxleCounter) CTCItem.findId(link.id2);
					if(ac == null)
					{
						ac = new AxleCounter((ACID)link.id2);
						//counters.add(ac);
					}
					setCounterLinked(ac, ac.Number % 2 == 0 ? Orientation.Even : Orientation.Odd);
				}
				if(link.id2 instanceof TrackItemID)
				{
					TrackItem t = (TrackItem) CTCItem.findId(link.id2);
					if(EvenItem==null && t.EvenItem == null)
					{
						t.EvenItem = this;
						EvenItem = t;
						invert = true;
						t.invert = true;
					}
					else if(OddItem==null && t.OddItem == null)
					{
						t.OddItem = this;
						OddItem = t;
						invert = true;
						t.invert = true;
					}
					else if(OddItem==null && t.EvenItem == null)
					{
						t.EvenItem = this;
						OddItem = t;
					}
					else if(EvenItem==null && t.OddItem == null)
					{
						t.OddItem = this;
						EvenItem = t;
					}
				}
			}
		}
	}
	void send(PacketType type)
	{
		Packet p;
		switch(type)
		{
			case TrackRegister:
				TrackRegister reg = new TrackRegister((TrackItemID) getID());
				reg.Name = Name;
				reg.OddRotation = OddRotation;
				reg.EvenRotation = EvenRotation;
				p = reg;
				break;
			case TrackData:
				TrackData d = new TrackData((TrackItemID) getID());
				d.Acknowledged = Acknowledged;
				d.BlockState = BlockState;
				d.Occupied = Occupied;
				d.EvenAxles = EvenAxles;
				d.OddAxles = OddAxles;
				p = d;
				break;
			default:
				return;
		}
		COM.toSend(p);
	}
	public void setCounterLinked(AxleCounter ac, Orientation dir)
	{
		ac.linked = this;
		CounterLinked = ac;
		CounterDir = dir;
		COM.toSend(new LinkPacket(getID(), ac.getID()));
	}
}
