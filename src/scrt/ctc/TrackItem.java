package scrt.ctc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.COM;
import scrt.com.packet.ID;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.Packet.PacketType;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;
import scrt.event.AxleEvent;
import scrt.event.BlockEvent;
import scrt.event.EventType;
import scrt.event.OccupationEvent;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.regulation.train.Train;

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
	AxleCounter EvenRelease = null;
	AxleCounter OddRelease = null;
	List<AxleCounter> EvenOccupier = new ArrayList<AxleCounter>();
	List<AxleCounter> OddOccupier = new ArrayList<AxleCounter>();
	public int x = 0;
	public int y = 0;
	public int OddRotation = 0;
	public int EvenRotation = 0;
	public boolean Acknowledged = true;
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
	public void setCounters(Orientation dir)
	{
		List<AxleCounter> l = new ArrayList<AxleCounter>();
		l.addAll(EvenOccupier);
		l.addAll(OddOccupier);
		l.add(EvenRelease);
		l.add(OddRelease);
		EvenOccupier.clear();
		OddOccupier.clear();
		EvenRelease = null;
		OddRelease = null;
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.listeners.remove(this);
		}
		l.clear();
		if(CounterLinked!=null)
		{
			if(CounterDir==Orientation.Even)
			{
				EvenRelease = CounterLinked;
				OddOccupier.add(CounterLinked);
			}
			if(CounterDir==Orientation.Odd)
			{
				OddRelease = CounterLinked;
				EvenOccupier.add(CounterLinked);
			}
		}
		TrackItem e = EvenItem;
		TrackItem o = OddItem;
		if(EvenRelease==null&&e!=null)
		{
			if(e.CounterLinked!=null && e.CounterDir!=Orientation.Even) EvenRelease = e.CounterLinked;
			else EvenRelease = e.EvenRelease;
		}
		if(OddRelease==null&&o!=null)
		{
			if(o.CounterLinked!=null && o.CounterDir!=Orientation.Odd) OddRelease = o.CounterLinked;
			else OddRelease = o.OddRelease;
		}
		if(EvenOccupier.isEmpty()&&o!=null&&o.getNext(Orientation.Even)==this)
		{
			if(o.CounterLinked!=null && o.CounterDir!=Orientation.Odd) EvenOccupier.add(o.CounterLinked);
			else EvenOccupier.addAll(o.EvenOccupier);
		}
		if(OddOccupier.isEmpty()&&e!=null&&e.getNext(Orientation.Odd)==this)
		{
			if(e.CounterLinked!=null && e.CounterDir!=Orientation.Even) OddOccupier.add(e.CounterLinked);
			else OddOccupier.addAll(e.OddOccupier);
		}
		l.add(EvenRelease);
		l.addAll(EvenOccupier);
		l.add(OddRelease);
		l.addAll(OddOccupier);
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.addListener(this);
		}
		if(EvenItem!=null&&dir==Orientation.Even&&CounterLinked==null) EvenItem.setCounters(dir);
		if(OddItem!=null&&dir==Orientation.Odd&&CounterLinked==null) OddItem.setCounters(dir);
	}
	public AxleCounter getReleaseCounter(Orientation dir)
	{
		if(dir==Orientation.Even) return EvenRelease;
		else return OddRelease;
	}
	public List<AxleCounter> getOccupierCounter(Orientation dir)
	{
		if(dir==Orientation.Even) return EvenOccupier;
		else return OddOccupier;
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
		if(BlockState==Orientation.None) BlockingTime = Clock.time();
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
		List<SRCTListener> list = new ArrayList<SRCTListener>(); 
		list.addAll(listeners);
		for(SRCTListener l : list)
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
	public void AxleDetected(AxleCounter a, Orientation dir) 
	{
		wasFree = Occupied == Orientation.None;
		if(!a.Working)
		{
			if((EvenOccupier.contains(a) || dir==Orientation.Odd) && (OddOccupier.contains(a) || dir == Orientation.Even))
			{
				OddAxles = EvenAxles = 0;
				Occupied = Orientation.Unknown;
				updateState();
			}
			return;
		}
		if(EvenRelease==a&&dir==Orientation.Even)
		{
			if(EvenAxles>0) EvenAxles--;
			else if(OddAxles>0) OddAxles--;
			else if(getNext(dir)==null || getNext(dir).getReleaseCounter(dir) != a) OddAxles = EvenAxles = -1;
		}
		else if(OddRelease==a&&dir==Orientation.Odd)
		{
			if(OddAxles>0) OddAxles--;
			else if(EvenAxles>0) EvenAxles--;
			else if(getNext(dir)==null || getNext(dir).getReleaseCounter(dir) != a) OddAxles = EvenAxles = -1;
		}
		if(EvenOccupier.contains(a)&&dir==Orientation.Even)
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
		else if(OddOccupier.contains(a)&&dir==Orientation.Odd)
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
		updateOccupancy();
	}
	Timer trainStopTimer = new Timer(10000, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					List<SRCTListener> list = new ArrayList<SRCTListener>();
					list.addAll(listeners);
					for(SRCTListener l : list)
					{
						l.actionPerformed(new OccupationEvent(TrackItem.this, Orientation.Unknown, 0));
					}
				}
			});
	void updateOccupancy()
	{
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
		updateState();
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
	public void PerformAction(AxleCounter a, Orientation dir)
	{
		if(OddAxles == -1 && EvenAxles == -1)
		{
			OddAxles = EvenAxles = 0;
			List<AxleCounter> acs = new ArrayList<AxleCounter>(getOccupierCounter(dir));
			for(AxleCounter ac : acs)
			{
				ac.Working = false;
				ac.Passed(dir);
			}
			return;
		}
		tryToFree();
		List<SRCTListener> list = new ArrayList<SRCTListener>();
		list.addAll(listeners);
		for(SRCTListener l : list)
		{
			l.actionPerformed(new OccupationEvent(this, dir, (dir == Orientation.Even ? EvenAxles : OddAxles)));
		}
		{
			OccupationEvent ev = new OccupationEvent(this, dir, (dir == Orientation.Even ? EvenAxles : OddAxles));
			if(EvenItem != null && EvenItem.SignalLinked!=null && EvenItem.SignalLinked instanceof MainSignal)
			{
				EvenItem.SignalLinked.actionPerformed(ev);
				/*MainSignal s = ((MainSignal)EvenItem.SignalLinked).NextSignal;
				if(s!=null) s.actionPerformed(ev);*/
			}
			if(OddItem != null && OddItem.SignalLinked!=null)
			{
				OddItem.SignalLinked.actionPerformed(ev);
				/*MainSignal s = ((MainSignal)OddItem.SignalLinked).NextSignal;
				if(s!=null) s.actionPerformed(ev);*/
			}
		}
		updateState();
	}
	boolean Done = false;
	void tryToFree()
	{
		if(BlockState!=Orientation.Odd && BlockState!=Orientation.Even) return;
		if(overlap != null) return;
		if(BlockingSignal==null||!BlockingSignal.ClearRequest||!BlockingSignal.listeners.contains(this))
		{
			if(BlockingTime<=OccupiedTime || BlockingSignal == null)
			{
				setBlock(Orientation.None);
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
			prev = t;
			t = t.getNext(dir);
			if(t == null) continue;
			if(prev!=t.getNext(Orientation.OppositeDir(dir))) break;
		}
		return list;
	}
	public boolean connectsTo(Orientation dir, TrackItem t)
	{
		return connectsTo(dir, t.x, t.y, dir == Orientation.Even ? t.EvenRotation : t.OddRotation);
	}
	public boolean connectsTo(Orientation dir, int objx, int objy, int objrot)
	{
		if(x==-8&&dir==Orientation.Even) return objy == y+2 && objx==45;
		if(x==45&&dir==Orientation.Odd) return objy == y-2 && objx==-8;
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
	public List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	@Override
	public void actionPerformed(SRCTEvent e) 
	{
		if(!Muted)
		{
			if(e.type == EventType.AxleCounter)
			{
				AxleEvent ae = (AxleEvent)e;
				if(!ae.second)
				{
					AxleDetected((AxleCounter)ae.creator, ae.dir);
					if(SignalLinked!=null) SignalLinked.actionPerformed(e);
				}
				else PerformAction((AxleCounter)ae.creator, ae.dir);
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
		CounterLinked = ac;
		CounterDir = dir;
		COM.toSend(new LinkPacket(getID(), ac.getID()));
	}
}
