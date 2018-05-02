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
		if(blockRequest == Orientation.Both) handleBoth();
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
