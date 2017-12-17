package ctc;

import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JOptionPane;

interface AxleListener
{
	void AxleDetected(AxleCounter a, Orientation d);
	void PerformAction(AxleCounter a, Orientation d);
}
public class AxleCounter 
{
	public Station Station;
	public int Number;
	AxleCounter EvenCounter = null;
	AxleCounter OddCounter = null;
	List<AxleListener> listeners = new ArrayList<AxleListener>();
	TrackItem Linked = null;
	AxleCounter(int num, Station dep)
	{
		Number = num;
		Station = dep;
	}
	public void EvenPassed()
	{
		List<AxleListener> c = new ArrayList<AxleListener>();
		c.addAll(listeners);
		for(AxleListener l  : c)
		{
			if(l==null) listeners.remove(l); 
			else l.AxleDetected(this, Orientation.Even);
		}
		for(AxleListener l  : c)
		{
			l.PerformAction(this, Orientation.Even);
		}
	}
	public void OddPassed()
	{
		List<AxleListener> c = new ArrayList<AxleListener>();
		c.addAll(listeners);
		for(AxleListener l  : c)
		{
			if(l==null) listeners.remove(l); 
			else l.AxleDetected(this, Orientation.Odd);
		}
		for(AxleListener l  : c)
		{
			l.PerformAction(this, Orientation.Odd);
		}
	}
	public void Error()
	{
		List<AxleListener> c = new ArrayList<AxleListener>();
		c.addAll(listeners);
		for(AxleListener l  : c)
		{
			if(l==null) listeners.remove(l); 
			else l.AxleDetected(this, Orientation.Unknown);
		}
		for(AxleListener l  : c)
		{
			l.PerformAction(this, Orientation.Unknown);
		}
	}
	public void addListener(AxleListener al)
	{
		if(!listeners.contains(al)) listeners.add(al);
	}
}
