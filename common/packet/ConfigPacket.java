package scrt.common.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigPacket extends Packet
{
	public boolean allowOnSight;
	public int sigsAhead;
	public int anuncioPrecaución;
	public boolean openSignals;
	public boolean lockBeforeBlock;
	public boolean lock;
	public boolean trailablePoints;
	public boolean overlap;
	public int D0;
	public int D1;
	public ConfigPacket() {}
	public ConfigPacket(String operator)
	{
		if(operator == null)
		{
			allowOnSight = true;
			sigsAhead = 2;
			anuncioPrecaución = 2;
			openSignals = true;
			lockBeforeBlock = false;
			lock = true;
			trailablePoints = false;
			overlap = true;
			D0 = 2000;
			D1 = 10000;
		}
		else if(operator.equals("FCD"))
		{
			allowOnSight = true;
			sigsAhead = 0;
			anuncioPrecaución = 0;
			openSignals = true;
			lockBeforeBlock = false;
			lock = false;
			trailablePoints = true;
			overlap = false;
			D0 = 2000;
			D1 = 10000;
		}
		else if(operator.equals("ADIF"))
		{
			allowOnSight = false;
			sigsAhead = 2;
			anuncioPrecaución = 2;
			openSignals = true;
			lockBeforeBlock = true;
			lock = true;
			trailablePoints = false;
			overlap = true;
			D0 = 30000;
			D1 = 150000;
		}
	}
	public static ConfigPacket byState(InputStream i) throws IOException
	{
		i.read();
		return new ConfigPacket(null);
	}
	@Override
	public List<Integer> getListState()
	{
		List<Integer> list = new ArrayList<>();
		return list;
	}
}
