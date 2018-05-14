package scrt.train;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Axle;

public class Wagon
{
	int number;
	List<Axle> axles = new ArrayList<Axle>();
	static int Number = 0;
	public Train train;
	public void addAxle(Axle e)
	{
		e.wagon = this;
		axles.add(e);
	}
	public Wagon()
	{
		number = ++Number;
	}
}
