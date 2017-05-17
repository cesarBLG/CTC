package main;

import java.util.ArrayList;
import java.util.List;

public class Station {
	String Name;
	int AssociatedNumber = 0;
	boolean Opened = true;
	List<Signal> Signals = new ArrayList<Signal>();
	List<TrackItem> Items = new ArrayList<TrackItem>();
	boolean ML = false;
	public Station(String name)
	{
		Name = name;
		if(Name.equals("CdM")) AssociatedNumber = 4;
		if(Name.equals("Arb")) AssociatedNumber = 5;
	}
	void Open()
	{
		Opened = true;
		setState();
	}
	void Close()
	{
		Opened = false;
		setState();
	}
	void MandoLocal()
	{
		if(AssociatedNumber == 0||ML) return;
		for(TrackItem t : Items)
		{
			t.setVisible(false);
		}
		for(Signal t : Signals)
		{
			t.setVisible(false);
		}
		ML = true;
	}
	void Telemando()
	{
		if(!ML) return;
		for(TrackItem t : Items)
		{
			t.setVisible(true);
		}
		for(Signal t : Signals)
		{
			t.setVisible(true);
		}
		ML = false;
	}
	void setState()
	{
		boolean prev = Opened;
		Opened = false;
		for(Signal s : Signals)
		{
			if(s instanceof MainSignal && !s.Automatic) Opened = true;
		}
		if(prev!=Opened)
		{
			for(Signal s : Signals)
			{
				s.setState();
			}
		}
	}
	public boolean isOpen()
	{
		setState();
		return Opened;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o instanceof Station)
		{
			return this.Name.equals(((Station)o).Name);
		}
		return false;
	}
}
