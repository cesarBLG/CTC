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

import scrt.Main;
import scrt.ctc.Junction.Position;
import scrt.ctc.Signal.MainSignal;
import scrt.ctc.Signal.Signal;

public class CommandParser {
	public static void Parse(String s)
	{
		if(s.equals("reset")) Main.main(null);
		if(s.equals("exit")) System.exit(0);
		if(s.length()<5) return;
		String name = s.substring(0, s.length() - 4);
		String dep = s.substring(s.length() - 3);
		for(Signal sig : Loader.signals)
		{
			if(sig.Name.equalsIgnoreCase(name)&&sig.Station.Name.equalsIgnoreCase(dep)&&sig instanceof MainSignal)
			{
				((MainSignal) sig).UserRequest(!sig.ClearRequest);
				return;
			}
		}
		for(Itinerary i : Loader.itineraries)
		{
			if(i.Name.equalsIgnoreCase(name)&&i.Station.Name.equalsIgnoreCase(dep))
			{
				i.Establish();
				return;
			}
		}
		for(TrackItem t : Loader.items)
		{
			if(t instanceof Junction)
			{
				Junction j = (Junction)t;
				if(j.Number==Integer.parseInt(name.substring(1))&&j.Station.Name.equalsIgnoreCase(dep))
				{
					j.setSwitch(j.Switch == Position.Straight ? j.Class : Position.Straight);
					return;
				}
			}
		}
	}
}
