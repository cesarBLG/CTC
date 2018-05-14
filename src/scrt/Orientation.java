package scrt;

public enum Orientation
{
	None,
	Odd,
	Even,
	Both,
	Unknown;
	public static Orientation OppositeDir(Orientation dir)
	{
		if(dir == Orientation.Even) return Orientation.Odd;
		if(dir == Orientation.Odd) return Orientation.Even;
		return dir;
	}
}