package scrt.ctc.Signal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.com.packet.StatePacket;
import scrt.com.packet.Packet;
import scrt.com.packet.SignalData;
import scrt.ctc.Clock;
import scrt.ctc.Config;
import scrt.ctc.Junction;
import scrt.ctc.Position;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
import scrt.ctc.TrackItem.TrackComparer;
import scrt.event.AxleEvent;
import scrt.event.BlockEvent;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;
import scrt.event.EventType;
import scrt.event.OccupationEvent;
import scrt.event.SignalEvent;
import scrt.gui.SignalIcon;
import scrt.log.Logger;

public class MainSignal extends Signal{
	public MainSignal NextSignal = null;
	List<TrackItem> MonitoringItems = new ArrayList<TrackItem>();
	boolean ForceClose = false;
	MainSignal()
	{
		
	}
	public MainSignal(String s, Station dep)
	{
		Name = s;
		Station = dep;
		if(Name.charAt(0)=='S')
		{
			Class = SignalType.Exit;
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Anuncio_precaucion);
			Aspects.add(Aspect.Precaucion);
			Aspects.add(Aspect.Rebase);
			Aspects.add(Aspect.Via_libre);
		}
		else if(Name.charAt(0)=='E' && Name.charAt(1)!='\'')
		{
			Class = SignalType.Entry;
			Aspects.add(Aspect.Rebase);
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Anuncio_precaucion);
			Aspects.add(Aspect.Via_libre);
		}
		else if(Name.charAt(0)=='E' && Name.charAt(1)=='\'')
		{
			Class = SignalType.Advanced;
			Automatic = true;
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Anuncio_precaucion);
			Aspects.add(Aspect.Precaucion);
			Aspects.add(Aspect.Preanuncio);
			Aspects.add(Aspect.Via_libre);
		}
		else if(Name.charAt(0)=='M')
		{
			Class = SignalType.Shunting;
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Rebase);
		}
		else
		{
			Class = SignalType.Block;
			Automatic = true;
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Precaucion);
			Aspects.add(Aspect.Via_libre);
		}
		if(Config.sigsAhead<2) Aspects.remove(Aspect.Anuncio_parada);
		if(Config.anuncioPrecaución == 0) Aspects.remove(Aspect.Anuncio_precaucion);
		int num = 0;
		int start1 = -1;
		int end1 = 0;
		int start2 = 0;
		for(int i=0; i<Name.length();i++)
		{
			if(Name.charAt(i)<='9'&&Name.charAt(i)>='0'&&start1==-1) start1 = i;
			if(Name.charAt(i)=='/') end1 = i;
			if(Name.charAt(i)<='9'&&Name.charAt(i)>='0'&&end1!=0&&start2==0) start2 = i;
		}
		if(end1==0) end1 = Name.length();
		Number = Integer.parseInt(Name.substring(start1, end1));
		if(start2!=0) Track = Integer.parseInt(Name.substring(start2));
		else Track = 0;
		allowsOnSight = Config.allowOnSight && Class != SignalType.Entry;
		set(Number%2 == 0 ? Orientation.Even : Orientation.Odd);
	}
	void set(Orientation dir)
	{
		Direction = dir;
		super.setAspect();
		setState();
	}
	boolean UserRequest = false;
	public void UserRequest(boolean Clear)
	{
		if(UserRequest == Clear) return;
		UserRequest = Clear;
		//if(Automatic) setAutomatic(false);
		update();
		if(!Cleared&&UserRequest)
		{
			Timer t = new Timer(30000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setCleared();
					if(!Cleared) UserRequest = OverrideRequest = false;
					setAspect();
				}
			});
			t.setRepeats(false);
			t.start();
		}
	}
	interface TrackIs
	{
		boolean condition(TrackItem t, Orientation dir, TrackItem p);
	}
	TrackIs nextSignal = (i, dir, p) -> i!=Linked && i!=null && i.SignalLinked != null && i.SignalLinked.Direction == dir && i.SignalLinked.protects();
	TrackIs ovEnd = (i, dir, p) ->
	{
		if(i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction)
		{
			if(Class == SignalType.Exit && i.Station != Linked.Station) return false;
			//if(i.Occupied != Orientation.None && i.Occupied != Orientation.Unknown &&(i.Occupied==dir||i.trainStopped())) return false;
			return true;
		}
		return false;
	};
	class TrackAvailable implements TrackComparer
	{
		boolean checkBlock = false;
		boolean Next = false;
		TrackIs end;
		boolean Override;
		TrackAvailable(boolean Override, boolean CheckBlock)
		{
			this.Override = Override;
			if(!Override) end = (i, dir, p) ->  i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||(i.SignalLinked.BlockSignal);
			else end = ovEnd;
			checkBlock = CheckBlock;
		}
		@Override
		public boolean condition(TrackItem i, Orientation dir, TrackItem p) 
		{
			if(nextSignal.condition(i, dir, p) && !Next)
			{
				Next = true;
				NextSignal = (MainSignal)i.SignalLinked;
				if(!NextSignal.listeners.contains(this)) NextSignal.listeners.add(MainSignal.this);
				checkBlock = false;
			}
			if(end.condition(i, dir, p))
			{
				if(i.Occupied!=Orientation.None&&!Next) Occupied = true;
				if(i.Occupied==Orientation.OppositeDir(dir)&&!Next&&criticalCondition(i, dir, p)) return false;
				if(i instanceof Junction && !Next)
				{
					Junction j = (Junction)i;
					if(j.Switch != Position.Straight && j.Direction==dir) Switches = true;
				}
				return true;
			}
			return false;
		}
		@Override
		public boolean criticalCondition(TrackItem i, Orientation dir, TrackItem p) 
		{
			if(i==null
					|| (i.BlockState!=dir&&(i.BlockState!=Orientation.None||checkBlock)) 
					|| (i.Occupied != Orientation.None && ((!OverrideRequest && !Next && !allowsOnSight) || ((i.Occupied == Orientation.OppositeDir(dir) || i.Occupied == Orientation.Both) && (!i.trainStopped())))))
			{
				return false;
			}
			if(i instanceof Junction)
			{
				Junction j = (Junction)i;
				if(p!=null && !j.blockedFor(p, checkBlock)) return false;
				if(Config.lock && p!=null && !j.LockedFor(p, checkBlock)) return false;
				if(j.CrossingLinked != null)
				{
					Junction k = j.CrossingLinked;
					if(k.Locked == 1 || (k.Switch != Position.Straight && k.BlockState != Orientation.None)) return false;
				}
			}
			return true;
		}
	}
	class LockTrack implements TrackComparer
	{
		TrackIs end;
		LockTrack(boolean Override)
		{
			if(!Override) end = (i, dir, p) -> !nextSignal.condition(i, dir, p);
			else end = ovEnd;
		}
		@Override
		public boolean condition(TrackItem i, Orientation dir, TrackItem prev) 
		{
			if(end.condition(i, dir, prev))
			{
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					j.lock(prev);
				}
				return true;
			}
			return false;
		}
		@Override
		public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) 
		{
			return true;
		}
	}
	class BlockTrack implements TrackComparer
	{
		TrackIs end;
		BlockTrack(boolean Override)
		{
			if(!Override) end = (i, dir, p) -> !nextSignal.condition(i, dir, p);
			else end = ovEnd;
		}
		@Override
		public boolean condition(TrackItem i, Orientation dir, TrackItem prev) 
		{
			if(end.condition(i, dir, prev))
			{
				if(i.Occupied != Orientation.None && i.Occupied != Orientation.Unknown && i.Occupied!=dir && !i.trainStopped()) return false;
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					j.block(prev);
				}
				i.setBlock(dir, MainSignal.this);
				return true;
			}
			return false;
		}
		@Override
		public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) 
		{
			return true;
		}
	}
	void setCleared()
	{
		Cleared = Override = Switches = Occupied = false;
		if(TrackItem.DirectExploration(Linked,  new TrackAvailable(OverrideRequest, (Class != SignalType.Advanced && Class != SignalType.Block) || !Config.openSignals), Direction) != null)
		{
			Override = OverrideRequest;
			Cleared = true;
		}
	}
	@Override
	public void setAspect()
	{
		setCleared();
		if(!Cleared)
		{
			SignalAspect = Aspect.Parada;
		}
		else if(Override)
		{
			SignalAspect = Aspect.Rebase;
		}
		else if(Occupied)
		{
			SignalAspect = Aspect.Precaucion;
		}
		else
		{
			SignalAspect = Aspect.Via_libre;
			if(Config.sigsAhead > 1 && NextSignal.SignalAspect == Aspect.Anuncio_parada && NextSignal.Class == SignalType.Entry) SignalAspect = Aspect.Preanuncio;
			if((Switches && Config.anuncioPrecaución == 1) || (NextSignal != null && NextSignal.Switches && Config.anuncioPrecaución == 2))
			{
				SignalAspect = Aspect.Anuncio_precaucion;
			}
			if(Config.sigsAhead > 1)
			{
				if(Switches && Config.anuncioPrecaución == 2)
				{
					SignalAspect = Aspect.Anuncio_parada;
				}
				if(NextSignal==null || NextSignal.SignalAspect == Aspect.Parada || NextSignal.SignalAspect == Aspect.Rebase || NextSignal.SignalAspect == Aspect.Apagado)
				{
					SignalAspect = Aspect.Anuncio_parada;
				}
			}
		}
		while(!Aspects.contains(SignalAspect) && SignalAspect != Aspect.Apagado)
		{
			if(SignalAspect == Aspect.Via_libre)
			{
				while(!Aspects.contains(SignalAspect) && SignalAspect != Aspect.Apagado)
				{
					switch(SignalAspect)
					{
						case Parada:
							SignalAspect = Aspect.Apagado;
							break;
						case Rebase:
							SignalAspect = Aspect.Parada;
						case Precaucion:
							SignalAspect = Aspect.Rebase;
							break;
						case Anuncio_parada:
							SignalAspect = Aspect.Precaucion;
							break;
						default:
							SignalAspect = Aspect.Anuncio_parada;
					}
				}
				break;
			}
			else
			{
				switch(SignalAspect)
				{
					case Parada:
						SignalAspect = Aspect.Apagado;
						break;
					case Rebase:
						SignalAspect = Aspect.Precaucion;
					case Anuncio_parada:
						SignalAspect = Config.sigsAhead < 2 ? ((Config.anuncioPrecaución == 1 && Switches) ? Aspect.Anuncio_precaucion : Aspect.Via_libre) : (Aspects.contains(Aspect.Parada) ? Aspect.Parada : Aspect.Apagado);
						break;
					case Preanuncio:
						SignalAspect = Aspect.Anuncio_precaucion;
						break;
					default:
						SignalAspect = Aspect.Via_libre;
						break;
				}
			}
		}
		super.setAspect();
	}
	public void setState()
	{
		boolean prev = BlockSignal;
		BlockSignal = !Station.Opened || (Class==SignalType.Block||Class==SignalType.Advanced);
		if(prev==BlockSignal) return;
		update();
	}
	public boolean Locked = false;
	@Override
	public void Lock()
	{
		if(Locked || Linked==null) return;
		muteEvents(true);
		List<TrackItem> items = TrackItem.DirectExploration(Linked, new TrackAvailable(OverrideRequest, false), Direction);
		muteEvents(false);
		setMonitors();
		Locked = true;
		if(items==null)
		{
			Unlock();
			return;
		}
		if(!Config.trailablePoints)
		{
			if(TrackItem.DirectExploration(Linked, new TrackComparer()
					{
						@Override
						public boolean condition(TrackItem t, Orientation dir, TrackItem p)
						{
							TrackIs end;
							if(!Override) end = (x, y, z) -> !nextSignal.condition(x, y, z);
							else end = ovEnd;
							if(!end.condition(t, dir, p)) return false;
							return true;
						}
						@Override
						public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
						{
							if(t instanceof Junction)
							{
								Junction j = (Junction)t;
								if(j.canGetFrom(p)) return true;
								else
								{
									j.userChangeSwitch();
									return false;
								}
							}
							return true;
						}
					}, Direction)==null) return;
		}
		muteEvents(true);
		if(Config.lock)
		{
			List<TrackItem> i = TrackItem.DirectExploration(Linked, new LockTrack(OverrideRequest), Direction);
			if(Config.lockBeforeBlock)
			{
				for(TrackItem t : i)
				{
					if(t instanceof Junction && ((Junction)t).Locked == -1)
					{
						muteEvents(false);
						return;
					}
				}
			}
		}
		TrackItem.DirectExploration(Linked, new BlockTrack(OverrideRequest), Direction);
		muteEvents(false);
	}
	@Override
	public void Unlock()
	{
		if(!Locked) return;
		Locked = false;
		muteEvents(true);
		TrackItem.DirectExploration(Linked, new TrackComparer()
				{
					boolean EndOfLock = true;
					@Override
					public boolean condition(TrackItem i, Orientation dir, TrackItem p) {
						if((i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir) && i.BlockingSignal == MainSignal.this)
						{
							if(OverrideRequest && Class == SignalType.Exit && i.Station != Linked.Station) return false;
							if(i.Occupied!=Orientation.None) EndOfLock = false;
							if(EndOfLock&&i.BlockState==dir) i.setBlock(Orientation.None);
							return true;
						}
						return false;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) {
						return true;
					}
			
				}, Direction);
		muteEvents(false);
		setMonitors();
		if(NextSignal != null) NextSignal.listeners.remove(this);
	}
	public void setClearRequest()
	{
		boolean BlockRequest = Linked.getNext(Orientation.OppositeDir(Direction)).BlockState == Direction;
		ClearRequest = UserRequest || ((Automatic||BlockSignal) && TrackRequest()) || ((/*Automatic||*/BlockSignal) && BlockRequest);
		if(Class == SignalType.Shunting) OverrideRequest = true;
		else if(!UserRequest) OverrideRequest = false;
	}
	public void update()
	{
		if(Linked==null||ForceClose) return;
		setClearRequest();
		if(TrackItem.DirectExploration(Linked,  new TrackAvailable(OverrideRequest, true), Direction) != null)
		{
			if(ClearRequest) Lock();
			else tryClose();
		}
		else
		{
			if(ClearRequest)
			{
				Locked = false;
				Lock();
			}
			else Unlock();
		}
		if(ClosingTimer!=null && ClosingTimer.isRunning())
		{
			if(ClearRequest) ClosingTimer.stop();
			else if(Cleared) tryClose();
		}
		setAspect();
		if(Config.sigsAhead == 2 && NextSignal!=null && NextSignal.SignalAspect == Aspect.Parada && NextSignal.Automatic) NextSignal.update();
	}
	public boolean trainInProximity()
	{
		return (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0;
	}
	public boolean TrackRequest()
	{
		if(trainInProximity()) return true;
		for(MainSignal s : getPreviousSignals())
		{
			if(s.trainInProximity())
			{
				if(Config.sigsAhead == 2) return true;
			}
		}
		return false;
	}
	boolean proximity = false;
	boolean affects = false;
	public void tryClose()
	{
		if(ClearRequest&&!Automatic) return;
		proximity = affects = false;
		if(trainInProximity()) proximity = true;
		TrackItem.InverseExploration(Linked, new TrackComparer()
				{
					int signalsPassed = 0;
					boolean cond;
					@Override
					public boolean condition(TrackItem t, Orientation dir, TrackItem p) 
					{
						if(t == Linked) return true;
						if(t == null) return false;
						if(t.Occupied==Direction||t.Occupied == Orientation.Both) cond = true;
						if(t.SignalLinked instanceof MainSignal && t.SignalLinked.Direction == Direction)
						{
							MainSignal s = (MainSignal) t.SignalLinked;
							if(s.SigsAhead()<=signalsPassed+1) return false;
							signalsPassed++;
							if(s.SigsAhead()>signalsPassed)
							{
								if(cond || s.trainInProximity()) proximity = true;
								cond = false;
								affects = true;
							}
						}
						return true;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) {
						return true;
					}
				}, Orientation.OppositeDir(Direction));
		if(proximity)
		{
			if(affects || Class == SignalType.Entry)
			{
				setClosingTimer(Config.D1);
			}
			else setClosingTimer(Config.D0);
		}
		else
		{
			Unlock();
			setAspect();
		}
	}
	Timer ClosingTimer = null;
	public void setClosingTimer(int time)
	{
		if(ClosingTimer==null)
		{
			ClosingTimer = new Timer(time, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!Cleared)
					{
						ClosingTimer.stop();
						return;
					}
					if(!ClearRequest)
					{
						Unlock();
						setAspect();
						ClosingTimer.stop();
						return;
					}
					if(!(Automatic||BlockSignal)) return;
					ClearRequest = false;
					ForceClose = true;
					muteEvents(true);
					Unlock();
					ClearRequest = true;
					ForceClose = false;
					Lock();
					muteEvents(false);
				}
			});
			ClosingTimer.setRepeats(false);
		}
		if(ClosingTimer.isRunning() && ClosingTimer.getInitialDelay() == time) return;
		ClosingTimer.setInitialDelay(time);
		ClosingTimer.restart();
	}
	List<MainSignal> PreviousSignals;
	public List<MainSignal> getPreviousSignals()
	{
		PreviousSignals = new ArrayList<MainSignal>();
		TrackItem.InverseExploration(Linked, new TrackComparer()
				{
					@Override
					public boolean condition(TrackItem t, Orientation dir, TrackItem p)
					{
						if(t == Linked) return true;
						if(t == null) return false;
						if(t.SignalLinked instanceof MainSignal && t.SignalLinked.Direction == Direction)
						{
							PreviousSignals.add((MainSignal) t.SignalLinked);
							return false;
						}
						return true;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
					{
						return true;
					}
				}, Orientation.OppositeDir(Direction));
		return PreviousSignals;
	}
	public void setAutomatic(boolean v)
	{
		if(Automatic==v) return;
		if(v)
		{
			Automatic = true;
			setMonitors();
			setState();
			update();
		}
		else Automatic = false;
		setState();
		update();
	}
	@Override
	public boolean protects()
	{
		return (!BlockSignal || Aspects.contains(Aspect.Parada)) && SignalAspect != Aspect.Apagado;
	}
	public int SigsAhead()
	{
		if(Override) return 1;
		if(!Cleared) return 0;
		if(Config.sigsAhead > 1 && NextSignal!=null && SignalAspect!=Aspect.Anuncio_parada)
		{
			if(NextSignal.SignalAspect == Aspect.Via_libre && NextSignal.Class == SignalType.Entry && SignalAspect == Aspect.Via_libre) return 3;
			return 2;
		}
		else return 1;
	}
	public void setMonitors()
	{
		for(TrackItem t : MonitoringItems)
		{
			t.listeners.remove(this);
		}
		MonitoringItems = TrackItem.DirectExploration(Linked, new TrackComparer()
				{
					@Override
					public boolean condition(TrackItem i, Orientation dir, TrackItem p) 
					{
						return i==Linked||(i!=null&&(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||i.SignalLinked.BlockSignal));
					}

					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) {
						// TODO Auto-generated method stub
						return true;
					}
					
				}, Direction);
		for(TrackItem t : MonitoringItems)
		{
			if(t!=null&&!t.listeners.contains(this)) t.listeners.add(this);
		}
	}
	long lastPass = 0;
	@Override
	public void actionPerformed(SRCTEvent e) {
		if(!EventsMuted)
		{
			if(e.type == EventType.Signal)
			{
				setAspect();
			}
			if(e.type == EventType.Block)
			{
				update();
			}
			if(e.type == EventType.Occupation)
			{
				update();
			}
			if(e.type == EventType.AxleCounter)
			{
				if(e.creator == Linked.CounterLinked && Linked.CounterLinked.Working)
				{
					AxleEvent ae = (AxleEvent)e;
					if(Direction==ae.dir)
					{
						if(SignalAspect==Aspect.Parada&&Clock.time() > lastPass + 10000)
						{
							Logger.trace("Señal " + Name + " de " + Station.FullName + " rebasada");
							TrackItem.DirectExploration(Linked, new TrackComparer()
									{
										@Override
										public boolean condition(TrackItem t, Orientation dir, TrackItem p)
										{
											//Needs revision
											if(!nextSignal.condition(t, dir, p))
											{
												t.setBlock(Orientation.Unknown, MainSignal.this);
												if(t instanceof Junction)
												{
													((Junction)t).lock(p);
												}
											}
											else return false;
											return true;
										}
										@Override
										public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p)
										{
											return true;
										}
									}, ae.dir);
						}
						if(SignalAspect != Aspect.Parada) lastPass = Clock.time();
						if(UserRequest) OverrideRequest = UserRequest = false;
					}
				}
			}
			Queue.remove(e);
		}
	}
	@Override
	public void muteEvents(boolean mute) {
		// TODO Auto-generated method stub
		EventsMuted = mute;
		if(!EventsMuted)
		{
			List<SRCTEvent> l = new ArrayList<SRCTEvent>();
			l.addAll(Queue);
			for(SRCTEvent e : l)
			{
				if(EventsMuted) return;
				else actionPerformed(e);
			}
		}
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof SignalData)
		{
			SignalData d = (SignalData)p;
			if(!d.id.equals(getID())) return;
			setAutomatic(d.Automatic);
			if(!OverrideRequest && d.OverrideRequest && UserRequest) UserRequest(false);
			OverrideRequest = d.OverrideRequest;
			if(d.UserRequest) UserRequest = false;
			UserRequest(d.UserRequest);
		}
	}
}
