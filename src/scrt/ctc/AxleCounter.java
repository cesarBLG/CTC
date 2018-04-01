package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.Orientation;
import scrt.com.packet.ACData;
import scrt.com.packet.ACID;
import scrt.com.packet.Packet;
import scrt.event.AxleEvent;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;

public class AxleCounter extends CTCItem
{
	public Station Station;
	public int Number;
	AxleCounter EvenCounter = null;
	AxleCounter OddCounter = null;
	public boolean Working = true;
	AxleCounter(int num, Station dep)
	{
		Number = num;
		Station = dep;
	}
	public void EvenPassed()
	{
		Working = true;
		Passed(Orientation.Even);
	}
	public void OddPassed()
	{
		Working = true;
		Passed(Orientation.Odd);
	}
	public void Passed(Orientation dir)
	{
		List<SRCTListener> c = new ArrayList<SRCTListener>();
		c.addAll(listeners);
		for(SRCTListener l  : c)
		{
			if(l==null) listeners.remove(l); 
			else l.actionPerformed(new AxleEvent(this, dir, false));
		}
		for(SRCTListener l  : c)
		{
			l.actionPerformed(new AxleEvent(this, dir, true));
		}
	}
	public void Error()
	{
		Working = false;
		Passed(Orientation.Unknown);
	}
	public void addListener(SRCTListener al)
	{
		if(!listeners.contains(al)) listeners.add(al);
	}
	@Override
	public ACID getID()
	{
		ACID acid = new ACID();
		acid.stationNumber = Station.AssociatedNumber;
		acid.Num = Number;
		acid.dir = (Number % 2 == 0) ? Orientation.Even : Orientation.Odd;
		return acid;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof ACData)
		{
			ACData a = (ACData)p;
			if(!a.id.equals(getID())) return;
			if(a.dir == Orientation.Odd) OddPassed();
			else EvenPassed();
		}
	}
	@Override
	public void actionPerformed(SRCTEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void muteEvents(boolean mute)
	{
		// TODO Auto-generated method stub
		
	}
}
