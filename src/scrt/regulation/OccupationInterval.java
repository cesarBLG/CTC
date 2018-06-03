/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
