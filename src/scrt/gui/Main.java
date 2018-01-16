package scrt.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scrt.ctc.Loader;
import scrt.ctc.Serial;
import scrt.ctc.Station;
import scrt.ctc.TrackItem;
import scrt.regulation.timetable.TimetableEntry;
import scrt.regulation.train.Train;

public class Main {
	public static Loader l;
	public static void main(String[] args)
	{
		/*while(!Serial.Connected)
		{
			Serial.begin(9600);
		}*/
		l = new Loader();
		new GUI();
		//new scrt.regulation.Loader();
	}
}
