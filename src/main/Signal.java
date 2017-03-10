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
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class Signal extends JLabel{
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
	Signal NextSignal = null;
	TrackItem Linked;
	List<TrackItem> MonitoringItems = new ArrayList<TrackItem>();
	JPopupMenu p;
	JMenuItem close = new JMenuItem("Abrir señal");
	JMenuItem override = new JMenuItem("Rebase autorizado");
	JMenuItem auto = new JMenuItem("Modo automático");
	List<Signal> SignalsListening = new ArrayList<Signal>();
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
							setAutomatic(false);
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
		this.setForeground(Color.WHITE);
		this.setHorizontalTextPosition(CENTER);
		this.setVerticalTextPosition(TOP);
		this.setText(Name.substring(0, Name.charAt(0)=='S' ? 4 : 2));
		this.setFont(new Font("Tahoma", 0, 10));
		setAspect();
	}
	public void Clear()
	{
		Occupied = false;
		Override = false;
		Switches = false;
		if(Linked==null) return;
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
		}
		while(i!=null&&(i.SignalLinked==null||i.SignalLinked.Direction!=Direction));
		for(TrackItem t : MonitoringItems)
		{
			if(t!=null&&!t.SignalsListening.contains(this)) t.SignalsListening.add(this);
		}
		i = Linked;
		p = null;
		do
		{
			if(i==null||(i.BlockState!=Orientation.None&&i.BlockState!=Direction)||(i.Occupied!=Orientation.None&&i.Occupied!=Direction))
			{
				for(TrackItem t : MonitoringItems)
				{
					for(Signal s : t.SignalsListening)
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
						for(Signal s : t.SignalsListening)
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
		while(i==null||i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
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
		while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction);
		Cleared = true;
		NextSignal = i.SignalLinked;
		NextSignal.setState();
		if(NextSignal.Automatic==Automatization.PreviousClear)
		{
			NextSignal.Clear();
			if(!NextSignal.Cleared)
			{
				Close();
				return;
			}
		}
		if(!NextSignal.SignalsListening.contains(this)) NextSignal.SignalsListening.add(this);
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
		setAspect();
		if(Override||!Cleared||Linked==null)
		{
			Override = false;
			setAspect();
			return;
		}
		Cleared = false;
		if(Automatic == Automatization.PreviousClear)
		{
			List<Signal> ss = new ArrayList<Signal>();
			ss.addAll(SignalsListening);
			for(Signal s : ss)
			{
				if(s!=this && s.NextSignal == this&&s.Cleared)
				{
					s.Close();
				}
				SignalsListening.remove(s);
			}
		}
		NextSignal.SignalsListening.remove(this);
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
		while(i.SignalLinked==null||i.SignalLinked.Direction!=Direction); 
		if(EndOfLock&&NextSignal!=null && NextSignal.Automatic==Automatization.PreviousClear) NextSignal.Close();
		NextSignal = null;
		setAspect();
	}
	public static Orientation OppositeDir(Orientation dir)
	{
		return dir==Orientation.Even ? Orientation.Odd : Orientation.Even;
	}
	public void TrackChanged(TrackItem t, Orientation dir, boolean Release)
	{
		if(Direction==Orientation.Odd ? Linked.getNext(Orientation.Even) == t : Linked.getNext(Orientation.Odd) == t)
		{
			if((Direction == Orientation.Odd ? t.OddAxles : t.EvenAxles)!=0) ClearRequest = true;
			else
			{
				if(ClearRequest&&Automatic == Automatization.PreviousClear)
				{
					boolean canClose = true;
					for(Signal s : SignalsListening)
					{
						if(s.NextSignal == this && s.Cleared) canClose = false;
					}
					if(canClose) Close();
				}
				ClearRequest = false;
			}
		}
		if(t==Linked&&Direction==dir)
		{
			if(Automatic==Automatization.Manual && !Release) Close();
		}
		if(Cleared) Clear();
		if(Automatic==Automatization.AutoClear&&!ClearRequest)
		{
			Close();
		}
		else if(ClearRequest&&Automatic!=Automatization.Manual) Clear();
		setAspect();
	}
	public void setAutomatic(boolean v)
	{
		if(v)
		{
			Automatic = Automatization.AutoClear;
			setState();
			if(ClearRequest) Clear();
			if(Automatic==Automatization.PreviousClear)
			{
				for(Signal s : SignalsListening)
				{
					if(s.NextSignal == this && s.Cleared) Clear();
				}
			}
		}
		else Automatic = Automatization.Manual;
		setAspect();
	}
	public void setAspect()
	{
		Aspect prev = SignalAspect;
		if(Cleared)
		{
			if(!Occupied)
			{
				if(NextSignal!=null&&(NextSignal.SignalAspect==Aspect.Precaucion||NextSignal.SignalAspect==Aspect.Anuncio_parada||NextSignal.SignalAspect==Aspect.Anuncio_precaucion||NextSignal.SignalAspect==Aspect.Via_libre))
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
		setIcon(new ImageIcon("Images/Signals/".concat((SignalAspect==Aspect.Parada&&Automatic!=Automatization.Manual ? "Automatic" : SignalAspect.name()).concat("_".concat(Direction.name().concat(".png"))))));
		close.setText(Cleared ? "Cerrar señal" : "Abrir señal");
		override.setText(Override ? "Desactivar rebase" : "Rebase autorizado");
		auto.setText(Automatic == Automatization.Manual ? "Modo automático" : "Modo manual");
		if(prev==SignalAspect) return;
		for(Signal s : SignalsListening)
		{
			if(s.ClearRequest&&s.Automatic!=Automatization.Manual) s.Clear(); 
			else s.setAspect();
		}
	}
}
