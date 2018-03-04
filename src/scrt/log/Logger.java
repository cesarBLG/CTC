package scrt.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger
{
	static PrintWriter out = null;
	public static void start()
	{
		try
		{
			FileWriter fw = new FileWriter("bin/log.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void trace(String s)
	{
		if(out == null) start();
		out.println(new Date() + ": " + s);
		out.close();
		out = null;
	}
}
