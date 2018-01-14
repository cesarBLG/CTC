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
		return dir==Orientation.Even ? Orientation.Odd : Orientation.Even;
	}
}