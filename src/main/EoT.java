package main;

public class EoT extends FixedSignal {
	public EoT(Orientation dir)
	{
		super(dir, Aspect.Parada, null);
	}
	@Override
	public void Clear()
	{
	}
	@Override
	public void Close()
	{
	}
	@Override
	public void TrackChanged(TrackItem t, Orientation dir, boolean Release)
	{
	}
	@Override
	public void setState()
	{
	}
}
