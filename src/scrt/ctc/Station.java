package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Signal.Signal;
import scrt.regulation.grp.GRP;
import scrt.regulation.train.Train;

public class Station {
	public String Name;
	public String FullName;
	public int AssociatedNumber = 0;
	public boolean Opened = true;
	public List<Signal> Signals = new ArrayList<Signal>();
	public List<TrackItem> Items = new ArrayList<TrackItem>();
	List<Train> Trains = new ArrayList<Train>();
	public boolean ML = false;
	public GRP grp = null;
	public Station(String name)
	{
		Name = name;
		if(Name.equals("Cen"))
		{
			FullName = "Central";
			AssociatedNumber = 1;
		}
		if(Name.equals("TmB"))
		{
			FullName = "Tom�s Bret�n";
			AssociatedNumber = 3;
		}
		if(Name.equals("CdM"))
		{
			FullName = "Caj�n de madera";
			AssociatedNumber = 4;
		}
		if(Name.equals("Arb"))
		{
			FullName = "Arboleda";
			AssociatedNumber = 5;
		}
		if(Name.equals("Los"))
		{
			FullName = "Losilla-Cocher�n";
			AssociatedNumber = 6;
		}
		if(Name.equals("Car"))
		{
			FullName = "Carbonera";
			AssociatedNumber = 7;
		}
		if(Name.matches("[0-9]{3}"))
		{
			FullName = "V�a general";
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
		ML = true;
	}
	public void Telemando()
	{
		if(!ML) return;
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
