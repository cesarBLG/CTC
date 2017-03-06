package main;

import java.awt.Component;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class Signal extends JLabel implements AxleListener{
	enum Aspect
	{
		Parada,
		Rebase,
		Precaucion,
		Anuncio_parada,
		Anuncio_precaucion,
		Via_libre
	}
	enum Automatization
	{
		Manual,
		AutoClear,
		PreviousClear,
	}
	public String Name;
	Orientation Direction;
	Automatization Automatic = Automatization.Manual;
	boolean Cleared = false;
	boolean Occupied = false;
	boolean Override = false;
	boolean ClearRequest = false;
	boolean Switches = false;
	public Aspect SignalAspect = Aspect.Parada;
	Signal NextSignal;
	TrackItem Linked;
	List<AxleCounter> MonitoringCounters = new ArrayList<AxleCounter>();
	JPopupMenu p;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	Signal()
	{
		
	}
	Signal(String s)
	{
		Name = s;
		set(s.charAt(1)=='1' ? Orientation.Odd : Orientation.Even);
	}
	void set(Orientation dir)
	{
		Direction = dir;
		p = new JPopupMenu();
		Component c = this;
		close.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						if(!Cleared) Clear();
						else Close();
						setAutomatic(false);
					}
			
				});
		override.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						Override = !Override;
						setAspect();
					}
			
				});
		auto.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						setAutomatic(Automatic == Automatization.Manual);
					}
			
				});
		JMenuItem config = new JMenuItem("Configuración");
		config.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				String s = JOptionPane.showInputDialog(c, "Puerto de arduino");
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
					        p.show(c, arg0.getX(), arg0.getY());
						}
						if(arg0.getButton()==MouseEvent.BUTTON1)
						{
							Clear();
						}
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						if(arg0.isPopupTrigger())
						{
					        p.show(c, arg0.getX(), arg0.getY());
						}
					}
			
				});
		setAspect();
	}
	public void Clear()
	{
		if(Cleared) return;
		Occupied = false;
		Override = false;
		Switches = false;
		if(Linked==null) return;
		TrackItem i = Linked;
		TrackItem p = null;
		do
		{
			if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction)||(i.Occupied!=Orientation.None&&i.Occupied!=Direction)) return;
			if(i instanceof Junction)
			{
				Junction j = (Junction)i;
				if(p!=null && !j.LockedFor(p)) return;
			}
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i==null||i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
		for(AxleCounter c : MonitoringCounters)
		{
			c.listeners.remove(this);
		}
		MonitoringCounters.clear();
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
			if(i.EvenRelease!=null) MonitoringCounters.add(i.EvenRelease);
			if(i.OddRelease!=null) MonitoringCounters.add(i.OddRelease);
			MonitoringCounters.addAll(i.EvenOccupier);
			MonitoringCounters.addAll(i.OddOccupier);
			TrackItem n;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
		for(AxleCounter c : MonitoringCounters)
		{
			if(c!=null&&!c.listeners.contains(this)) c.listeners.add(this);
		}
		Cleared = true;
		ClearRequest = false;
		NextSignal = i.SignalLinked;
		NextSignal.setState();
		if(NextSignal.Automatic==Automatization.PreviousClear) NextSignal.Clear();
		setAspect();
	}
	public void setState()
	{
		if(Automatic==Automatization.Manual) return;
		Automatic = Automatization.AutoClear;
		TrackItem i = Linked;
		TrackItem p = null;
		do
		{
			if(i instanceof Junction && ((Junction) i).Direction!=Direction && i.getNext(((Junction) i).Direction)!=p) return;
			TrackItem n = null;
			if(p==null) n = i.getNext(Direction);
			else n = i.getNext(p);
			p = i;
			i = n;
		}
		while(i!=null&&(i.SignalLinked==null||i.SignalLinked.Direction!=Direction));
		Automatic = Automatization.PreviousClear;
	}
	public void Close()
	{
		if(Override)
		{
			Override = false;
			setAspect();
			return;
		}
		if(!Cleared) return;
		Cleared = false;
		ClearRequest = false;
		if(Linked==null) return;
		TrackItem i = Linked;
		TrackItem p = null;
		boolean EndOfLock = true;
		int in = 0;
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
		while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
		if(EndOfLock)
		{
			for(AxleCounter c : MonitoringCounters)
			{
				c.listeners.remove(this);
			}
			MonitoringCounters.clear();
		}
		setAspect();
	}
	public static Orientation OppositeDir(Orientation dir)
	{
		return dir==Orientation.Even ? Orientation.Odd : Orientation.Even;
	}
	public void AxleDetected(AxleCounter a, Orientation dir) 
	{
		if(Automatic!=Automatization.Manual&&(Direction==Orientation.Odd ? Linked.getNext(Orientation.Even).OddOccupier : Linked.getNext(Orientation.Odd).EvenOccupier).contains(a))
		{
			if(dir==Direction) ClearRequest = true;
			else if((Direction==Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)==0) Close();
		}
		if((Direction==Orientation.Odd ? Linked.getNext(Orientation.Even).OddRelease : Linked.getNext(Orientation.Odd).EvenRelease)==a)
		{
			if((Direction==Orientation.Odd ? Linked.getNext(Orientation.Even).OddAxles : Linked.getNext(Orientation.Odd).EvenAxles)==0)
			{
				ClearRequest = false;
			}
		}
		if(dir==Direction&&(Direction==Orientation.Odd ? Linked.OddOccupier : Linked.EvenOccupier).contains(a))
		{
			if(SignalAspect == Aspect.Parada) JOptionPane.showMessageDialog(null, "Tren x rebasó señal".concat(Name));
			if(Automatic==Automatization.Manual||!ClearRequest) Close();
		}
		if(MonitoringCounters.contains(a))
		{
			Occupied = false;
			TrackItem i = Linked;
			TrackItem p = null;
			boolean EndOfLock = true;
			do
			{
				if(i.Occupied==Direction) Occupied = true;
				if(i.Occupied!=Orientation.None) EndOfLock = false;
				if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction)||(i.Occupied!=Orientation.None&&i.Occupied!=Direction)) Close();
				if(i instanceof Junction)
				{
					Junction j = (Junction)i;
					if(p!=null && !j.LockedFor(p)) Close();
				}
				TrackItem n;
				if(p==null) n = i.getNext(Direction);
				else n = i.getNext(p);
				p = i;
				i = n;
			}
			while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
			if(Cleared)
			{
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
					if(i.EvenRelease!=null) MonitoringCounters.add(i.EvenRelease);
					if(i.OddRelease!=null) MonitoringCounters.add(i.OddRelease);
					MonitoringCounters.addAll(i.EvenOccupier);
					MonitoringCounters.addAll(i.OddOccupier);
					TrackItem n;
					if(p==null) n = i.getNext(Direction);
					else n = i.getNext(p);
					p = i;
					i = n;
				}
				while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
				for(AxleCounter c : MonitoringCounters)
				{
					if(c!=null&&!c.listeners.contains(this)) c.listeners.add(this);
				}
			}
		}
		if(ClearRequest&&Automatic!=Automatization.Manual) Clear();
		setAspect();
	}
	public void setAutomatic(boolean v)
	{
		if(v)
		{
			Automatic = Automatization.AutoClear;
			setState();
			if(ClearRequest) Clear();
		}
		else Automatic = Automatization.Manual;
		setAspect();
	}
	public void setAspect()
	{
		if(Cleared)
		{
			if(!Occupied)
			{
				if(NextSignal!=null&&(NextSignal.SignalAspect==Aspect.Anuncio_parada||NextSignal.SignalAspect==Aspect.Anuncio_precaucion||NextSignal.SignalAspect==Aspect.Via_libre))
				{
					if(!Switches) SignalAspect = Aspect.Via_libre;
					else SignalAspect = Aspect.Anuncio_precaucion;
				}
				else SignalAspect = Aspect.Anuncio_parada;
			}
			else SignalAspect = Aspect.Precaucion;
		}
		else if(Override) SignalAspect = Aspect.Rebase;
		else SignalAspect = Aspect.Parada;
		setIcon(new ImageIcon("Images/".concat((SignalAspect==Aspect.Parada&&Automatic!=Automatization.Manual ? "Automatic.png" : SignalAspect.name().concat(".png")))));
		close.setText(Cleared ? "Cerrar señal" : "Abrir señal");
		override.setText(Override ? "Desactivar rebase" : "Rebase autorizado");
		auto.setText(Automatic == Automatization.Manual ? "Modo automático" : "Modo manual");
	}
}
