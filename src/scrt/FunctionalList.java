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
package scrt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import scrt.event.ListEvent;
import scrt.event.SCRTListener;

public class FunctionalList<E> implements List<E>{
	List<E> list;
	List<SCRTListener> listeners = new ArrayList<>();
	public FunctionalList()
	{
		list = new ArrayList<>();
	}
	public FunctionalList(List<E> l)
	{
		list = l;
	}
	public void addListener(SCRTListener l)
	{
		if(!listeners.contains(l)) listeners.add(l);
	}
	public void removerListener(SCRTListener l)
	{
		listeners.remove(l);
	}
	void changed(Object e)
	{
		for(SCRTListener l : listeners)
		{
			l.actionPerformed(new ListEvent(this, e));
		}
	}
	@Override
	public boolean add(E arg0)
	{
		changed(arg0);
		return list.contains(arg0) ? false : list.add(arg0);
	}
	@Override
	public void add(int arg0, E arg1) 
	{
		changed(arg1);
		if(list.contains(arg1)) list.add(arg0, arg1);
	}
	@Override
	public boolean addAll(Collection<? extends E> arg0) 
	{
		changed(arg0);
		return list.addAll(arg0);
	}
	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) 
	{
		changed(arg1);
		return list.addAll(arg0, arg1);
	}
	@Override
	public void clear() 
	{
		changed(null);
		list.clear();
	}
	@Override
	public boolean contains(Object arg0) 
	{
		return list.contains(arg0);
	}
	@Override
	public boolean containsAll(Collection<?> arg0) 
	{
		return list.containsAll(arg0);
	}
	@Override
	public E get(int arg0)
	{
		return list.get(arg0);
	}
	@Override
	public int indexOf(Object arg0) 
	{
		return list.indexOf(arg0);
	}
	@Override
	public boolean isEmpty() 
	{
		return list.isEmpty();
	}
	@Override
	public Iterator<E> iterator() 
	{
		return list.iterator();
	}
	@Override
	public int lastIndexOf(Object arg0) 
	{
		return list.lastIndexOf(arg0);
	}
	@Override
	public ListIterator<E> listIterator() 
	{
		return list.listIterator();
	}
	@Override
	public ListIterator<E> listIterator(int arg0) 
	{
		return list.listIterator(arg0);
	}
	@Override
	public boolean remove(Object arg0) 
	{
		changed(arg0);
		return list.remove(arg0);
	}
	@Override
	public E remove(int arg0) 
	{
		changed(arg0);
		return list.remove(arg0);
	}
	@Override
	public boolean removeAll(Collection<?> arg0) 
	{
		changed(arg0);
		return list.removeAll(arg0);
	}
	@Override
	public boolean retainAll(Collection<?> arg0) 
	{
		changed(arg0);
		return list.retainAll(arg0);
	}
	@Override
	public E set(int arg0, E arg1) 
	{
		changed(arg0);
		return list.set(arg0, arg1);
	}
	@Override
	public int size() 
	{
		return list.size();
	}
	@Override
	public List<E> subList(int arg0, int arg1) 
	{
		
		return new FunctionalList<>(list.subList(arg0, arg1));
	}
	@Override
	public Object[] toArray() 
	{
		return list.toArray();
	}
	@Override
	public <T> T[] toArray(T[] arg0) 
	{
		return list.toArray(arg0);
	}
}
