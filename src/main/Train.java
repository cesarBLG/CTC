package main;

public class Train {
	String Name;
	Orientation Direction = Orientation.None;
	int NumAxles = 0;
	int Length = 0;
	Train(String s)
	{
		Name = s;
	}
}
