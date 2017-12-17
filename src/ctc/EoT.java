package ctc;

public class EoT extends FixedSignal {
	public EoT(Orientation dir, Station s)
	{
		super(dir, Aspect.Parada, s);
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
