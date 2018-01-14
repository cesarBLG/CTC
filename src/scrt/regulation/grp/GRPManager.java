package scrt.regulation.grp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import scrt.ctc.Loader;
import scrt.ctc.Station;

public class GRPManager {
	public List<GRP> GRPs = new ArrayList<GRP>();
	Loader l;
	public GRPManager(Loader l)
	{
		this.l = l;
		for(Station s : l.stations)
		{
			if(!s.isOpen()) continue;
			GRPs.add(new GRP(s));
		}
	}
}
