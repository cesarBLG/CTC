package ctc;

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

import ctc.TrackItem.TrackComparer;

public class MainSignal extends Signal{
	MainSignal NextSignal = null;
	List<TrackItem> MonitoringItems = new ArrayList<TrackItem>();
	JPopupMenu popup;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	List<Signal> SignalsListening = new ArrayList<Signal>();
	boolean ForceClose = false;
	MainSignal()
	{
		
	}
	MainSignal(String s, Station dep)
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
		popup = new JPopupMenu();
		final Component comp = this;
		close.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						UserRequest(!ClearRequest);
					}
			
				});
		override.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						setOverride(!Override);
					}
			
				});
		auto.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						setAutomatic(!Automatic);
					}
			
				});
		JMenuItem config = new JMenuItem("Configuración");
		config.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				String s = JOptionPane.showInputDialog(comp, "Puerto de arduino");
			}
	
		});
		popup.add(close);
		popup.add(override);
		popup.add(auto);
		popup.add(config);
		addMouseListener(new MouseListener()
				{

					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// TODO Auto-generated method stub
						if(arg0.isPopupTrigger())
						{
					        popup.show(comp, arg0.getX(), arg0.getY());
						}
						if(arg0.getButton()==MouseEvent.BUTTON1)
						{
							UserRequest(true);
						}
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						if(arg0.isPopupTrigger())
						{
					        popup.show(comp, arg0.getX(), arg0.getY());
						}
					}
			
				});
		this.setForeground(Color.WHITE);
		this.setVerticalAlignment(BOTTOM);
		this.setHorizontalAlignment(Direction == Orientation.Odd ? RIGHT : LEFT);
		this.setHorizontalTextPosition(CENTER);
		this.setVerticalTextPosition(TOP);
		this.setText(Name);
		this.setFont(new Font("Tahoma", 0, 10));
		setAspect();
	}
	public void UserRequest(boolean Clear)
	{
		ClearRequest = Clear;
		setAutomatic(false);
		update();
		if(!Cleared)
		{
			Timer t = new Timer(10000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(ClearRequest&&!Automatic&&!BlockSignal&&!Cleared&&(Class!=SignalType.Shunting||!Override))
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
		if(tryClear()) return;
		if(Clearing!=0)
		{
			if(Clearing + 5 < Clock.time()) Clearing = 0;
			else return;
		}
		if(Class == SignalType.Shunting)
		{
			setOverride(true);
			return;
		}
		if(Linked==null) return;
		setMonitors();
		List<TrackItem> items = TrackItem.PositiveExploration(Linked, new TrackComparer()
				{
					boolean Next = false;
					TrackItem p = null;
					@Override
					public boolean condition(TrackItem i, Orientation dir) 
					{
						if(i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||(i.SignalLinked.BlockSignal&&!i.SignalLinked.Aspects.contains(Aspect.Parada)))
						{
							if(i!=Linked && i!=null && i.SignalLinked != null && i.SignalLinked.Direction == dir && i.SignalLinked instanceof MainSignal) Next = true;
							return true;
						}
						return false;
					}
					@Override
					public boolean criticalCondition(TrackItem i, Orientation dir) {
						if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=dir)||(i.Occupied!=Orientation.None&&(i.Occupied!=dir||(!Next&&!allowsOnSight))))
						{
							return false;
						}
						if(i instanceof Junction)
						{
							Junction j = (Junction)i;
							if(p!=null && !j.LockedFor(p)) return false;
						}
						p = i;
						return true;
					}
				}, Direction);
		if(items==null)
		{
			if(Cleared) Close();
			return;
		}
		MainSignal sig = this;
		TrackItem.PositiveExploration(Linked, new TrackComparer()
				{
					TrackItem prev = null;
					@Override
					public boolean condition(TrackItem i, Orientation dir) {
						if(i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir)
						{
							if(i instanceof Junction)
							{
								Junction j = (Junction)i;
								j.lock(prev);
							}
							i.setBlock(dir, sig);
							prev = i;
							return true;
						}
						NextSignal = (MainSignal) i.SignalLinked;
						return false;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir) {
						return true;
					}
				}, Direction);
		Clearing = Clock.time();
		tryClear();
	}
	public boolean tryClear()
	{
		Occupied = false;
		Switches = false;
		if(TrackItem.PositiveExploration(Linked, new TrackComparer()
		{
			boolean Next = false;
			TrackItem p = null;
			@Override
			public boolean condition(TrackItem i, Orientation dir) 
			{
				if(i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||(i.SignalLinked.BlockSignal&&!i.SignalLinked.Aspects.contains(Aspect.Parada)))
				{
					if(i!=Linked && i!=null && i.SignalLinked != null && i.SignalLinked.Direction == dir && i.SignalLinked instanceof MainSignal) Next = true;
					if(i.Occupied==dir&&!Next) Occupied = true;
					return true;
				}
				return false;
			}
			@Override
			public boolean criticalCondition(TrackItem i, Orientation dir) {
				if(i==null||i.BlockState!=dir||(i.Occupied!=Orientation.None&&(i.Occupied!=dir||(!Next&&!allowsOnSight))))
				{
					return false;
				}
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					if(j.Switch != Position.Straight && j.Direction==dir) Switches = true;
					if(p!=null && !j.LockedFor(p)) return false;
				}
				p = i;
				return true;
			}
		}, Direction) != null)
		{
			Clearing = 0;
			Override = false;
			Cleared = true;
			if(NextSignal!=null && !NextSignal.SignalsListening.contains(this)) NextSignal.SignalsListening.add(this);
			setAspect();
			return true;
		}
		if(Cleared) Close();
		return false;
	}
	public void setOverride(boolean o)
	{
		if(!o)
		{
			if(Override) deactivateOverride();
			return;
		}
		if(Cleared) return;
		setMonitors();
		if(TrackItem.PositiveExploration(Linked, new TrackComparer()
		{
			boolean Next = false;
			TrackItem p = null;
			@Override
			public boolean condition(TrackItem i, Orientation dir) 
			{
				if(i==Linked||i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction)
				{
					if(Class == SignalType.Exit && i.Station != Linked.Station) return false;
					return true;
				}
				return false;
			}
			@Override
			public boolean criticalCondition(TrackItem i, Orientation dir) {
				if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction)) return false;
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					if(p!=null && !j.LockedFor(p)) return false;
				}
				p = i;
				return true;
			}
		}, Direction) == null)
		{
			if(Override) deactivateOverride();
			return;
		}
		MainSignal sig = this;
		TrackItem.PositiveExploration(Linked, new TrackComparer()
		{
			@Override
			public boolean condition(TrackItem i, Orientation dir) {
				if(i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction)
				{
					if(Class == SignalType.Exit && i.Station != Linked.Station) return false;
					i.setBlock(dir, sig);
					return true;
				}
				NextSignal = (MainSignal) i.SignalLinked;
				return false;
			}
			@Override
			public boolean criticalCondition(TrackItem t, Orientation dir) {
				return true;
			}
		}, Direction);
		Override = true;
		setAspect();
	}
	private void deactivateOverride()
	{
		if(!Override) return;
		if(Class == SignalType.Shunting) ClearRequest = false;
		TrackItem.PositiveExploration(Linked, new TrackComparer()
		{
			boolean EndOfLock = true;
			@Override
			public boolean condition(TrackItem i, Orientation dir) {
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
			public boolean criticalCondition(TrackItem t, Orientation dir) {
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
		if(Class == SignalType.Shunting)
		{
			setOverride(false);
			return;
		}
		setAspect();
		if(!Cleared||Linked==null) return;
		Cleared = false;
		TrackItem.PositiveExploration(Linked, new TrackComparer()
				{
					boolean EndOfLock = true;
					@Override
					public boolean condition(TrackItem i, Orientation dir) {
						if(i==Linked||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir)
						{
							if(i.Occupied!=Orientation.None) EndOfLock = false;
							if(EndOfLock&&i.BlockState==dir) i.setBlock(Orientation.None);
							return true;
						}
						return false;
					}
					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir) {
						return true;
					}
			
				}, Direction);
		if(!Automatic&&!BlockSignal) ClearRequest = false;
		if(NextSignal != null) NextSignal.SignalsListening.remove(this);
		setAspect();
	}
	public void TrackChanged(TrackItem t, Orientation dir, boolean Release)
	{
		setState();
		if(t==Linked&&Direction==dir)
		{
			if(!Automatic && !Release && Cleared)
			{
				ClearRequest = false;
				Close();
			}
			if(!Release&&Override) setOverride(false);
		}
		update();
	}
	public void update()
	{
		if(Linked==null||ForceClose) return;
		boolean TrainRequest = (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0;
		boolean BlockRequest = Linked.getNext(OppositeDir(Direction)).BlockState == Direction;
		if(((Automatic||BlockSignal) && TrainRequest) || ((Automatic||BlockSignal) && BlockRequest)) ClearRequest = true;
		else if(Automatic||BlockSignal) ClearRequest = false;
		boolean prev = Cleared;
		if(ClearRequest) Clear();
		else if(Cleared)
		{
			tryClose();
		}
		if(Cleared&&!prev)
		{
			Timer t = new Timer(5000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tryClose();
				}
			});
			t.setRepeats(false);
			t.start();
		}
	}
	public void tryClose()
	{
		if(ClearRequest&&(!Automatic||BlockSignal)) return;
		if(TrackRequest()) setClosingTimer(10000);
		else
		{
			List<MainSignal> sigs = getPreviousSignals();
			for(MainSignal s : sigs)
			{
				TrackItem t = s.Linked;
				boolean ocup = false;
				do
				{
					if(t.Occupied==Direction||t.Occupied==Orientation.Both)
					{
						ocup = true;
						break;
					}
					t = t.getNext(Direction);
				}
				while(t!=Linked);
				if(ocup)
				{
					setClosingTimer(20000);
					return;
				}
				if(s.SigsAhead()==2&&s.TrackRequest())
				{
					setClosingTimer(30000);
					return;
				}
			}
			if(Automatic&&!BlockSignal&&ClearRequest)
			{
				ClearRequest = false;
				ForceClose = true;
				Close();
				ClearRequest = true;
				ForceClose = false;
				Clear();
				Timer t = new Timer(5000, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						tryClose();
					}
				});
				t.setRepeats(false);
				t.start();
				return;
			}
			if(!ClearRequest) Close();
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
					ClosingTimer.stop();
					ClosingTimer.setRepeats(false);
					return;
				}
				if(!ClearRequest)
				{
					Close();
					ClosingTimer.stop();
					ClosingTimer.setRepeats(false);
					return;
				}
				if(!(Automatic||BlockSignal)) return;
				ClearRequest = false;
				ForceClose = true;
				Close();
				ClearRequest = true;
				ForceClose = false;
				Clear();
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
		for(Signal s : SignalsListening)
		{
			if(!(s instanceof MainSignal)||((MainSignal)s).NextSignal!=this) continue;
			list.add((MainSignal)s);
		}
		return list;
	}
	public void setAutomatic(boolean v)
	{
		if(v)
		{
			if(MonitoringItems.isEmpty()) setMonitors();
			Automatic = true;
			setState();
			update();
		}
		else Automatic = BlockSignal = false;
		setState();
		setAspect();
		List<Signal> prevs = new ArrayList<Signal>();
		prevs.addAll(SignalsListening);
		for(Signal s : prevs)
		{
			s.update();
		}
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
			if(!Occupied)
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
		else if(Override)
		{
			if(Class!=SignalType.Entry&&Class!=SignalType.Exit&&Class!=SignalType.Shunting) SignalAspect = Aspect.Precaucion;
			else SignalAspect = Aspect.Rebase;
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
		setIcon(new ImageIcon(getClass().getResource("/Images/Signals/".concat((SignalAspect==Aspect.Parada&&Automatic ? "Automatic" : SignalAspect.name()).concat("_".concat(Direction.name().concat(".png")))))));
		close.setText(Cleared ? "Cerrar señal" : "Abrir señal");
		override.setText(Override ? "Desactivar rebase" : "Rebase autorizado");
		auto.setText(!Automatic ? "Modo automático" : "Modo manual");
		if(LastAspect==SignalAspect) return;
		for(Signal s : SignalsListening) s.setAspect();
		super.setAspect();
	}
	public void setMonitors()
	{
		TrackItem i = Linked;
		TrackItem p = null;
		for(TrackItem t : MonitoringItems)
		{
			t.SignalsListening.remove(this);
		}
		MonitoringItems = TrackItem.PositiveExploration(Linked, new TrackComparer()
				{
					@Override
					public boolean condition(TrackItem i, Orientation dir) 
					{
						return i==Linked||(i!=null&&(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=dir||i.SignalLinked.BlockSignal));
					}

					@Override
					public boolean criticalCondition(TrackItem t, Orientation dir) {
						// TODO Auto-generated method stub
						return true;
					}
					
				}, Direction);
		for(TrackItem t : MonitoringItems)
		{
			if(t!=null&&!t.SignalsListening.contains(this)) t.SignalsListening.add(this);
		}
	}
}
