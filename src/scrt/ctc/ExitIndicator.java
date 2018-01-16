package scrt.ctc;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;

import scrt.Orientation;
import scrt.event.SRCTEvent;
import scrt.gui.SignalIcon;

public class ExitIndicator extends Signal{
	MainSignal MainSignal = null;
	public ExitIndicator(String s, Station dep)
	{
		Name = s;
		Station = dep;
		Automatic = true;
		icon = new SignalIcon(this);
		Direction = Name.charAt(2)=='1' ? Orientation.Odd : Orientation.Even;
	}
	public void setAspect()
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
			MainSignal = (scrt.ctc.MainSignal) t.SignalLinked;
			MainSignal.listeners.add(this);
		}
		Cleared = MainSignal.SignalAspect != Aspect.Parada;
		if(Cleared) SignalAspect = Aspect.Via_libre;
		else SignalAspect = Aspect.Parada;
		icon.update();
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