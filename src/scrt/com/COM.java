package scrt.com;

import java.util.ArrayList;
import java.util.List;

import scrt.Main;
import scrt.Orientation;
import scrt.com.tcp.Client;
import scrt.com.tcp.TCP;
import scrt.ctc.AxleCounter;
import scrt.ctc.Junction;
import scrt.ctc.Position;
import scrt.ctc.TrackItem;
import scrt.ctc.packet.Packet;
import scrt.ctc.Signal.Signal;
import scrt.event.SRCTEvent;
import scrt.gui.CTCIcon;

public class COM
{
	static List<Device> devs = new ArrayList<Device>();
	public static void initialize()
	{
		new TCP().initialize();
		/*Serial serial = new Serial();
		serial.begin(9600);
		devs.add(serial);*/
	}
	public static void addDevice(Device d)
	{
		devs.add(d);
	}
	public static void write(int a)
	{
		for(Device dev : devs) dev.write(a);
	}
	public static void write(byte[] data)
	{
		for(Device dev : devs) dev.write(data);
	}
	public static void send(Object o)
	{
		if(o instanceof Packet)
		{
			Packet p  = (Packet)o;
			CTCIcon.handlePacket(p);
			write(p.getState());
			return;
		}
		ArrayList<Integer> data = new ArrayList<Integer>();
		if(o instanceof TrackItem)
		{
			TrackItem t = (TrackItem)o;
			data.add(4);
			data.add(t.Station.AssociatedNumber);
			data.add(t.x);
			data.add(t.y);
			int state = 0;
			switch(t.Occupied)
			{
				case Odd:
					state += 1;
					break;
				case Even:
					state += 2;
					break;
				case Both:
					state += 3;
					break;
			}
			if(t.Acknowledged) state += 4;
			switch(t.BlockState)
			{
				case Odd:
					state += 8;
					break;
				case Even:
					state += 16;
					break;
			}
			data.add(state);
		}
		if(o instanceof Junction)
		{
			Junction j = (Junction)o;
			data.add(3);
			data.add(j.Station.AssociatedNumber);
			data.add(j.Number);
			data.add(0);
			int state = j.Switch == Position.Straight ? 0 : 1;
			state += j.Locked == -1 ? 0 : 2;
			state += j.Locked == 1 ? 4 : 0;
			state += j.Occupied == Orientation.None ? 8 : 0;
			data.add(state);
		}
		byte[] send = new byte[data.size() + 1];
		int control = 0;
		for(int i=0; i<data.size(); i++)
		{
			control += data.get(i) * ((i%2) + 1);
			send[i] = data.get(i).byteValue();
		}
		control = 255 - (control % 255);
		send[send.length - 1] = (byte) control;
		write(send);
		
	}
	public static void parse(byte[] data)
	{
		data[0] = (byte) (data[0] & 0xEFFF);
	    if(data[0]==1)
	    {
	       for(AxleCounter ac : Main.l.counters)
	       {
	    	   if(ac.Station.AssociatedNumber == data[1] && ac.Number == data[2])
	    	   {
    			   if(data[4]==0) ac.EvenPassed();
    			   else ac.OddPassed();
	    	   }
	       }
	    }
	    if(data[0]==4)
	    {
	    	for(TrackItem t : Main.l.items)
	    	{
	    		if(t.Station.AssociatedNumber == data[1] && t.x == data[2] && t.y == data[3])
	    		{
	    			if(data[4]==4 && !t.Acknowledged)
	    			{
	    				t.Acknowledged = true;
	    				t.updateState();
	    			}
	    		}
	    	}
	    }
	}
}
