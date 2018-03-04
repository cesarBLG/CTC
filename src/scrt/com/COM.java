package scrt.com;

import java.util.ArrayList;
import java.util.List;

import scrt.Main;
import scrt.Orientation;
import scrt.com.packet.Packet;
import scrt.com.packet.StatePacket;
import scrt.com.tcp.ClientListener;
import scrt.com.tcp.TCP;
import scrt.ctc.AxleCounter;
import scrt.ctc.CTCItem;
import scrt.ctc.Junction;
import scrt.ctc.Position;
import scrt.ctc.TrackItem;
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
	public static void send(Packet p)
	{
		CTCIcon.PacketManager.handlePacket(p);
		write(p.getState());
		return;
	}
	public static void parse(byte[] data)
	{
		StatePacket p = StatePacket.byState(data);
		CTCItem.PacketManager.handlePacket(p);
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
