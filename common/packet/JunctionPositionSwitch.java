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
package scrt.common.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import scrt.common.Position;

public class JunctionPositionSwitch extends StatePacket implements ActionPacket
{
	public enum Posibilities
	{
		Request,
		Order,
		Comprobation,
	}
	public Posibilities orderType;
	public Position position;
	public JunctionPositionSwitch(JunctionID id, Posibilities type)
	{
		super(id);
		orderType = type;
	}
	@Override
	public List<Integer> getListState()
	{
		ArrayList l = new ArrayList<>(id.getId());
		l.add(orderType.ordinal());
		l.add(position.ordinal());
		return l;
	}
	public static JunctionPositionSwitch byState(InputStream i) throws IOException
	{
		i.read();
		JunctionPositionSwitch jps = new JunctionPositionSwitch(new JunctionID(i), Posibilities.values()[i.read()]);
		jps.position = Position.values()[i.read()];
		return jps;
	}

}
