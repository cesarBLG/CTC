package scrt;

import scrt.ctc.Loader;
import scrt.gui.GUI;
import scrt.gui.editor.Editor;

public class Main {
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			Loader.load();
			scrt.regulation.Regulation.load();
			new GUI();
		}
		else new Editor();
	}
}
