package scrt.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import scrt.FunctionalList;
import scrt.event.ListEvent;
import scrt.event.SRCTEvent;
import scrt.event.SRCTListener;

public class FunctionalListModel<E> implements ListModel<E>, SRCTListener {
	List<E> list;
	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
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
