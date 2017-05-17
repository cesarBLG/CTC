package main;

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

public class MainSignal extends Signal{
	MainSignal NextSignal = null;
	List<TrackItem> MonitoringItems = new ArrayList<TrackItem>();
	JPopupMenu p;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	List<Signal> SignalsListening = new ArrayList<Signal>();
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
			set(Integer.parseInt(Name.substring(1, 2))%2==1 ? Orientation.Odd : Orientation.Even);
		}
		else if(Name.charAt(0)=='E' && Name.charAt(1)!='\'')
		{
			Class = SignalType.Entry;
			set(Integer.parseInt(Name.substring(1, 2))%2==1 ? Orientation.Odd : Orientation.Even);
		}
		else if(Name.charAt(0)=='E' && Name.charAt(1)=='\'')
		{
			Class = SignalType.Advanced;
			Automatic = true;
			set(Integer.parseInt(Name.substring(2, 3))%2==1 ? Orientation.Odd : Orientation.Even);
		}
		else if(Name.charAt(0)=='M')
		{
			Class = SignalType.Shunting;
			set(Integer.parseInt(Name.substring(1, 2))%2==1 ? Orientation.Odd : Orientation.Even);
		}
		else
		{
			Class = SignalType.Block;
			Automatic = true;
			set(Integer.parseInt(Name.substring(0, 2))%2==1 ? Orientation.Odd : Orientation.Even);
		}
		int num = 0;
		for(int i = Name.length() - 1; i>=0 && Name.charAt(i)!='/'; i--)
		{
			if((Name.charAt(i)>'9' || Name.charAt(i) < '0'))
			{
				num /= 2;
				break;
			}
			else
			{
				num *= 10;
				num = Integer.parseInt(Character.toString(s.charAt(i)));
			}
			if(i == 0) num /= 2;
		}
		Number = num;
	}
	void set(Orientation dir)
	{
		Direction = dir;
		p = new JPopupMenu();
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
		p.add(close);
		p.add(override);
		p.add(auto);
		p.add(config);
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
					        p.show(comp, arg0.getX(), arg0.getY());
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
					        p.show(comp, arg0.getX(), arg0.getY());
						}
					}
			
				});
		this.setForeground(Color.WHITE);
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
			Timer t = new Timer(5000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(ClearRequest&&!Automatic&&!BlockSignal&&!Cleared)
					{
						ClearRequest = false;
						update();
					}
				}
			});
			t.setRepeats(false);
			t.start();
		}
	}
	public void Clear()
	{
		if(Class == SignalType.Shunting)
		{
			setOverride(true);
			return;
		}
		Occupied = false;
		Switches = false;
		if(Linked==null) return;
		setMonitors();
		TrackItem i = Linked;
		TrackItem p = null;
		boolean Next = false;
		do
		{
			if(i!=Linked && i!=null && i.SignalLinked != null && i.SignalLinked.Direction == Direction && i.SignalLinked instanceof MainSignal) Next = true;
			if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction)||(i.Occupied!=Orientation.None&&(i.Occupied!=Direction||(!Next&&Class==SignalType.Entry))))
			{
				for(TrackItem t : MonitoringItems)
				{
					for(MainSignal s : t.SignalsListening)
					{
						if(s!=this&&!s.SignalsListening.contains(this)) s.SignalsListening.add(this);
					}
				}
				if(Cleared) Close();
				return;
			}
			if(i instanceof Junction)
			{
				Junction j = (Junction)i;
				if(p!=null && !j.LockedFor(p))
				{
					for(TrackItem t : MonitoringItems)
					{
						for(MainSignal s : t.SignalsListening)
						{
							if(s!=this&&!s.SignalsListening.contains(this)) s.SignalsListening.add(this);
						}
					}
					if(Cleared) Close();
					return;
				}
			}
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction||i.SignalLinked.BlockSignal);
		i = Linked;
		p = null;
		do
		{
			if(i.Occupied==Direction) Occupied = true;
			if(i instanceof Junction)
			{
				Junction j = (Junction)i;
				if(j.Switch != Position.Straight && j.Direction==Direction) Switches = true;
			}
			i.setBlock(Direction);
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction);
		Override = false;
		if(!Cleared&&Automatic) setClosingTimer();
		Cleared = true;
		NextSignal = (MainSignal) i.SignalLinked;
		if(!NextSignal.SignalsListening.contains(this)) NextSignal.SignalsListening.add(this);
		setAspect();
	}
	public void setClosingTimer()
	{
		Timer t = new Timer(10000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!ClearRequest||!Automatic||BlockSignal) return;
				ClearRequest = false;
				Close();
				ClearRequest = true;
				Clear();
			}
		});
		t.setRepeats(false);
		t.start();
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
		TrackItem i = Linked;
		TrackItem p = null;
		do
		{
			if(Class == SignalType.Exit && i.Station != Linked.Station) break;
			if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction))
			{
				for(TrackItem t : MonitoringItems)
				{
					for(MainSignal s : t.SignalsListening)
					{
						if(s!=this&&!s.SignalsListening.contains(this)) s.SignalsListening.add(this);
					}
				}
				deactivateOverride();
				return;
			}
			if(i instanceof Junction)
			{
				Junction j = (Junction)i;
				if(p!=null && !j.LockedFor(p))
				{
					for(TrackItem t : MonitoringItems)
					{
						for(MainSignal s : t.SignalsListening)
						{
							if(s!=this&&!s.SignalsListening.contains(this)) s.SignalsListening.add(this);
						}
					}
					deactivateOverride();
					return;
				}
			}
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i==null||i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction);
		i = Linked;
		p = null;
		do
		{
			if(Class == SignalType.Exit && i.Station != Linked.Station) break;
			i.setBlock(Direction);
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction);
		Override = true;
		setAspect();
	}
	private void deactivateOverride()
	{
		if(!Override) return;
		TrackItem i = Linked;
		TrackItem p = null;
		boolean EndOfLock = true;
		do
		{
			if(i.Occupied!=Orientation.None) EndOfLock = false;
			if(EndOfLock&&i.BlockState==Direction) i.setBlock(Orientation.None);
			if(i!=Linked && i.SignalLinked != null && Class == SignalType.Exit) break;
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction); 
		Override = false;
		setAspect();
	}
	public void setState()
	{
		boolean prev = BlockSignal;
		BlockSignal = !Station.Opened;
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
		TrackItem i = Linked;
		TrackItem p = null;
		boolean EndOfLock = true;
		do
		{
			if(i.Occupied!=Orientation.None) EndOfLock = false;
			if(EndOfLock&&i.BlockState==Direction) i.setBlock(Orientation.None);
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction); 
		if(EndOfLock&&NextSignal!=null && NextSignal.BlockSignal) NextSignal.Close();
		if(!Automatic&&!BlockSignal) ClearRequest = false;
		setAspect();
	}
	public static Orientation OppositeDir(Orientation dir)
	{
		return dir==Orientation.Even ? Orientation.Odd : Orientation.Even;
	}
	public void TrackChanged(TrackItem t, Orientation dir, boolean Release)
	{
		setState();
		if(t==Linked&&Direction==dir)
		{
			if(!Automatic && !Release && Cleared)
			{
				ClearRequest = false;
				update();
			}
			if(!Release&&Override) setOverride(false);
		}
		update();
	}
	public void update()
	{
		if(((Automatic||BlockSignal) && (Direction == Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)!=0) || (Linked.getNext(OppositeDir(Direction)).BlockState == Direction && BlockSignal)) ClearRequest = true;
		else if(Automatic||BlockSignal) ClearRequest = false;
		if(Cleared) Clear();
		if(!ClearRequest) Close();
		else Clear();
	}
	public void setAutomatic(boolean v)
	{
		if(v)
		{
			if(MonitoringItems.isEmpty()) setMonitors();
			Automatic = true;
			if(ClearRequest) Clear();
		}
		else Automatic = false;
		setState();
		setAspect();
		Station.setState();
		List<Signal> prevs = new ArrayList<Signal>();
		prevs.addAll(SignalsListening);
		for(Signal s : prevs)
		{
			s.update();
		}
	}
	public void setAspect()
	{
		Aspect prev = SignalAspect;
		if(Cleared)
		{
			if(!Occupied)
			{
				if(NextSignal==null||(!NextSignal.Cleared&&(Class!=SignalType.Exit || NextSignal.Class != SignalType.Entry))) SignalAspect = Aspect.Anuncio_parada;
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
		setIcon(new ImageIcon(getClass().getResource("/Images/Signals/".concat((SignalAspect==Aspect.Parada&&Automatic ? "Automatic" : SignalAspect.name()).concat("_".concat(Direction.name().concat(".png")))))));
		close.setText(Cleared ? "Cerrar señal" : "Abrir señal");
		override.setText(Override ? "Desactivar rebase" : "Rebase autorizado");
		auto.setText(!Automatic ? "Modo automático" : "Modo manual");
		if(prev==SignalAspect) return;
		List<Signal> prevs = new ArrayList<Signal>();
		prevs.addAll(SignalsListening);
		for(Signal s : prevs)
		{
			s.update();
		}
		Serial.write(0);
		Serial.write(Station.AssociatedNumber);
		switch(Class)
		{
			case Exit:
				Serial.write(0);
				break;
			case Entry:
				Serial.write(1);
				break;
			case Block:
				Serial.write(2);
				break;
			case Advanced:
				Serial.write(3);
				break;
		}
		Serial.write(Number * 2 + (Direction == Orientation.Even ? 0 : 1));
		switch(SignalAspect)
		{
			case Parada:
				Serial.write(0);
				break;
			case Rebase:
				Serial.write(1);
				break;
			case Precaucion:
			case Anuncio_parada:
				Serial.write(2);
				break;
			default:
				Serial.write(3);
				break;
		}
	}
	public void setMonitors()
	{
		TrackItem i = Linked;
		TrackItem p = null;
		for(TrackItem t : MonitoringItems)
		{
			t.SignalsListening.remove(this);
		}
		MonitoringItems.clear();
		do
		{
			MonitoringItems.add(i);
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
			if(i!=null&&i.SignalLinked!=null&&i.SignalLinked instanceof MainSignal&&i.SignalLinked.Direction == Direction) i.SignalLinked.setState();
		}
		while(i!=null&&(i.SignalLinked==null||!(i.SignalLinked instanceof MainSignal)||i.SignalLinked.Direction!=Direction||i.SignalLinked.BlockSignal));
		for(TrackItem t : MonitoringItems)
		{
			if(t!=null&&!t.SignalsListening.contains(this)) t.SignalsListening.add(this);
		}
	}
}
