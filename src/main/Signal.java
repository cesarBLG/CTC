package main;

import javax.swing.JLabel;
enum Aspect
{
Parada,
Rebase,
Precaucion,
Anuncio_parada,
Anuncio_precaucion,
Via_libre
}
enum SignalType
{
Exit,
Entry,
Advanced,
Block,
Shunting
}
public abstract class Signal extends JLabel
{
	Orientation Direction;
	public String Name = "";
	SignalType Class = SignalType.Entry;
	boolean Automatic = false;
	boolean BlockSignal = false;
	boolean Cleared = false;
	boolean Occupied = false;
	boolean Override = false;
	boolean ClearRequest = false;
	boolean Switches = false;
	public Aspect SignalAspect = Aspect.Parada;
	Station Station;
	int Number;
	TrackItem Linked;
	public void Clear(){}
	public void Close(){}
	public void setState(){}
	public void TrackChanged(TrackItem t, Orientation dir, boolean Release) {}
	public void setAspect(){}
	public void update() {setAspect();}
}
