package scrt.regulation;

import java.util.Date;

public class OccupationInterval {
	Date entryTime;
	Date exitTime;
	public OccupationInterval(){}
	public OccupationInterval(Date entry, Date exit)
	{
		entryTime = entry;
		exitTime = exit;
	}
	public Date getEntry()
	{
		return entryTime;
	}
	public void setEntry(Date time)
	{
		entryTime = time;
	}
	public Date getExit()
	{
		return exitTime;
	}
	public void setExit(Date time)
	{
		exitTime = time;
	}
	public boolean overlapsWith(OccupationInterval i)
	{
		if(getEntry().compareTo(i.getEntry()) <= 0 && getExit().compareTo(i.getEntry()) > 0) return true;
		if(getEntry().compareTo(i.getExit()) < 0 && getExit().compareTo(i.getExit()) >= 0) return true;
		if(getEntry().compareTo(i.getEntry()) >= 0 && getExit().compareTo(i.getExit()) <= 0) return true;
		return false;
	}
}
