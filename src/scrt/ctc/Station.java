package scrt.ctc;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import scrt.regulation.grp.GRP;
import scrt.regulation.train.Train;

public class Station {
	public String Name;
	public String FullName;
	public int AssociatedNumber = 0;
	public boolean Opened = true;
	List<Signal> Signals = new ArrayList<Signal>();
	public List<TrackItem> Items = new ArrayList<TrackItem>();
	List<Train> Trains = new ArrayList<Train>();
	public boolean ML = false;
	public GRP grp = null;
	public Station(String name)
	{
		Name = name;
		if(Name.equals("CdM"))
		{
			FullName = "Cajón de madera";
			AssociatedNumber = 4;
		}
		if(Name.equals("Arb"))
		{
			FullName = "Arboleda";
			AssociatedNumber = 5;
		}
		if(Name.equals("Los"))
		{
			FullName = "Losilla-Cocherón";
			AssociatedNumber = 6;
		}
		if(Name.equals("TmB"))
		{
			FullName = "Tomás Bretón";
			AssociatedNumber = 5;
		}
		if(Name.matches("[0-9]{3}"))
		{
			FullName = "Vía general";
			Close();
		}
	}
	public void Open()
	{
		if(Opened||AssociatedNumber==0) return;
		Opened = true;
		for(Signal s : Signals)
		{
			s.setState();
		}
	}
	public void Close()
	{
		if(!Opened) return;
		Opened = false;
		for(Signal s : Signals)
		{
			s.setState();
		}
	}
	public void MandoLocal()
	{
		if(AssociatedNumber == 0||ML) return;
		for(TrackItem t : Items)
		{
			((Component) t.icon).setVisible(false);
		}
		for(Signal s : Signals)
		{
			((Component) s.icon).setVisible(false);
		}
		ML = true;
	}
	public void Telemando()
	{
		if(!ML) return;
		for(TrackItem t : Items)
		{
			((Component) t.icon).setVisible(true);
		}
		for(Signal s : Signals)
		{
			((Component) s.icon).setVisible(true);
		}
		ML = false;
	}
	public boolean isOpen()
	{
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
	@Override
	public String toString()
	{
		return FullName;
	}
	public void trainExited(Train t)
	{
		Trains.remove(t);
	}
}
