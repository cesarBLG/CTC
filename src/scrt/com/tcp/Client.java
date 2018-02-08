package scrt.com.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import scrt.com.COM;
import scrt.com.Device;

public class Client implements Device
{
	public Socket socket;
	static int num = 0;
	int id;
	DataInputStream in;
	DataOutputStream out;
	boolean debug = false;
	public Client(Socket socket)
	{
		id = num++;
		this.socket = socket;
		try
		{
			debug = socket.getInetAddress().equals(InetAddress.getByName(/*"192.168.2.3"*/"localhost"));
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if(debug)
					{
						/*out.writeBytes("HTTP/1.1 200 OK\r\n");
						out.writeBytes("Content-Type: text/html\r\n");
					    out.writeBytes("\r\n");
					    out.writeBytes("<h> Bienvenido al Control de Trafico Centralizado </h>");
					    out.flush();
						out.close();
					    socket.close();*/
						out.writeBytes("Bienvenido al Control de Trafico Centralizado: " + id + "\n");
					}
					while(!socket.isClosed())
					{
						if(debug) System.out.print((char)in.readByte());
						else if(in.available()>=5)
						{
							byte data[] = new byte[5];
							in.read(data, 0, 5);
							COM.parse(data);
						}
					}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}).start();
	}
	public void write(int data)
	{
		try
		{
			if(debug) out.writeBytes(Integer.toString(data & 0xFF) + " ");
			else out.writeByte(data);
		} 
		catch (IOException e)
		{
			//e.printStackTrace();
		}
	}
	public void write(byte[] b)
	{
		try
		{
			if(debug)
			{
				for(byte a : b)
				{
					out.writeBytes(Integer.toString(a & 0xFF) + " ");
				}
			}
			else out.write(b);
		} 
		catch (IOException e)
		{
			//e.printStackTrace();
		}
	}
}
