package main;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
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
					if(Switch==Position.Straight) setSwitch(Class);
					else setSwitch(Position.Straight);
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					JOptionPane.showMessageDialog(null, Occupied.name());
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
		ImageIcon i = null;
		if(Direction==Orientation.Odd&&Class==Position.Left) i = new ImageIcon("Images/LeftJunction_Straight_Left.png");
		if(Direction==Orientation.Even&&Class==Position.Left) i = new ImageIcon("Images/LeftJunction_Straight_Right.png");
		if(Direction==Orientation.Odd&&Class==Position.Right) i = new ImageIcon("Images/RightJunction_Straight_Left.png");
		if(Direction==Orientation.Even&&Class==Position.Right) i = new ImageIcon("Images/RightJunction_Straight_Right.png");
		setIcon(i);
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
	}
	@Override
	public void AxleDetected(AxleCounter c, Orientation dir)
	{
		boolean wasFree = Occupied == Orientation.None;
		super.AxleDetected(c, dir);
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
	}
	@Override
	void updateIcon(){}
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
		setCounters(Orientation.Both);
		ImageIcon icon = null;
		if(Switch==Position.Straight)
		{

			if(Direction==Orientation.Odd&&Class==Position.Left) icon = new ImageIcon("Images/LeftJunction_Straight_Left.png");
			if(Direction==Orientation.Even&&Class==Position.Left) icon = new ImageIcon("Images/LeftJunction_Straight_Right.png");
			if(Direction==Orientation.Odd&&Class==Position.Right) icon = new ImageIcon("Images/RightJunction_Straight_Left.png");
			if(Direction==Orientation.Even&&Class==Position.Right) icon = new ImageIcon("Images/RightJunction_Straight_Right.png");
		}
		else
		{
			if(Direction==Orientation.Odd&&Class==Position.Left) icon = new ImageIcon("Images/LeftJunction_Left_Left.png");
			if(Direction==Orientation.Even&&Class==Position.Left) icon = new ImageIcon("Images/LeftJunction_Left_Right.png");
			if(Direction==Orientation.Odd&&Class==Position.Right) icon = new ImageIcon("Images/RightJunction_Right_Left.png");
			if(Direction==Orientation.Even&&Class==Position.Right) icon = new ImageIcon("Images/RightJunction_Right_Right.png");
		}
		ImageIcon icono = new ImageIcon(icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
		setIcon(icono);
		this.repaint();
	}
	public void setSwitch(Position p)
	{
		if(Occupied==Orientation.None&&Muelle!=-1) Switch = Muelle == 0 ? Position.Straight : Class;
		else if(Switch!=p&&Occupied==Orientation.None&&BlockState==Orientation.None&&Locked==-1) Switch = p;
		else return;
		updatePosition();
	}
}
