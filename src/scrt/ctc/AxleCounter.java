package scrt.ctc;

import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.com.packet.ACData;
import scrt.com.packet.ACID;
import scrt.com.packet.ID;
import scrt.com.packet.Packable;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.event.AxleEvent;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;

public class AxleCounter extends CTCItem
{
	public Station Station;
	public int Number;
	AxleCounter EvenCounter = null;
	AxleCounter OddCounter = null;
	boolean Working = true;
	AxleCounter(int num, Station dep)
	{
		Number = num;
		Station = dep;
	}
	public void EvenPassed()
	{
		Passed(Orientation.Even);
	}
	public void OddPassed()
	{
		Passed(Orientation.Odd);
	}
	public void Passed(Orientation dir)
	{
		Working = true;
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
		return acid;
	}
	@Override
	public void load(Packet p)
	{
		if(p instanceof ACData)
		{
			ACData a = (ACData)p;
			if(!a.id.equals(getID())) return;
			Passed(a.dir);
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
