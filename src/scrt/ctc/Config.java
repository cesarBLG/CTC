package scrt.ctc;

public class Config
{
	public static boolean allowOnSight = true;
	public static int sigsAhead = 2;
	public static int anuncioPrecaución = 2;
	public static boolean openSignals = true;
	public static boolean lockBeforeBlock = false;
	public static boolean lock = true;
	public static boolean trailablePoints = false;
	public static int D0 = 2000;
	public static int D1 = 10000;
	public static void set(boolean adif)
	{
		if(!adif) return;
		allowOnSight = false;
		sigsAhead = 2;
		anuncioPrecaución = 2;
		openSignals = true;
		lockBeforeBlock = false;
		lock = true;
		trailablePoints = false;
		D0 = 30000;
		D1 = 150000;
	}
}
