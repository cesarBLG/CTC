package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import gnu.io.*;

public class Serial {
	private static SerialPort sp;
	static OutputStream Output;
	static InputStream Input;
	public static boolean Connected = false;
	public static Loader l;
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
	static void Receive()
	{
		try
		{
			if(Input.available() >= 5)
			{
				byte data[] = new byte[5];
				Input.read(data, 0, 5);
			    if(data[0]==1)
			    {
			       for(AxleCounter ac : l.counters)
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
			    	for(TrackItem t : l.items)
			    	{
			    		if(t.Station.AssociatedNumber == data[1] && t.x == data[2] && t.y == data[3])
			    		{
			    			if(data[4]==4 && !t.Acknowledged)
			    			{
			    				t.Acknowledged = true;
			    				t.updateIcon();
			    			}
			    		}
			    	}
			    }
			}
		}
		catch(IOException e){}
	}
}
