package scrt.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Serial implements Device {
	private SerialPort sp;
	OutputStream Output;
	InputStream Input;
	public boolean Connected = false;
	public void begin(int BaudRate)
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
						@Override
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
	public void print(String a)
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
	public void print(char a)
	{
		if(!Connected) return;
		try
		{
			Output.write(a);
		}
		catch(IOException e){}
	}
	@Override
	public void write(int a)
	{
		if(!Connected) return;
		try
		{
			Output.write(a);
		}
		catch(IOException e){}
	}
	void Receive()
	{
		try
		{
			parse(Input);
		}
		catch(IOException e){}
	}
	@Override
	public void write(byte[] b)
	{
		try
		{
			Output.write(b);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
