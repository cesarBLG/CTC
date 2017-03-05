package main;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

enum Orientation
{
	None,
	Odd,
	Even,
	Both,
	Unknown
}
public class TrackItem extends JLabel implements AxleListener{
	public String Name;
	public Orientation BlockState = Orientation.None;
	public Orientation Occupied = Orientation.None;
	public Signal SignalLinked = null;
	public AxleCounter CounterLinked = null;
	Orientation CounterDir = Orientation.None;
	TrackItem EvenItem = null;
	TrackItem OddItem = null;
	int EvenAxles = 0;
	int OddAxles = 0;
	AxleCounter EvenRelease = null;
	AxleCounter OddRelease = null;
	List<AxleCounter> EvenOccupier = new ArrayList<AxleCounter>();
	List<AxleCounter> OddOccupier = new ArrayList<AxleCounter>();
	TrackItem()
	{
		return;
	}
	TrackItem(String s)
	{
		Name = s;
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
				if(arg0.getButton()==MouseEvent.BUTTON1)
				{
					if(CounterLinked!=null) CounterLinked.OddPassed();
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					if(CounterLinked!=null) CounterLinked.EvenPassed();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
	
		});
		setIcon(new ImageIcon("Images/TrackFree.png"));
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
	void setBlock(Orientation o)
	{
		BlockState = o;
		updateIcon();
	}
	void updateIcon()
	{
		ImageIcon i = null;
		if(Occupied != Orientation.None)  i = new ImageIcon("Images/TrackOccupied.png");
		else if(BlockState != Orientation.None) i = new ImageIcon("Images/TrackBlocked.png");
		else i = new ImageIcon("Images/TrackFree.png");
		/*ImageIcon icono = new ImageIcon(i.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
		setIcon(icono);*/
		setIcon(i);
		this.repaint();
	}
	public void AxleDetected(AxleCounter a, Orientation dir) 
	{
		if(EvenRelease==a&&dir==Orientation.Even)
		{
			if(EvenAxles>0) EvenAxles--;
			else if(OddAxles>0) OddAxles--;
			//else throw(new CounterFailException())
		}
		else if(OddRelease==a&&dir==Orientation.Odd)
		{
			if(OddAxles>0) OddAxles--;
			else if(EvenAxles>0) EvenAxles--;
			//else throw(new CounterFailException())
		}
		if(EvenOccupier.contains(a)&&dir==Orientation.Even) EvenAxles++;
		else if(OddOccupier.contains(a)&&dir==Orientation.Odd) OddAxles++;
		if(Occupied==Orientation.Unknown) return;
		else if(EvenAxles>0&&OddAxles>0) Occupied = Orientation.Both;
		else if(EvenAxles>0) Occupied = Orientation.Even;
		else if(OddAxles>0) Occupied = Orientation.Odd;
		else
		{
			if(Occupied!=Orientation.None) setBlock(Orientation.None);
			Occupied = Orientation.None;
		}
		updateIcon();
		if(SignalLinked != null && !SignalLinked.MonitoringCounters.contains(a)) SignalLinked.AxleDetected(a, dir);
		TrackItem t = getNext(Orientation.Even);
		if(t!=null&&t.SignalLinked!=null && !t.SignalLinked.MonitoringCounters.contains(a)) t.SignalLinked.AxleDetected(a, dir);
		t = getNext(Orientation.Odd);
		if(t!=null&&t.SignalLinked!=null && !t.SignalLinked.MonitoringCounters.contains(a)) t.SignalLinked.AxleDetected(a, dir);
	}
}
