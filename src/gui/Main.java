package gui;

import ctc.Loader;
import ctc.Serial;

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
	}
}
