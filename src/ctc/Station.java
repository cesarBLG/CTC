package ctc;

import java.util.ArrayList;
import java.util.List;

import ctc.grp.GRP;
import ctc.train.Train;

public class Station {
	public String Name;
	public String FullName;
	public int AssociatedNumber = 0;
	public boolean Opened = true;
	List<Signal> Signals = new ArrayList<Signal>();
	List<TrackItem> Items = new ArrayList<TrackItem>();
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
		try
		{
			grp = (GRP) Class.forName("ctc.grp.".concat(Name).concat("GRP")).newInstance();
			grp.station = this;
			grp.Activated = true;
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException e){}
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
			t.setVisible(false);
		}
		for(Signal t : Signals)
		{
			t.setVisible(false);
		}
		ML = true;
	}
	public void Telemando()
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
	public void trainExited(Train t)
	{
		Trains.remove(t);
	}
}
