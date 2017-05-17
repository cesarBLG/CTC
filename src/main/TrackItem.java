package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
	public Orientation BlockState = Orientation.None;
	public Orientation Occupied = Orientation.None;
	public Signal SignalLinked = null;
	public AxleCounter CounterLinked = null;
	Station Station;
	String Name = "";
	Orientation CounterDir = Orientation.None;
	TrackItem EvenItem = null;
	TrackItem OddItem = null;
	TrackItem CrossingLinked;
	int EvenAxles = 0;
	int OddAxles = 0;
	AxleCounter EvenRelease = null;
	AxleCounter OddRelease = null;
	List<AxleCounter> EvenOccupier = new ArrayList<AxleCounter>();
	List<AxleCounter> OddOccupier = new ArrayList<AxleCounter>();
	List<MainSignal> SignalsListening = new ArrayList<MainSignal>();
	JLabel TrackIcon = new JLabel();
	JLabel NumAxles = new JLabel();
	GridBagConstraints GridBag = new GridBagConstraints();
	int x = 0;
	int y = 0;
	int OddRotation = 0;
	int EvenRotation = 0;
	boolean Acknowledged = true;
	void setSignal(Signal sig)
	{
		SignalLinked = sig;
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		g.anchor = GridBagConstraints.CENTER;
		if(SignalLinked!=null)
		{
			g.insets = new Insets(5, 0, 3, 0);
			g.anchor = SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.EAST : GridBagConstraints.WEST;
			if(SignalLinked instanceof FixedSignal && (EvenItem == null || OddItem == null) ) g.anchor = SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
			add(SignalLinked, g);
		}
		/*if(SignalLinked instanceof EoT)
		{
			this.setPreferredSize(new Dimension(8,3));
		}*/
	}
	TrackItem()
	{
		
	}
	TrackItem(String label, Station dep, int oddrot, int evenrot)
	{
		Station = dep;
		if(oddrot==2) oddrot = -1;
		if(evenrot==2) evenrot = -1;
		OddRotation = oddrot;
		EvenRotation = evenrot;
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
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		this.setBackground(Color.black);
		TrackIcon.setOpaque(true);
		if(OddRotation==EvenRotation&&EvenRotation==-1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/Images/Track/Right.png")));
			TrackIcon.setMinimumSize(new Dimension(41, 36));
			TrackIcon.setPreferredSize(new Dimension(41, 36));
			TrackIcon.setMaximumSize(new Dimension(41, 36));
		}
		else if(OddRotation==EvenRotation&&EvenRotation==1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/Images/Track/Left.png")));
			TrackIcon.setMinimumSize(new Dimension(41, 36));
			TrackIcon.setPreferredSize(new Dimension(41, 36));
			TrackIcon.setMaximumSize(new Dimension(41, 36));
		}
		else
		{
			TrackIcon.setMinimumSize(new Dimension(20, 3));
			TrackIcon.setPreferredSize(new Dimension(30, 3));
			TrackIcon.setMaximumSize(new Dimension(61, 3));
		}
		g.gridy++;
		add(TrackIcon, g);
		Name = label;
		JLabel j = new JLabel(label.length()== 0 ? " " : label);
		j.setHorizontalAlignment(JLabel.CENTER);
		j.setVerticalAlignment(JLabel.TOP);
		j.setForeground(Color.yellow);
		j.setFont(new Font("Tahoma", 0, 10));
		g.gridy++;
		add(j, g);
		g.gridy++;
		NumAxles.setFont(new Font("Tahoma", 0, 10));
		NumAxles.setHorizontalAlignment(JLabel.CENTER);
		NumAxles.setVerticalAlignment(JLabel.TOP);
		NumAxles.setHorizontalTextPosition(JLabel.CENTER);
		add(NumAxles, g);
		updateIcon();
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
		if(BlockState == o) return;
		BlockState = o;
		if(EvenItem != null && EvenItem.SignalLinked!=null)
		{
			EvenItem.SignalLinked.TrackChanged(this, o, false);
		}
		if(OddItem != null && OddItem.SignalLinked!=null)
		{
			OddItem.SignalLinked.TrackChanged(this, o, false);
		}
		if(CrossingLinked!=null) CrossingLinked.setBlock(o);
		updateIcon();
	}
	void updateIcon()
	{
		if(Acknowledged) TrackIcon.setBackground(Occupied != Orientation.None ? Color.red : BlockState != Orientation.None ? Color.green : Color.yellow);
		else TrackIcon.setBackground(Color.MAGENTA);
		if(Name.length()>=1)
		{
			String n = Occupied.name();
			if(Occupied == Orientation.None && BlockState!=Orientation.None)
			{
				n = "Block".concat(BlockState.name());
			}
			NumAxles.setIcon(new ImageIcon(getClass().getResource("/Images/Track/".concat(n).concat(".png"))));
			NumAxles.setForeground(OddAxles + EvenAxles == 0 ? Color.YELLOW : Color.red);
			NumAxles.setText(Integer.toString(EvenAxles + OddAxles));
		}
		Serial.write(4);
		Serial.write(Station.AssociatedNumber);
		Serial.write(x);
		Serial.write(y);
		int state = 0;
		switch(Occupied)
		{
			case Odd:
				state += 1;
				break;
			case Even:
				state += 2;
				break;
			case Both:
				state += 3;
				break;
		}
		if(Acknowledged) state += 4;
		switch(BlockState)
		{
			case Odd:
				state += 8;
				break;
			case Even:
				state += 16;
				break;
		}
		Serial.write(state);
	}
	boolean wasFree = true;
	public void AxleDetected(AxleCounter a, Orientation dir) 
	{
		wasFree = Occupied == Orientation.None;
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
		updateOccupancy();
	}
	void updateOccupancy()
	{
		if(Occupied == Orientation.Unknown) return;
		else if(EvenAxles>0&&OddAxles>0) Occupied = Orientation.Both;
		else if(EvenAxles>0) Occupied = Orientation.Even;
		else if(OddAxles>0) Occupied = Orientation.Odd;
		else Occupied = Orientation.None;
		if(wasFree && ((Occupied == Orientation.Even && EvenItem!=null && !EvenItem.Station.equals(Station) && EvenItem.Station.isOpen())||(Occupied == Orientation.Odd && OddItem!=null && !OddItem.Station.equals(Station) && OddItem.Station.isOpen())))
		{
			Acknowledged = false;
		}
		if(Occupied == Orientation.None) Acknowledged = true;
		if(CrossingLinked != null)
		{
			if(Occupied == Orientation.None && CrossingLinked.Occupied == Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.None;
				CrossingLinked.updateOccupancy();
				List<MainSignal> ss = new ArrayList<MainSignal>();
				ss.addAll(CrossingLinked.SignalsListening);
				for(MainSignal s : ss)
				{
					s.TrackChanged(CrossingLinked, Orientation.None, false);
				}
			}
			if(Occupied != Orientation.None && Occupied != Orientation.Unknown)
			{
				CrossingLinked.Occupied = Orientation.Unknown;
				CrossingLinked.updateIcon();
				List<MainSignal> ss = new ArrayList<MainSignal>();
				ss.addAll(CrossingLinked.SignalsListening);
				for(MainSignal s : ss)
				{
					s.TrackChanged(CrossingLinked, Orientation.None, false);
				}
			}
		}
		updateIcon();
	}
	public void PerformAction(AxleCounter a, Orientation dir)
	{
		tryToFree();
		List<MainSignal> ss = new ArrayList<MainSignal>();
		ss.addAll(SignalsListening);
		for(MainSignal s : ss)
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
		updateIcon();
	}
	private void tryToFree()
	{
		boolean Free = true;
		for(MainSignal s : SignalsListening)
		{
			if(!Free) return;
			if(s.Direction!=BlockState||SignalsListening.contains(s.NextSignal)) continue;
			if(s.Cleared||s.Override) Free = false;
			TrackItem i = s.Linked;
			while(i!=null&&i!=this)
			{
				if(i.Occupied == BlockState) Free = false;
				i = i.getNext(BlockState);
			}
		}
		if(Free) setBlock(Orientation.None);
	}
	public boolean connectsTo(Orientation dir, TrackItem t)
	{
		return connectsTo(dir, t.x, t.y, dir == Orientation.Even ? t.EvenRotation : t.OddRotation);
	}
	public boolean connectsTo(Orientation dir, int objx, int objy, int objrot)
	{
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
}
