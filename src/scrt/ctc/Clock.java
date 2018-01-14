package scrt.ctc;

import java.util.Date;

public class Clock {
	public static double time()
	{
		return System.currentTimeMillis() / 1000f;
	}
}
