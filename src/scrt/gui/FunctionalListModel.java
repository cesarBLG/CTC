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
package scrt.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import scrt.FunctionalList;
import scrt.event.ListEvent;
import scrt.event.SRCTEvent;
import scrt.event.SCRTListener;

public class FunctionalListModel<E> implements ListModel<E>, SCRTListener {
	List<E> list;
	List<ListDataListener> listeners = new ArrayList<>();
	public FunctionalListModel(FunctionalList<E> list)
	{
		this.list = list;
		list.addListener(this);
	}
	@Override
	public void addListDataListener(ListDataListener arg0) {
		if(!listeners.contains(arg0)) listeners.add(arg0);
	}

	@Override
	public E getElementAt(int arg0) {
		return list.get(arg0);
	}
	@Override
	public int getSize() {
		return list.size();
	}
	@Override
	public void removeListDataListener(ListDataListener arg0) {
		listeners.remove(arg0);
	}
	@Override
	public void actionPerformed(SRCTEvent e) {
		if(e instanceof ListEvent)
		{
			ListDataEvent le = new ListDataEvent(list, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
			for(ListDataListener l : listeners)
			{
				l.contentsChanged(le);
			}
		}
	}
	@Override
	public void muteEvents(boolean mute) {
	}
}
