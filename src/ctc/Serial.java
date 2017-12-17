package ctc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import gnu.io.*;
import gui.Main;

public class Serial {
	private static SerialPort sp;
	static OutputStream Output;
	static InputStream Input;
	public static boolean Connected = false;
	public static void begin(int BaudRate)
	{
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements())
		{
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if ("/dev/ttyACM0".equals(currPortId.getName())||"COM3".equals(currPortId.getName()))
			{
				portId = currPortId;
				break;
			}
		}
		if(portId==null) return;
		try
		{
			sp = (SerialPort) portId.open("CTC", 2000);
			sp.setSerialPortParams(
				BaudRate,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);
			sp.setDTR(true);
			Output = sp.getOutputStream();
			Input = sp.getInputStream();
			sp.addEventListener(new SerialPortEventListener()
					{
						public void serialEvent(SerialPortEvent e)
						{
							Receive();
						}
					});
			sp.notifyOnDataAvailable(true);
		}
		catch(Exception e){return;}
		Connected = true;
	}
	static void print(String a)
	{
		if(!Connected) return;
		try
		{
			for(int i=0; i<a.length(); i++)
			{
				Output.write(a.charAt(i));
			}
		}
		catch(IOException e){}
	}
	static void print(char a)
	{
		if(!Connected) return;
		try
		{
			Output.write(a);
		}
		catch(IOException e){}
	}
	static void write(int a)
	{
		if(!Connected) return;
		try
		{
			Output.write(a);
		}
		catch(IOException e){}
	}
	static void send(Object o, boolean info)
	{
		if(o instanceof Signal)
		{
			Signal sig = (Signal)o;
			Serial.write(0);
			Serial.write(sig.Class.ordinal());
			Serial.write(sig.Station.AssociatedNumber);
			Serial.write(sig.Track);
			Serial.write(sig.Number);
			Serial.write(sig.SignalAspect.ordinal());
		}
		if(o instanceof TrackItem)
		{
			TrackItem t = (TrackItem)o;
			Serial.write(4);
			Serial.write(t.Station.AssociatedNumber);
			Serial.write(t.x);
			Serial.write(t.y);
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
			Serial.write(state);
		}
		if(o instanceof Junction)
		{
			Junction j = (Junction)o;
			Serial.write(3);
			Serial.write(j.Station.AssociatedNumber);
			Serial.write(j.Number);
			Serial.write(0);
			int data = j.Switch == Position.Straight ? 0 : 1;
			data += j.Locked == -1 ? 0 : 2;
			data += j.Locked == 1 ? 4 : 0;
			data += j.Occupied == Orientation.None ? 8 : 0;
			Serial.write(data);
		}
	}
	static void Receive()
	{
		try
		{
			if(Input.available() >= 5)
			{
				byte data[] = new byte[5];
				Input.read(data, 0, 5);
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
		catch(IOException e){}
	}
}
