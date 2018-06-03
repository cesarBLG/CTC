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
