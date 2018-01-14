package scrt.event;

import scrt.FunctionalList;

public class ListEvent extends SRCTEvent {
	public Object element;
	public ListEvent(FunctionalList<?> c, Object element) {
		super(EventType.List, c);
		this.element = element;
	}
	
}
