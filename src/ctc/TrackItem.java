package ctc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import ctc.grp.GRP;
import ctc.train.Train;

public class TrackItem extends JPanel implements AxleListener{
	public Orientation BlockState = Orientation.None;
	public Orientation Occupied = Orientation.None;
	public Signal SignalLinked = null;
	public AxleCounter CounterLinked = null;
	public Station Station;
	String Name = "";
	Orientation CounterDir = Orientation.None;
	public TrackItem EvenItem = null;
	public TrackItem OddItem = null;
	public TrackItem CrossingLinked;
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
	public int x = 0;
	public int y = 0;
	public int OddRotation = 0;
	public int EvenRotation = 0;
	boolean Acknowledged = true;
	List<Train> trains = new ArrayList<Train>();
	void setSignal(Signal sig)
	{
		SignalLinked = sig;
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		if(SignalLinked!=null)
		{
			g.insets = new Insets(5, 0, 3, 0);
			g.anchor = SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHEAST : GridBagConstraints.SOUTHWEST;
			if(SignalLinked instanceof FixedSignal && (EvenItem == null || OddItem == null) ) g.anchor = SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
			add(SignalLinked, g);
		}
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
				if(arg0.getButton()==MouseEvent.BUTTON2)
				{
					
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
		g.insets = new Insets(0,0,0,0);
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		this.setBackground(Color.black);
		TrackIcon.setOpaque(true);
		if(OddRotation==EvenRotation&&EvenRotation==-1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/Images/Track/Right.png")));
			TrackIcon.setMinimumSize(new Dimension(30, 73));
			TrackIcon.setPreferredSize(new Dimension(30, 73));
			TrackIcon.setMaximumSize(new Dimension(30, 73));
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
			TrackIcon.setMinimumSize(new Dimension(30, 3));
			TrackIcon.setPreferredSize(new Dimension(30, 3));
			TrackIcon.setMaximumSize(new Dimension(30, 3));
		}
		g.gridy++;
		add(TrackIcon, g);
		Name = label;
		if(Name.length()>=1)
		{
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
		}
		if(OddRotation!=EvenRotation||OddRotation==0)
		{
			JPanel jp = new JPanel();
			g.insets = new Insets(0,0,0,0);
			g.gridx++;
			g.gridy = 0;
			g.fill = GridBagConstraints.BOTH;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			add(jp,g);
			jp = new JPanel();
			g.gridy = 2;
			if(Name.length()>=1) g.gridheight = 2;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			add(jp,g);
		}
		updateState();
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
	MainSignal BlockingSignal;
	double BlockingTime = 0;
	void setBlock(Orientation o, MainSignal blocksignal)
	{
		BlockingSignal = blocksignal;
		BlockingTime = Clock.time();
		setBlock(o);
	}
	void setBlock(Orientation o)
	{
		if(CrossingLinked!=null) CrossingLinked.setBlock(o, BlockingSignal);
		if(BlockState == o) return;
		BlockState = o;
		if(EvenItem != null && EvenItem.SignalLinked!=null)
		{
			EvenItem.SignalLinked.TrackChanged(this, o, true);
		}
		if(OddItem != null && OddItem.SignalLinked!=null)
		{
			OddItem.SignalLinked.TrackChanged(this, o, true);
		}
		List<Signal> sigs = new ArrayList<Signal>(); 
		sigs.addAll(SignalsListening);
		for(Signal s : sigs)
		{
			s.TrackChanged(this, BlockState, true);
		}
		updateState();
	}
	void updateState()
	{
		if(Acknowledged) TrackIcon.setBackground(Occupied != Orientation.None ? Color.red : BlockState != Orientation.None ? Color.green : Color.yellow);
		else TrackIcon.setBackground(Color.MAGENTA);
		if(Name.length()>=1)
		{
			String n = Occupied.name();
			if(Occupied == Orientation.None && BlockState==Orientation.Odd && BlockState==Orientation.Even)
			{
				n = "Block".concat(BlockState.name());
			}
			NumAxles.setIcon(new ImageIcon(getClass().getResource("/Images/Track/".concat(n).concat(".png"))));
			NumAxles.setForeground(OddAxles + EvenAxles == 0 ? Color.YELLOW : Color.red);
			NumAxles.setText(Integer.toString(EvenAxles + OddAxles));
		}
		Serial.send(this, true);
	}
	boolean wasFree = true;
	double OccupiedTime = 0;
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
		if(EvenOccupier.contains(a)&&dir==Orientation.Even)
		{
			EvenAxles++;
			OccupiedTime = Clock.time();
		}
		else if(OddOccupier.contains(a)&&dir==Orientation.Odd)
		{
			OddAxles++;
			OccupiedTime = Clock.time();
		}
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
			if(!trains.isEmpty())
			{
				GRP grp = (Occupied == Orientation.Even ? EvenItem : OddItem).Station.grp;
				if(grp != null) grp.monitoringTrains.add(trains.get(0));
			}
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
				CrossingLinked.updateState();
				List<MainSignal> ss = new ArrayList<MainSignal>();
				ss.addAll(CrossingLinked.SignalsListening);
				for(MainSignal s : ss)
				{
					s.TrackChanged(CrossingLinked, Orientation.None, false);
				}
			}
		}
		updateState();
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
		updateState();
	}
	boolean Done = false;
	private void tryToFree()
	{
		if(BlockingTime<=OccupiedTime) setBlock(Orientation.None);
	}
	interface TrackComparer
	{
		boolean condition(TrackItem t, Orientation dir);
		boolean criticalCondition(TrackItem t, Orientation dir);
	}
	public static List<TrackItem> PositiveExploration(TrackItem start, TrackComparer tc, Orientation dir)
	{
		List<TrackItem> list = new ArrayList<TrackItem>();
		TrackItem t = start;
		while(true)
		{
			if(!tc.condition(t, dir)) break;
			if(!tc.criticalCondition(t, dir)) return null;
			list.add(t);
			t = t.getNext(dir);
		}
		return list;
	}
	public static List<TrackItem> NegativeExploration(TrackItem start, TrackComparer tc, Orientation dir)
	{
		List<TrackItem> list = new ArrayList<TrackItem>();
		TrackItem t = start;
		while(true)
		{
			if(!tc.condition(t, dir)) break;
			if(!tc.criticalCondition(t, dir)) return null;
			list.add(t);
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(j.Direction == dir)
				{
					if(tc.condition(j.FrontItems[0], dir)) list.addAll(NegativeExploration(j.FrontItems[0], tc, dir));
					if(tc.condition(j.FrontItems[1], dir)) list.addAll(NegativeExploration(j.FrontItems[1], tc, dir));
					break;
				}
			}
			TrackItem prev = t;
			t = t.getNext(dir);
			if(!tc.condition(t, dir)||prev!=t.getNext(Signal.OppositeDir(dir))) break;
		}
		return list;
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
