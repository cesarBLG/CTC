package scrt.ctc;

import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.ctc.packet.ACData;
import scrt.ctc.packet.Packable;
import scrt.ctc.packet.Packet;
import scrt.event.AxleEvent;
import scrt.event.SRCTListener;

public class AxleCounter implements Packable
{
	public Station Station;
	public int Number;
	AxleCounter EvenCounter = null;
	AxleCounter OddCounter = null;
	List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	boolean Working = true;
	AxleCounter(int num, Station dep)
	{
		Number = num;
		Station = dep;
		CTCItem.PacketManager.items.add(this);
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
	public Packet getPacket()
	{
		ACData a = new ACData();
		a.stationNumber = Station.AssociatedNumber;
		a.Num = Number;
		return a;
	}
	@Override
	public void load(Packet p)
	{
		ACData a = (ACData)p;
		Passed(a.dir);
	}
}
