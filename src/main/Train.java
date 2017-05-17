package main;

import java.util.ArrayList;
import java.util.List;

public class Train {
	String Name;
	List<TrackItem> Location = new ArrayList<TrackItem>();
	Orientation Direction = Orientation.None;
	int NumAxles = 2;
	int Length = 0;
	Train(String s)
	{
		Name = s;
	}
}
