package scrt.ctc;

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
		allowsOnSight = Class != SignalType.Entry;
		set(Number%2 == 0 ? Orientation.Even : Orientation.Odd);
	}
	void set(Orientation dir)
	{
		Direction = dir;
		icon = new SignalIcon(this);
		setAspect();
	}
	public void UserRequest(boolean Clear)
	{
		ClearRequest = Clear;
		setAutomatic(false);
		update();
		if(!Cleared)
		{
			Timer t = new Timer(30000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(ClearRequest&&!Automatic&&!BlockSignal&&!Cleared)
					{
						ClearRequest = false;
					}
				}
			});
			t.setRepeats(false);
			t.start();
		}
	}
	double Clearing = 0;
	public void Clear()
	{
		if(Cleared)
		{
			tryClear();
			return;
		}
		tryClear();
		if(Cleared) return;
		if(!tryBlockTrack()) return;
		tryClear();
	}
	boolean tryBlockTrack()
	{
		if(Clearing!=0)
		{
			if(Clearing + 5 < Clock.time()) Clearing = 0;
			else return false;
		}
		if(Linked==null) return false;
		muteEvents(true);
		List<TrackItem> items = TrackItem.PositiveExploration(Linked, new TrackAvailable(OverrideRequest, false), Direction);
		muteEvents(false);
		if(items==null) return false;
		TrackItem.PositiveExploration(Linked, new BlockTrack(false), Direction);
		setMonitors();
		Clearing = Clock.time();
		return true;
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
			if(i.Occupied != Orientation.None) return false;
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
			if(Override)
			{
				if(i==null||(i.BlockState!=dir&&(i.BlockState!=Orientation.None||checkBlock))) return false;
				return junctionState.condition(i, dir, p);
			}
			if(i==null||(BlockOccupied.condition(i, dir, p)&&(i.BlockState!=Orientation.None||checkBlock))||(i.Occupied!=Orientation.None&&(i.Occupied!=dir||(!Next&&!allowsOnSight))))
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
			return false;
		}
		@Override
		public boolean criticalCondition(TrackItem t, Orientation dir, TrackItem p) 
		{
			return true;
		}
	}
	public void tryClear()
	{
		Occupied = false;
		Switches = false;
		if(TrackItem.PositiveExploration(Linked,  new TrackAvailable(OverrideRequest, true), Direction) != null)
		{
			Clearing = 0;
			Override = OverrideRequest;
			Cleared = true;
			if(NextSignal!=null && !NextSignal.listeners.contains(this)) NextSignal.listeners.add(this);
			setAspect();
			return;
		}
		if(Cleared)
		{
			if(tryBlockTrack()) return;
			else Close();
		}
		Cleared = Override = false;
	}
	private void deactivateOverride()
	{
		if(!Override) return;
		if(Class == SignalType.Shunting) ClearRequest = false;
		TrackItem.PositiveExploration(Linked, new TrackComparer()
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
		Override = false;
		if(!Automatic&&!BlockSignal) ClearRequest = false;
		setAspect();
	}
	public void setState()
	{
		boolean prev = BlockSignal;
		BlockSignal = !Station.Opened || (Class==SignalType.Block||Class==SignalType.Advanced);
		if(prev==BlockSignal) return;
		update();
	}
	public void Close()
	{
		if(Override)
		{
			deactivateOverride();
			return;
		}
		if(!Cleared) return;
		Cleared = false;
		muteEvents(true);
		TrackItem.PositiveExploration(Linked, new TrackComparer()
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
		if(!Automatic&&!BlockSignal) ClearRequest = false;
		if(NextSignal != null) NextSignal.listeners.remove(this);
		setAspect();
	}
	public void update()
	{
		if(Linked==null||ForceClose) return;
		boolean TrainRequest = (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0;
		boolean BlockRequest = Linked.getNext(Orientation.OppositeDir(Direction)).BlockState == Direction;
		if(((Automatic||BlockSignal) && TrainRequest) || ((Automatic||BlockSignal) && BlockRequest)) ClearRequest = true;
		else if(Automatic||BlockSignal) ClearRequest = false;
		boolean prev = Cleared;
		if(ClearRequest) Clear();
		else if(Cleared)
		{
			tryClose();
		}
	}
	int closeTimerValue = 0;
	public void tryClose()
	{
		if(ClearRequest&&!Automatic) return;
		if(TrackRequest()) setClosingTimer(10000);
		else
		{
			closeTimerValue = 0;
			TrackItem.NegativeExploration(Linked, new TrackComparer()
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
			else Close();
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
					Close();
					ClosingTimer.setRepeats(false);
					ClosingTimer.stop();
					return;
				}
				if(!(Automatic||BlockSignal)) return;
				ClearRequest = false;
				ForceClose = true;
				muteEvents(true);
				Close();
				ClearRequest = true;
				ForceClose = false;
				muteEvents(true);
				Clear();
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
		setAspect();
	}
	public int SigsAhead()
	{
		if(Override) return 1;
		if(!Cleared) return 0;
		if(NextSignal!=null && NextSignal.Cleared && (Class!=SignalType.Exit || NextSignal.Station == Station || NextSignal.BlockSignal)) return 2;
		else return 1;
	}
	public void setAspect()
	{
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
			else SignalAspect = Aspects.get(0);
		}
		icon.update();		
		if(LastAspect==SignalAspect) return;
		for(SRCTListener l : listeners) l.actionPerformed(new SignalEvent(this));
		super.setAspect();
	}
	public void setMonitors()
	{
		for(TrackItem t : MonitoringItems)
		{
			t.listeners.remove(this);
		}
		MonitoringItems = TrackItem.PositiveExploration(Linked, new TrackComparer()
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
	List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	List<SRCTEvent> Queue = new ArrayList<SRCTEvent>();
	boolean EventsMuted = false;
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
						if(!Automatic && Cleared)
						{
							ClearRequest = false;
							Close();
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
}
