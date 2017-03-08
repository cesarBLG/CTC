package main;

import java.util.*;

interface AxleListener
{
	void AxleDetected(AxleCounter a, Orientation d);
	void PerformAction(AxleCounter a, Orientation d);
}
public class AxleCounter 
{
	public String Name;
	List<AxleListener> listeners = new ArrayList<AxleListener>();
	AxleCounter(String s)
	{
		Name = s;
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
	public void addListener(AxleListener al)
	{
		if(!listeners.contains(al)) listeners.add(al);
	}
}
