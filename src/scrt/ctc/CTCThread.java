/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scrt.ctc;

import java.util.LinkedList;
import java.util.Queue;

public class CTCThread extends Thread
{
	public Queue<Runnable> tasks = new LinkedList<>();
	public CTCThread()
	{
		super("CTC Thread");
	}
	@Override
	public void run()
	{
		while(true)
		{
			Runnable r = null;
			synchronized(tasks)
			{
				if(tasks.isEmpty())
				{
					try
					{
						tasks.wait();
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else r = tasks.poll();
			}
			if(r!=null) r.run();
		}
	}
}
