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
package scrt.train;

import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Axle;

public class Wagon
{
	int number;
	List<Axle> axles = new ArrayList<>();
	static int Number = 0;
	public Train train;
	public int length = 0;
	public void addAxle(Axle e)
	{
		length+=30;
		e.wagon = this;
		axles.add(e);
	}
	public Wagon()
	{
		number = ++Number;
	}
}
