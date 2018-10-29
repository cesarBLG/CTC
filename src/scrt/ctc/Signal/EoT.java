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
package scrt.ctc.Signal;

import scrt.Orientation;
import scrt.ctc.Station;
import scrt.event.SCRTEvent;

public class EoT extends FixedSignal {
	static int evenCount = 50;
	static int oddCount = 51;
	public EoT(Orientation dir, Station s)
	{
		super("F" + String.valueOf(dir == Orientation.Even ? (evenCount+=2) : (oddCount+=2)) + "/1", dir, Aspect.Parada, s);
	}
	@Override
	public void Lock()
	{
	}
	@Override
	public void Unlock()
	{
	}
	@Override
	public void setState()
	{
	}
	@Override
	public void actionPerformed(SCRTEvent e)
	{
	}
}
