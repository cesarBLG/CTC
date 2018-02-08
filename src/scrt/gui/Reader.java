package scrt.gui;

import java.io.IOException;
import java.net.Socket;

public class Reader
{
	Socket s;
	public Reader()
	{
		new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							s = new Socket("localhost", 300);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}
}
