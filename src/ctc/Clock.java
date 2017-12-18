package ctc;

import java.util.Date;

public class Clock {
	public static double time()
	{
		return new Long(System.currentTimeMillis()).doubleValue() / 1000f;
	}
}
