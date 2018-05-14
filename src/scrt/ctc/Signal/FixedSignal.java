package scrt.ctc.Signal;

import scrt.Orientation;
import scrt.ctc.Station;

public class FixedSignal extends MainSignal {
	public FixedSignal(String s, Orientation dir, Aspect a, Station dep) {
		super(dep);
		Name = s;
		if(Name.length()!=0)
		{
			Number = Integer.parseInt(Name.split("/")[0].substring(1));
			Track = 0;
		}
		//else 
		/*if(Name.charAt(1)=='S') Class = SignalType.Exit;
		else if(Name.charAt(1)=='E' && Name.charAt(2)!='\'')Class = SignalType.Entry;
		else if(Name.charAt(1)=='E' && Name.charAt(2)=='\'') Class = SignalType.Advanced;
		else if(Name.charAt(1)=='M') Class = SignalType.Shunting;
		else */Class = SignalType.Entry;
		Automatic = true;
		Direction = dir;
		Aspects.add(a);
		Station = dep;
		allowsOnSight = true;
		Cleared = a != Aspect.Parada;
		prevClear = !Cleared;
		setState();
		setAspect();
	}
	@Override
	public void setAutomatic(boolean b) {}
	boolean prevClear = false;
	@Override
	public void setClearRequest()
	{
		if(!BlockRequest() && !TrackRequest()) UserRequest = false;
		super.setClearRequest();
	}
	@Override
	public void setAspect() {
		SignalAspect = Aspects.get(0);
		send();
	}
	@Override
	int maxSigsAhead()
	{
		return SigsAhead();
	}
	@Override
	public int SigsAhead()
	{
		if(!Cleared) return 0;
		return 1;
	}
}
