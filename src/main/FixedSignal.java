package main;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class FixedSignal extends MainSignal {

	FixedSignal(Orientation dir, Aspect a, Station dep) {
		Automatic = true;
		BlockSignal = false;
		Direction = dir;
		FixedAspect = a;
		Station = dep;
		this.setForeground(Color.WHITE);
		this.setHorizontalTextPosition(CENTER);
		this.setVerticalTextPosition(TOP);
		this.setText(Name);
		this.setFont(new Font("Tahoma", 0, 10));
		setAspect();
	}
	Aspect FixedAspect;
	@Override
	public void setAutomatic(boolean b) {}
	boolean prevClear = false;
	@Override
	public void setAspect() {
		SignalAspect = FixedAspect;
		setIcon(new ImageIcon(getClass().getResource("/Images/Signals/Fixed/".concat(SignalAspect.name()).concat("_".concat(Direction.name().concat(".png"))))));
		if(prevClear==Cleared) return;
		List<Signal> prevs = new ArrayList<Signal>();
		prevs.addAll(SignalsListening);
		for(Signal s : prevs)
		{
			s.update();
		}
		prevClear = Cleared;
	}
}
