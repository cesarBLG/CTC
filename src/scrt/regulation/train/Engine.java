package scrt.regulation.train;
public class Engine {
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
