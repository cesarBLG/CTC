package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.*;

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
			if ("COM3".equals(currPortId.getName())) portId = currPortId;
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
		catch(Exception e){}
		Connected = true;
	}
	static void print(String a)
	{
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
			while(Input.available()!=0) Input.read();
		}
		catch(IOException e){}
	}
}
