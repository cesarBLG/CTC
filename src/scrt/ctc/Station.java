package scrt.ctc;

import java.util.ArrayList;
import java.util.List;

import scrt.com.COM;
import scrt.com.packet.StationRegister;
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
	public static List<Station> stations = new ArrayList<>();
	public Station(StationRegister reg)
	{
		stations.add(this);
		FullName = reg.name;
		Name = reg.shortName;
		AssociatedNumber = reg.associatedNumber;
		if(AssociatedNumber == 0) Close();
		COM.send(reg);
	}
	public static int getNumber(String name)
	{
		if(name.equals("Cen")) return 1;
		else if(name.equals("TmB")) return 3;
		else if(name.equals("CdM")) return 4;
		else if(name.equals("Arb")) return 5;
		else if(name.equals("Los")) return 6;
		else if(name.equals("Car")) return 7;
		else return 0;
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
			return this.AssociatedNumber == ((Station)o).AssociatedNumber;
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
	public static Station byNumber(int num)
	{
		for(Station s : stations)
		{
			if(s.AssociatedNumber == num) return s;
		}
		return null;
	}
}
