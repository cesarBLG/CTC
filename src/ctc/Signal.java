package ctc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
enum Aspect
{
Parada,
Rebase,
Precaucion,
Anuncio_parada,
Anuncio_precaucion,
Via_libre,
Apagado
}
enum SignalType
{
Exit,
Entry,
Advanced,
Block,
Shunting,
Exit_Indicator,
Switch_Indicator
}
public abstract class Signal extends JLabel
{
	public Orientation Direction;
	public String Name = "";
	SignalType Class = SignalType.Entry;
	boolean Automatic = false;
	boolean BlockSignal = false;
	public boolean Cleared = false;
	boolean Occupied = false;
	public boolean Override = false;
	boolean ClearRequest = false;
	boolean Switches = false;
	boolean allowsOnSight = false;
	public Aspect SignalAspect = Aspect.Parada;
	public List<Aspect> Aspects = new ArrayList<Aspect>();
	Station Station;
	int Track;
	int Number;
	TrackItem Linked;
	public abstract void Clear();
	public abstract void Close();
	public abstract void setState();
	public abstract void TrackChanged(TrackItem t, Orientation dir, boolean Release);
	Aspect LastAspect = Aspect.Parada;
	public void setAspect()
	{
		Serial.send(this, true);
		LastAspect = SignalAspect;
	}
	public void update() {setAspect();}
	public static Orientation OppositeDir(Orientation dir)
	{
		return dir==Orientation.Even ? Orientation.Odd : Orientation.Even;
	}
}
