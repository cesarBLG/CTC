package scrt.ctc;

import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.event.AxleEvent;
import scrt.event.SRCTListener;

public class AxleCounter 
{
	public Station Station;
	public int Number;
	AxleCounter EvenCounter = null;
	AxleCounter OddCounter = null;
	List<SRCTListener> listeners = new ArrayList<SRCTListener>();
	boolean Working = true;
	//List<TrackItem> Linked = null;
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
}
