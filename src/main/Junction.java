package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

enum Position
{
	Straight,
	Left,
	Right,
}
public class Junction extends TrackItem 
{
	Orientation Direction;
	Position Switch = Position.Straight;
	Position Class;
	int Locked = -1;
	int Muelle = -1;
	TrackItem BackItem;
	TrackItem FrontItems[] = new TrackItem[2];
	AxleCounter ReleaseCounter[] = new AxleCounter[2];
	List<AxleCounter> StraightOccupier = new ArrayList<AxleCounter>();
	List<AxleCounter> CurveOccupier = new ArrayList<AxleCounter>();
	JLabel Locking = new JLabel();
	JLabel State = new JLabel();
	Junction(String s, Position p, Orientation o)
	{
		Name = s;
		Class = p;
		Direction = o;
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
				}
				if(arg0.getButton()==MouseEvent.BUTTON1)
				{
					if(Muelle!=-1) Muelle = 1-Muelle;
					if(Switch==Position.Straight) setSwitch(Class);
					else setSwitch(Position.Straight);
				}
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.isPopupTrigger())
				{
				}
			}
	
		});
		setLayout(new GridBagLayout());
		this.setBackground(Color.black);
		setIcon();
	}
	@Override
	public TrackItem getNext(Orientation o)
	{
		if(Direction!=o) return BackItem;
		else return FrontItems[Switch==Position.Straight ? 0 : 1];
	}
	@Override
	public TrackItem getNext(TrackItem t)
	{
		if((t==FrontItems[0])||(t==FrontItems[1])) return BackItem;
		if(t==BackItem) return FrontItems[Switch==Position.Straight ? 0 : 1];
		return null;
	}
	boolean LockedFor(TrackItem t)
	{
		if((t==FrontItems[0]&&Locked!=1)||(t==FrontItems[1]&&Locked!=0)||t==BackItem) return true;
		return false;
	}
	@Override
	void setBlock(Orientation o) 
	{
		super.setBlock(o);
		if(Locked == -1)
		{
			if(BlockState==Direction) Locked = Switch==Position.Straight ? 0 : 1;
			else if(BlockState!=Orientation.None)
			{
				if(FrontItems[0].BlockState==o) Locked = 0;
				else Locked = 1;
			}
		}
		if(BlockState==Orientation.None&&Occupied==Orientation.None) Locked = -1;
		updateIcon();
	}
	boolean wasFree = true;
	@Override
	public void AxleDetected(AxleCounter c, Orientation dir)
	{
		wasFree = Occupied == Orientation.None;
		super.AxleDetected(c, dir);
	}
	@Override
	public void PerformAction(AxleCounter c, Orientation dir)
	{
		super.PerformAction(c, dir);
		if(Occupied==Direction) Locked = Switch==Position.Straight ? 0 : 1;
		else if(Occupied!=Orientation.None)
		{
			if(wasFree)
			{
				if(StraightOccupier.contains(c) && dir != Direction)
				{
					Switch = Position.Straight;
					updatePosition();
					Locked = 0;
				}
				else if(CurveOccupier.contains(c) && dir != Direction)
				{
					Switch = Class;
					updatePosition();
					Locked = 1;
				}
			}
		}
		if(Occupied==Orientation.None && Muelle!=-1) setSwitch(Muelle == 0 ? Position.Straight : Class);
		if(BlockState==Orientation.None&&Occupied==Orientation.None) Locked = -1;
		updateIcon();
	}
	@Override
	void updateIcon()
	{
		TrackIcon.setBackground(Occupied != Orientation.None ? Color.red : BlockState != Orientation.None ? Color.green : Color.yellow);
		Locking.setBackground(Locked != -1 ? Color.blue : Color.yellow);
		this.remove(State);
		g.gridy = (Direction==Orientation.Even && Switch == Position.Right) || (Direction==Orientation.Odd && Switch == Position.Left) ? 1 : 0;
		g.insets = new Insets(0, 0, 0, 0);
		add(State, g);
		this.repaint();
		this.validate();
	}
	GridBagConstraints g = new GridBagConstraints();
	void setIcon()
	{
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.NORTH;
		g.gridx = Direction == Orientation.Even ? 0 : 2;
		g.gridy = (Direction==Orientation.Even && Class == Position.Right) || (Direction==Orientation.Odd && Class == Position.Left) ? 0 : 1;
		TrackIcon.setOpaque(true);
		TrackIcon.setMinimumSize(new Dimension(10, 3));
		TrackIcon.setPreferredSize(new Dimension(17, 3));
		TrackIcon.setMaximumSize(new Dimension(17, 3));
		add(TrackIcon, g);
		g.gridy = 1-g.gridy;
		JLabel j = new JLabel();
		j.setForeground(Color.yellow);
		j.setText(Name.substring(0, 2));
		add(j, g);
		g.gridy = 1-g.gridy;
		if(Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(0, 2, 0, 2);
		Locking.setOpaque(true);
		Locking.setMinimumSize(new Dimension(3, 3));
		Locking.setPreferredSize(new Dimension(4, 3));
		Locking.setMaximumSize(new Dimension(4, 3));
		add(Locking, g);
		if(Switch != Position.Straight) g.gridy = 1-g.gridy;
		if(Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(0, 0, 0, 0);
		State.setOpaque(true);
		State.setMinimumSize(new Dimension(3, 3));
		State.setPreferredSize(new Dimension(4, 3));
		State.setMaximumSize(new Dimension(4, 3));
		State.setBackground(Color.yellow);
		add(State, g);
		this.validate();
		updateIcon();
	}
	@Override
	public void setCounters(Orientation dir)
	{
		List<AxleCounter> l = new ArrayList<AxleCounter>();
		l.addAll(EvenOccupier);
		l.addAll(OddOccupier);
		l.add(EvenRelease);
		l.add(OddRelease);
		EvenOccupier.clear();
		OddOccupier.clear();
		StraightOccupier.clear();
		CurveOccupier.clear();
		ReleaseCounter[0] = ReleaseCounter[1] = null;
		EvenRelease = null;
		OddRelease = null;
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.listeners.remove(this);
		}
		if(BackItem!=null)
		{
			if(BackItem.CounterLinked!=null && BackItem.CounterDir==Direction)
			{
				if(Direction==Orientation.Even)
				{
					if(BackItem.getNext(Direction)==this) EvenOccupier.add(BackItem.CounterLinked);
					OddRelease = BackItem.CounterLinked;
				}
				else
				{
					if(BackItem.getNext(Direction)==this) OddOccupier.add(BackItem.CounterLinked);
					EvenRelease = BackItem.CounterLinked;
				}
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(BackItem.getNext(Direction)==this) EvenOccupier.addAll(BackItem.EvenOccupier);
					OddRelease = BackItem.OddRelease;
				}
				else
				{
					if(BackItem.getNext(Direction)==this) OddOccupier.addAll(BackItem.OddOccupier);
					EvenRelease = BackItem.EvenRelease;
				}
			}
		}
		if(FrontItems[0]!=null)
		{
			if(FrontItems[0].CounterLinked!=null && FrontItems[0].CounterDir!=Direction)
			{
				if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.add(FrontItems[0].CounterLinked);
				ReleaseCounter[0] = FrontItems[0].CounterLinked;
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.addAll(FrontItems[0].OddOccupier);
					ReleaseCounter[0] = FrontItems[0].EvenRelease;
				}
				else
				{
					if(FrontItems[0].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) StraightOccupier.addAll(FrontItems[0].EvenOccupier);
					ReleaseCounter[0] = FrontItems[0].OddRelease;
				}
			}
		}
		if(FrontItems[1]!=null)
		{
			if(FrontItems[1].CounterLinked!=null && FrontItems[1].CounterDir!=Direction)
			{
				if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.add(FrontItems[1].CounterLinked);
				ReleaseCounter[1] = FrontItems[1].CounterLinked;
			}
			else
			{
				if(Direction==Orientation.Even)
				{
					if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.addAll(FrontItems[1].OddOccupier);
					ReleaseCounter[1] = FrontItems[1].EvenRelease;
				}
				else
				{
					if(FrontItems[1].getNext(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even)==this) CurveOccupier.addAll(FrontItems[1].EvenOccupier);
					ReleaseCounter[1] = FrontItems[1].OddRelease;
				}
			}
		}
		if(Direction==Orientation.Even)
		{
			if(Switch==Position.Straight) EvenRelease = ReleaseCounter[0];
			else EvenRelease = ReleaseCounter[1];
			OddOccupier.addAll(StraightOccupier);
			OddOccupier.addAll(CurveOccupier);
		}
		else
		{
			if(Switch==Position.Straight) OddRelease = ReleaseCounter[0];
			else OddRelease = ReleaseCounter[1];
			EvenOccupier.addAll(StraightOccupier);
			EvenOccupier.addAll(CurveOccupier);
		}
		l.clear();
		l.add(EvenRelease);
		l.addAll(EvenOccupier);
		l.add(OddRelease);
		l.addAll(OddOccupier);
		for(AxleCounter ac : l)
		{
			if(ac!=null) ac.addListener(this);
		}
		if(dir==Orientation.Both)
		{
			if(FrontItems[0]!=null) FrontItems[0].setCounters(Direction);
			if(FrontItems[1]!=null) FrontItems[1].setCounters(Direction);
			if(BackItem!=null) BackItem.setCounters(Direction == Orientation.Even ? Orientation.Odd : Orientation.Even);
		}
		else if(dir==Direction)
		{
			if(FrontItems[0]!=null) FrontItems[0].setCounters(Direction);
			if(FrontItems[1]!=null) FrontItems[1].setCounters(Direction);
		}
		else if(dir!=Orientation.None)
		{
			if(BackItem!=null) BackItem.setCounters(dir);
		}
	}
	public void updatePosition()
	{
		updateIcon();
		setCounters(Orientation.Both);
	}
	public void setSwitch(Position p)
	{
		if(Occupied==Orientation.None&&Muelle!=-1) Switch = Muelle == 0 ? Position.Straight : Class;
		else if(Switch!=p&&Occupied==Orientation.None&&BlockState==Orientation.None&&Locked==-1) Switch = p;
		else return;
		updatePosition();
	}
}
