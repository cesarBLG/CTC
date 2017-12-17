package ctc;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class FixedSignal extends MainSignal {

	FixedSignal(Orientation dir, Aspect a, Station dep) {
		/*if(Name.charAt(1)=='S') Class = SignalType.Exit;
		else if(Name.charAt(1)=='E' && Name.charAt(2)!='\'')Class = SignalType.Entry;
		else if(Name.charAt(1)=='E' && Name.charAt(2)=='\'') Class = SignalType.Advanced;
		else if(Name.charAt(1)=='M') Class = SignalType.Shunting;
		else */Class = SignalType.Entry;
		Automatic = true;
		BlockSignal = false;
		Direction = dir;
		Aspects.add(a);
		Station = dep;
		allowsOnSight = true;
		Cleared = a != Aspect.Parada;
		prevClear = !Cleared;
		this.setForeground(Color.WHITE);
		this.setHorizontalTextPosition(CENTER);
		this.setVerticalTextPosition(TOP);
		this.setText(Name);
		this.setFont(new Font("Tahoma", 0, 10));
		setAspect();
	}
	@Override
	public void setAutomatic(boolean b) {}
	boolean prevClear = false;
	@Override
	public void setAspect() {
		SignalAspect = Aspects.get(0);
		if(prevClear==Cleared) return;
		setIcon(new ImageIcon(getClass().getResource("/Images/Signals/Fixed/".concat(SignalAspect.name()).concat("_".concat(Direction.name().concat(".png"))))));
		prevClear = Cleared;
	}
}
