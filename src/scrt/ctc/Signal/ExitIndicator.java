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

import scrt.com.packet.SignalID;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
import scrt.event.SRCTEvent;

public class ExitIndicator extends Signal{
	MainSignal MainSignal = null;
	public ExitIndicator(String s, Station dep)
	{
		super(dep);
		Name = s;
		id = new SignalID(s, dep.AssociatedNumber);
		Automatic = true;
		Direction = id.Direction;
		Class = SignalType.Exit_Indicator;
		Number = id.Number;
		Track = id.Track;
		setAspect();
	}
	void setMain()
	{
		if(MainSignal == null)
		{
			TrackItem t = Linked;
			if(t == null) return;
			while(t.SignalLinked==null || !(t.SignalLinked instanceof MainSignal) || t.SignalLinked.Direction != Direction)
			{
				t = t.getNext(Direction);
				if(t == null) return;
			}
			if(t.SignalLinked != null)
			{
				MainSignal = (scrt.ctc.Signal.MainSignal) t.SignalLinked;
				MainSignal.listeners.add(this);
			}

		}
	}
	@Override
	public void setAspect()
	{
		setMain();
		Cleared = MainSignal != null && MainSignal.SignalAspect != Aspect.Parada;
		if(Cleared) SignalAspect = Aspect.Via_libre;
		else SignalAspect = Aspect.Parada;
		super.setAspect();
	}
	@Override
	public void Lock() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Unlock() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setState() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(SRCTEvent e) {
		setAspect();
	}
	@Override
	public void muteEvents(boolean mute) {
		// TODO Auto-generated method stub
		
	}
}
