package main;

public class Main {
	public static void main(String[] args)
	{
		/*while(!Serial.Connected)
		{
			Serial.begin(9600);
		}*/
		Loader l = new Loader();
		Serial.l = l;
		new GUI(l);
	}
}
