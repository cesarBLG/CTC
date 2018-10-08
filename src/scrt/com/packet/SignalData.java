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
package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.ctc.Signal.Aspect;

public class SignalData extends StatePacket implements DataPacket
{
	public SignalData(SignalID packetID)
	{
		super(packetID);
	}
	public Aspect SignalAspect;
	public boolean Automatic;
	public boolean UserRequest;
	public boolean OverrideRequest;
	public boolean ClearRequest;
	public boolean MT = false;
	@Override
	public List<Integer> getListState()
	{
		List<Integer> data = new ArrayList<>();
		data.addAll(id.getId());
		data.add(SignalAspect.ordinal());
		data.add(Automatic ? 1 : 0);
		data.add(UserRequest ? 1 : 0);
		data.add(OverrideRequest ? 1 : 0);
		data.add(ClearRequest ? 1 : 0);
		data.add(MT ? 1 : 0);
		return data;
	}
	public static SignalData byState(InputStream d) throws IOException
	{
		d.read();
		SignalData s = new SignalData(new SignalID(d));
		s.SignalAspect = Aspect.values()[d.read()];
		s.Automatic = d.read() == 1;
		s.UserRequest = d.read() == 1;
		s.OverrideRequest = d.read() == 1;
		s.ClearRequest = d.read() == 1;
		s.MT = d.read() == 1;
		return s;
	}
}
