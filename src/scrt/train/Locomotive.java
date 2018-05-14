package scrt.train;
public class Locomotive extends Wagon {
	public enum PowerClass
	{
		Steam,
		Diesel,
		Diesel_electric,
		Electric
	}
	public PowerClass Class;
	int MaxWagons;
	String Name;
	public boolean NeedsWater = false;
}
