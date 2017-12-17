package ctc.train;

import java.util.Date;

import ctc.Station;

public class StationStop {
	public enum StopType
	{
		No_stop,
		Commercial,
		Technical
	}
	public Station station;
	public int stopTime;
	public StopType type;
}
