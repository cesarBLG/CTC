package scrt.com.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import scrt.com.COM;

public class TCP {
	List<ClientListener> clientListeners = new ArrayList<ClientListener>();
	ServerSocket server;
	public void initialize()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					server = new ServerSocket(300);
					while(true)
					{
						ClientListener c = new ClientListener(server.accept());
						clientListeners.add(c);
						COM.addDevice(c);
					}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void free()
	{
		try
		{
			for(ClientListener c : clientListeners)
			{
				c.socket.close();
			}
			server.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
