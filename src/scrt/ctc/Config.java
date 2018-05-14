package scrt.ctc;

public class Config
{
	public static boolean allowOnSight = true;
	public static int sigsAhead = 2;
	public static int anuncioPrecaución = 2;
	public static boolean openSignals = true;
	public static boolean lockBeforeBlock = false;
	public static boolean lock = true;
	public static boolean trailablePoints = true;
	public static boolean overlap = false;
	public static int D0 = 2000;
	public static int D1 = 10000;
	public static void set(String operator)
	{
		if(operator == null)
		{
			allowOnSight = true;
			sigsAhead = 2;
			anuncioPrecaución = 2;
			openSignals = true;
			lockBeforeBlock = false;
			lock = true;
			trailablePoints = false;
			overlap = true;
			D0 = 2000;
			D1 = 10000;
		}
		else if(operator.equals("FCD"))
		{
			allowOnSight = true;
			sigsAhead = 0;
			anuncioPrecaución = 0;
			openSignals = true;
			lockBeforeBlock = false;
			lock = false;
			trailablePoints = true;
			overlap = false;
			D0 = 2000;
			D1 = 10000;
		}
		else if(operator.equals("ADIF"))
		{
			allowOnSight = false;
			sigsAhead = 2;
			anuncioPrecaución = 2;
			openSignals = true;
			lockBeforeBlock = true;
			lock = true;
			trailablePoints = false;
			D0 = 30000;
			D1 = 150000;
		}
	}
}
