package scrt.com.packet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class ID
{
	public ElementType type;
	public int stationNumber;
	public List<Integer> getId()
	{
		List<Integer> l = new ArrayList<Integer>();
		l.add(type.ordinal());
		l.add(stationNumber);
		return l;
	}
	public ID() 
	{
		String name = this.getClass().getSimpleName();
		type = ElementType.valueOf(name.substring(0, name.length() - 2));
	}
	public ID(InputStream b) throws IOException
	{
		//type = ElementType.values()[b.read()];
		String name = this.getClass().getSimpleName();
		type = ElementType.valueOf(name.substring(0, name.length() - 2));
		stationNumber = b.read();
	}
	@Override
	public boolean equals(Object packet)
	{
		if(packet instanceof ID)
		{
			List<Integer> l1 = ((ID) packet).getId();
			List<Integer> l2 = getId();
			if(l1.size()!=l2.size()) return false;
			for(int i=0; i<l1.size(); i++)
			{
				if(!l1.get(i).equals(l2.get(i))) return false;
			}
			return true;
		}
		return super.equals(packet);
	}
	public static ID byState(InputStream i) throws IOException
	{
		ID id = null;
		try
		{
			Class<?> c = Class.forName("scrt.com.packet." + ElementType.values()[i.read()].name() + "ID");
			id = (ID) c.getConstructor(InputStream.class).newInstance(i);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
}
