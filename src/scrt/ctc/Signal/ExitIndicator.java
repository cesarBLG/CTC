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