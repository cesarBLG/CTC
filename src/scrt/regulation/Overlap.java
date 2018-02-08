package scrt.regulation;

import java.util.Date;

import scrt.regulation.timetable.TimetableEntry;

public class Overlap {
	TimetableEntry[] entries = new TimetableEntry[2];
	public Track track;
	public Date startTime;
	Date endTime;
}
