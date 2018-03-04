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

public class MainSignal extends Signal{
	MainSignal NextSignal = null;
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
			Aspects.add(Aspect.Apagado);
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
			Aspects.add(Aspect.Apagado);
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
			Aspects.add(Aspect.Apagado);
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Anuncio_precaucion);
			Aspects.add(Aspect.Precaucion);
			Aspects.add(Aspect.Via_libre);
		}
		else if(Name.charAt(0)=='M')
		{
			Class = SignalType.Shunting;
			Aspects.add(Aspect.Apagado);
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Rebase);
		}
		else
		{
			Class = SignalType.Block;
			Automatic = true;
			Aspects.add(Aspect.Apagado);
			Aspects.add(Aspect.Parada);
			Aspects.add(Aspect.Anuncio_parada);
			Aspects.add(Aspect.Precaucion);
			Aspects.add(Aspect.Via_libre);
		}
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
		if(Automatic) setAutomatic(false);
		update();
		if(!Cleared&&UserRequest)
		{
			Timer t = new Timer(30000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UserRequest = OverrideRequest = false;
					setAspect();
				}
			});
			t.setRepeats(false);
			t.start();
		}
	}
	public boolean Locked = false;
	long Clearing = 0;
	static int x = 0;
	public void Lock()
	{
		if(Linked==null) return;
		muteEvents(true);
		List<TrackItem> items = TrackItem.DirectExploration(Linked, new TrackAvailable(OverrideRequest, false), Direction);
		muteEvents(false);
		setMonitors();
		if(items==null) return;
		TrackItem.DirectExploration(Linked, new BlockTrack(OverrideRequest), Direction);
		Locked = true;
		Clearing = Clock.time();
	}
	interface TrackIs
	{
		boolean condition(TrackItem t, Orientation dir, TrackItem p);
	}
	TrackIs nextSignal = (i, dir, p) -> i!=Linked && i!=null && i.SignalLinked != null && i.SignalLinked.Direction == dir && i.SignalLinked instanceof MainSignal;
	TrackIs ovEnd = (i, dir, p) ->
	{
		if(i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction)
		{
			if(Class == SignalType.Exit && i.Station != Linked.Station) return false;
			if(i.Occupied != Orientation.None&&(i.Occupied==dir||i.trainStopped())) return false;
			return true;
		}
		return false;
	};
	TrackIs junctionState = (i, dir, p) ->
	{
		if(i instanceof Junction)
		{
			Junction j = (Junction)i;
			if(j.Switch != Position.Straight && j.Direction==dir) Switches = true;
			if(p!=null && !j.LockedFor(p)) return false;
		}
		return true;
	};
	TrackIs BlockOccupied = (i, dir, p) -> i.BlockState!=dir;
	class TrackAvailable implements TrackComparer
	{
		boolean checkBlock = false;
		boolean Next = false;
		TrackIs end;
		boolean Override;
		TrackAvailable(boolean Override, boolean CheckBlock)
		{
			this.Override = Override;
			if(!Override) end = (i, dir, p) ->  i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||(i.SignalLinked.BlockSignal&&!i.SignalLinked.Aspects.contains(Aspect.Parada));
			else end = ovEnd;
			checkBlock = CheckBlock;
		}
		@Override
		public boolean condition(TrackItem i, Orientation dir, TrackItem p) 
		{
			if(end.condition(i, dir, p))
			{
				if(nextSignal.condition(i, dir, p)) Next = true;
				if(i.Occupied==dir&&!Next) Occupied = true;
				return true;
			}
			return false;
		}
		@Override
		public boolean criticalCondition(TrackItem i, Orientation dir, TrackItem p) 
		{
			/*if(Override)
			{
				if(i==null||(i.BlockState!=dir&&(i.BlockState!=Orientation.None||checkBlock))) return false;
				return junctionState.condition(i, dir, p);
			}*/
			if(i==null||(BlockOccupied.condition(i, dir, p)&&(i.BlockState!=Orientation.None||checkBlock))||(i.Occupied!=Orientation.None&&((i.Occupied!=dir && (!Override || !i.trainStopped()))||(!Next&&!allowsOnSight))))
			{
				/*if(i!=null&&!checkBlock&&BlockOccupied.condition(i, dir, p))
				{
					muteEvents(false);
					if(i.BlockingSignal!=null) i.BlockingSignal.tryClose();
				}*/
				return false;
			}
			return junctionState.condition(i, dir, p);
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
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					j.lock(prev);
				}
				i.setBlock(dir, MainSignal.this);
				return true;
			}
			NextSignal = (MainSignal)i.SignalLinked;
			if(NextSignal!=null && !NextSignal.listeners.contains(this)) NextSignal.listeners.add(MainSignal.this);
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
		if(ClearRequest && TrackItem.DirectExploration(Linked,  new TrackAvailable(OverrideRequest, true), Direction) != null)
		{
			Override = OverrideRequest;
			Cleared = true;
		}
	}
	@Override
	public void setAspect()
	{
		setCleared();
		if(Cleared)
		{
			if(Override)
			{
				if(Class!=SignalType.Entry&&Class!=SignalType.Exit&&Class!=SignalType.Shunting) SignalAspect = Aspect.Precaucion;
				else SignalAspect = Aspect.Rebase;
			}
			else if(!Occupied)
			{
				if(NextSignal==null||((NextSignal.SignalAspect == Aspect.Parada || NextSignal.SignalAspect == Aspect.Rebase || NextSignal.SignalAspect == Aspect.Apagado)&&(Class!=SignalType.Exit || NextSignal.Station == Station || NextSignal.BlockSignal))) SignalAspect = Aspect.Anuncio_parada;
				else
				{
					if(!Switches) SignalAspect = Aspect.Via_libre;
					else SignalAspect = Aspect.Anuncio_precaucion;
				}
			}
			else SignalAspect = Aspect.Precaucion;
		}
		else SignalAspect = Aspect.Parada;
		while(!Aspects.contains(SignalAspect))
		{
			if(SignalAspect == Aspect.Via_libre||SignalAspect == Aspect.Anuncio_precaucion) SignalAspect = Aspect.Anuncio_parada;
			else if(SignalAspect == Aspect.Anuncio_parada||SignalAspect == Aspect.Precaucion) SignalAspect = Aspect.Rebase;
			else if(SignalAspect == Aspect.Rebase)
			{
				if(BlockSignal) SignalAspect = Aspect.Apagado;
				else SignalAspect = Aspect.Parada;
			}
			else if(SignalAspect == Aspect.Parada) SignalAspect = Aspect.Apagado;
			else if(Aspects.size()!=0) SignalAspect = Aspects.get(0);
			else SignalAspect = Aspect.Apagado;
		}
		super.setAspect();
	}
	private void deactivateOverride()
	{
		if(Class == SignalType.Shunting) ClearRequest = false;
		TrackItem.DirectExploration(Linked, new TrackComparer()
		{
			boolean EndOfLock = true;
			@Override
			public boolean condition(TrackItem i, Orientation dir, TrackItem p) {
				if(i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction)
				{
					if(Class == SignalType.Exit && i.Station != Linked.Station) return false;
					if(i.Occupied!=Orientation.None) EndOfLock = false;
					if(EndOfLock&&i.BlockState==Direction) i.setBlock(Orientation.None);
					return true;
				}
				return false;
			}
			@Override
			public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) {
				return true;
			}
	
		}, Direction);
	}
	public void setState()
	{
		boolean prev = BlockSignal;
		BlockSignal = !Station.Opened || (Class==SignalType.Block||Class==SignalType.Advanced);
		if(prev==BlockSignal) return;
		update();
	}
	@Override
	public void Unlock()
	{
		if(!Locked) return;
		Locked = false;
		Clearing = 0;
		if(Override)
		{
			deactivateOverride();
			return;
		}
		muteEvents(true);
		TrackItem.DirectExploration(Linked, new TrackComparer()
				{
					boolean EndOfLock = true;
					@Override
					public boolean condition(TrackItem i, Orientation dir, TrackItem p) {
						if(i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir)
						{
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
		UserRequest = false;
		if(NextSignal != null) NextSignal.listeners.remove(this);
	}
	public void setClearRequest()
	{
		boolean TrainRequest = (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0;
		boolean BlockRequest = Linked.getNext(Orientation.OppositeDir(Direction)).BlockState == Direction;
		ClearRequest = UserRequest || ((Automatic||BlockSignal) && TrainRequest) || ((/*Automatic||*/BlockSignal) && BlockRequest);
	}
	public void update()
	{
		if(Linked==null||ForceClose) return;
		setClearRequest();
		if(TrackItem.DirectExploration(Linked,  new TrackAvailable(OverrideRequest, true), Direction) != null)
		{
			if(!ClearRequest) tryClose();
			if(!Locked)
			{
				Clearing = 0;
			}
		}
		else
		{
			if(ClearRequest) Lock();
			else Unlock();
		}
		setAspect();
	}
	int closeTimerValue = 0;
	public void tryClose()
	{
		if(ClearRequest&&!Automatic) return;
		if(TrackRequest()) setClosingTimer(10000);
		else
		{
			closeTimerValue = 0;
			TrackItem.InverseExploration(Linked, new TrackComparer()
					{
						int count = 0;
						int signalsPassed = 0;
						@Override
						public boolean condition(TrackItem t, Orientation dir, TrackItem p) 
						{
							if(t == Linked) return true;
							if(t == null) return false;
							if(!criticalCondition(t, dir, p)) return false;
							if(t.Occupied==Orientation.OppositeDir(dir)||t.Occupied == Orientation.Both)
							{
								closeTimerValue = (signalsPassed + 1) * 10000;
								return false;
							}
							if(t.SignalLinked instanceof MainSignal && t.SignalLinked.Direction == Orientation.OppositeDir(dir))
							{
								MainSignal s = (MainSignal) t.SignalLinked;
								signalsPassed++;
								if(s.SigsAhead()<=signalsPassed)
								{
									closeTimerValue = 0;
									return false;
								}
							}
							return true;
						}
						@Override
						public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) {
							if(t!=Linked && t.SignalLinked instanceof MainSignal && t.SignalLinked.Direction == Orientation.OppositeDir(dir))
							{
								MainSignal s = (MainSignal) t.SignalLinked;
								if(s.SigsAhead()==0)
								{
									return false;
								}
							}
							return true;
						}
					}, Orientation.OppositeDir(Direction));
			if(closeTimerValue!=0) setClosingTimer(closeTimerValue);
			else Unlock();
		}
	}
	Timer ClosingTimer = null;
	public void setClosingTimer(int time)
	{
		if(ClosingTimer!=null&&ClosingTimer.isRunning()) return;
		ClosingTimer = new Timer(time, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!Cleared)
				{
					ClosingTimer.setRepeats(false);
					ClosingTimer.stop();
					return;
				}
				if(!ClearRequest)
				{
					Unlock();
					ClosingTimer.setRepeats(false);
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
		ClosingTimer.start();
		ClosingTimer.setRepeats(true);
	}
	public boolean TrackRequest()
	{
		return (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0;
	}
	public List<MainSignal> getPreviousSignals()
	{
		List<MainSignal> list = new ArrayList<MainSignal>();
		for(SRCTListener s : listeners)
		{
			if(!(s instanceof Signal)) continue;
			if(!(s instanceof MainSignal)||((MainSignal)s).NextSignal!=this) continue;
			list.add((MainSignal)s);
		}
		return list;
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
	public int SigsAhead()
	{
		if(Override) return 1;
		if(!Cleared) return 0;
		if(NextSignal!=null && NextSignal.Cleared && (Class!=SignalType.Exit || NextSignal.Station == Station || NextSignal.BlockSignal)) return 2;
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
				BlockEvent be = (BlockEvent)e;
				update();
			}
			if(e.type == EventType.Occupation)
			{
				update();
			}
			if(e.type == EventType.AxleCounter)
			{
				if(e.creator == Linked.CounterLinked)
				{
					AxleEvent ae = (AxleEvent)e;
					if(Direction==ae.dir)
					{
						if(SignalAspect==Aspect.Parada&&(Automatic || Clock.time() > lastPass + 10000))
						{
							//JOptionPane.showMessageDialog(null, "Señal " + Name + " de " + Station.FullName + " rebasada");
							TrackItem.DirectExploration(Linked, new TrackComparer()
									{
										@Override
										public boolean condition(TrackItem t, Orientation dir, TrackItem p)
										{
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
						if(UserRequest)
						{
							lastPass = Clock.time();
							UserRequest = false;
						}
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
			((MainSignal)this).setAutomatic(d.Automatic);
			OverrideRequest = d.OverrideRequest;
			((MainSignal)this).UserRequest(d.UserRequest);
		}
	}
}
