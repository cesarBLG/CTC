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
package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;

import scrt.FunctionalList;
import scrt.ctc.Station;
import scrt.train.Train;

public class GRP {
	public Station station;
	public boolean Activated = false;
	public FunctionalList<GRPRule> rules = new FunctionalList<>();
	List<Train> trains = new ArrayList<>();
	public GRP(Station s) 
	{
		station = s;
		s.grp = this;
		rules.add(new GRPRule());
		rules.add(new GRPRule());
		//trains.add(new Train(27001));
	}
	public void update()
	{
		trains.sort((t1, t2) -> 
		{
			t1.setPriority();
			t2.setPriority();
			return Integer.valueOf(t1.priority).compareTo(t2.priority);
		});
		for(GRPRule rule : rules)
		{
			for(Train t : trains)
			{
				if(rule.set(t)) return;
			}
		}
	}
}
