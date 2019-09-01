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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import scrt.Orientation;

public class BlockingLine
{
	public Orientation blockState = Orientation.None;
	public Orientation blockRequest = Orientation.None;
	long evenRequest = 0;
	long oddRequest = 0;
	public void request(Orientation dir, boolean request)
	{
		if(dir == Orientation.Even) evenRequest = request ? Clock.time() : 0;
		if(dir == Orientation.Odd) oddRequest = request ? Clock.time() : 0;
		if(oddRequest!=0 && evenRequest!=0) blockRequest = Orientation.Both;
		else if(evenRequest!=0) blockRequest = Orientation.Even;
		else if(oddRequest!=0) blockRequest = Orientation.Odd;
		else blockRequest = Orientation.None;
		update();
	}
	Timer t = new Timer(30000, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					handleBoth();
				}
			});
	public void update()
	{
		if(blockRequest == Orientation.None) blockState = Orientation.None;
		if(blockRequest == Orientation.Even) blockState = Orientation.Even;
		if(blockRequest == Orientation.Odd) blockState = Orientation.Odd;
		if(blockRequest == Orientation.Both)
		{
			if(!t.isRunning()) handleBoth();
		}
		else t.stop();
	}
	void handleBoth()
	{
		if(!t.isRunning())
		{
			t.start();
			t.setRepeats(true);
			t.setInitialDelay(30000 - (int)(Clock.time() - Math.min(evenRequest, oddRequest)));
		}
		else blockState = Orientation.OppositeDir(blockState);
	}
}
