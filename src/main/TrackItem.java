package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

enum Orientation
{
	None,
	Odd,
	Even,
	Both,
	Unknown
}
public class TrackItem extends JPanel implements AxleListener{
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
	List<Signal> SignalsListening = new ArrayList<Signal>();
	JLabel TrackIcon = new JLabel();
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
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.black);
		GridBagConstraints g = new GridBagConstraints();
		TrackIcon.setOpaque(true);
		TrackIcon.setMinimumSize(new Dimension(20, 3));
		TrackIcon.setPreferredSize(new Dimension(61, 3));
		TrackIcon.setMaximumSize(new Dimension(61, 3));
		this.add(TrackIcon, g);
		updateIcon();
		if(Name.charAt(0)=='V')
		{
			g.gridy++;
			JLabel j = new JLabel();
			j.setForeground(Color.yellow);
			if(Name.contains("/Arb/CdM")) j.setText("Vía ".concat(Name.charAt(1)=='1' ? "I" : "II"));
			else j.setText(Name.substring(1,2));
		}
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
		TrackIcon.setBackground(Occupied != Orientation.None ? Color.red : BlockState != Orientation.None ? Color.green : Color.yellow);
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
	}
	public void PerformAction(AxleCounter a, Orientation dir)
	{
		List<Signal> ss = new ArrayList<Signal>();
		ss.addAll(SignalsListening);
		for(Signal s : ss)
		{
			s.TrackChanged(this, dir, (dir == Orientation.Even ? EvenRelease : OddRelease)==a);
		}
		if(EvenItem != null && EvenItem.SignalLinked!=null)
		{
			EvenItem.SignalLinked.TrackChanged(this, dir, false);
		}
		if(OddItem != null && OddItem.SignalLinked!=null)
		{
			OddItem.SignalLinked.TrackChanged(this, dir, false);
		}
	}
}
