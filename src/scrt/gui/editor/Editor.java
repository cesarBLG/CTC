package scrt.gui.editor;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import scrt.Orientation;
import scrt.com.packet.ACID;
import scrt.com.packet.DataPacket;
import scrt.com.packet.JunctionData;
import scrt.com.packet.JunctionID;
import scrt.com.packet.JunctionRegister;
import scrt.com.packet.LinkPacket;
import scrt.com.packet.Packet;
import scrt.com.packet.RegisterPacket;
import scrt.com.packet.SignalData;
import scrt.com.packet.SignalID;
import scrt.com.packet.SignalRegister;
import scrt.com.packet.StatePacket;
import scrt.com.packet.StationRegister;
import scrt.com.packet.TrackData;
import scrt.com.packet.TrackItemID;
import scrt.com.packet.TrackRegister;
import scrt.ctc.Junction.Position;
import scrt.ctc.Loader;
import scrt.ctc.Signal.Aspect;
import scrt.gui.CTCIcon;
import scrt.gui.JunctionIcon;
import scrt.gui.SignalIcon;
import scrt.gui.TrackIcon;
import scrt.gui.TrackLayout;

public class Editor
{
	List<Packet> packets = new ArrayList<>();
	int currentTrack = 0;
	int currentStation = 0;
	public Editor()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() 
			{
				var rem = new ArrayList<Packet>();
				for(Packet p : packets)
				{
					if(p instanceof StatePacket && p instanceof RegisterPacket)
					{
						for(Packet q : packets)
						{
							if(q==p) continue;
							if(q instanceof StatePacket && q instanceof RegisterPacket)
							{
								if(((StatePacket)p).id.equals(((StatePacket)q).id)) rem.add(q);
							}
						}
					}
					if(p instanceof SignalRegister)
					{
						boolean linked = false;
						for(Packet q : packets)
						{
							if(q instanceof LinkPacket)
							{
								if(((LinkPacket) q).id2.equals(((SignalRegister) p).id)) linked = true;
							}
						}
						if(!linked) rem.add(p);
					}
				}
				packets.removeAll(rem);
				try
				{
					var f = new FileOutputStream("layout.bin");
					for(Packet p : packets)
					{
						f.write(p.getState());
					}
					f.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}));
		TrackLayout.main(null);
		var loadPacket = Loader.parseLayoutFile();
		for(Packet p : loadPacket)
		{
			send(p);
		}
		var l = new ArrayList<CTCIcon>();
		l.addAll(CTCIcon.items);
		for(CTCIcon i : l) set(i);
		TrackLayout.frame.setFocusable(true);
		TrackLayout.frame.addKeyListener(new KeyListener()
				{
					@Override
					public void keyTyped(KeyEvent e){}
					@Override
					public void keyPressed(KeyEvent e)
					{
						int val = e.getKeyChar() - 48;
						if(val >= 0 && val < 10)
						{
							if(e.isControlDown())
							{
								currentStation = val;
								boolean exists = false;
								for(var p : packets)
								{
									if(p instanceof StationRegister)
									{
										var reg = (StationRegister)p;
										if(reg.associatedNumber == currentStation) exists = true;
									}
								}
								if(!exists)
								{
									var p = new StationRegister(currentStation);
									if(p.associatedNumber == 0)
									{
										p.name = "Plena vía";
										p.shortName = "000";
									}
									else
									{
										p.name = JOptionPane.showInputDialog("Nombre de la estación");
										if(p.name == null) return;
										p.shortName = p.name.substring(0, 3);
									}
									send(p);
								}
							}
							else currentTrack = val;
						}
					}
					@Override
					public void keyReleased(KeyEvent e){}
				});
		CTCIcon.layout.addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e){}
					@Override
					public void mousePressed(MouseEvent ev)
					{
						if(ev.getButton()==MouseEvent.BUTTON1)
						{
							var gbl = (GridBagLayout)CTCIcon.layout.getLayout();
							Point p = gbl.location(ev.getPoint().x, ev.getPoint().y);
							int x = p.x - 40;
							int y = p.y;
							if(ev.isControlDown() || ev.isAltDown())
							{
								solveFromX(x, ev.isAltDown() ? -1 : 1);
								return;
							}
							var id = new TrackItemID();
							id.stationNumber = currentStation;
							id.x = x;
							id.y = y;
							if(currentTrack>6)
							{
								var jid = new JunctionID();
								jid.Name = JOptionPane.showInputDialog("Nombre del desvío");
								jid.Number = Integer.parseInt(jid.Name.substring(1));
								jid.stationNumber = currentStation;
								var reg = new JunctionRegister(jid, id);
								reg.Class = currentTrack == 7 ? Position.Left : Position.Right;
								reg.Direction = jid.Number % 2 == 0 ? Orientation.Even : Orientation.Odd;
								send(reg);
								set(CTCIcon.findID(jid));
							}
							else
							{
								var reg = new TrackRegister(id);
								if(ev.isControlDown()) reg.Name = JOptionPane.showInputDialog("Nombre de la vía");
								if(reg.Name == null) reg.Name = "";
								int o = 0, e = 0;
								switch(currentTrack)
								{
									case 1:
										o = e = 1;
										break;
									case 2:
										o = e = -1;
										break;
									case 3:
										e = 1;
										break;
									case 4:
										e = -1;
										break;
									case 5:
										o = 1;
										break;
									case 6:
										o = -1;
										break;
									default:
										break;
								}
								reg.EvenRotation = e;
								reg.OddRotation = o;
								send(reg);
								set(CTCIcon.findID(id));
							}
						}
					}					
					@Override
					public void mouseReleased(MouseEvent e) {}
					@Override
					public void mouseEntered(MouseEvent e){}
					@Override
					public void mouseExited(MouseEvent e){}
				});
		
	}
	void solveFromX(int x, int i)
	{
		var ids = new ArrayList<TrackItemID>();
		for(var p : packets)
		{
			if(p instanceof StatePacket)
			{
				if(((StatePacket) p).id instanceof TrackItemID) ids.add((TrackItemID) ((StatePacket) p).id);
				if(p instanceof JunctionRegister) ids.add(((JunctionRegister) p).TrackId);
			}
			if(p instanceof LinkPacket)
			{
				if(((LinkPacket) p).id1 instanceof TrackItemID) ids.add((TrackItemID) ((LinkPacket) p).id1);
				if(((LinkPacket) p).id2 instanceof TrackItemID) ids.add((TrackItemID) ((LinkPacket) p).id2);
			}
		}
		for(var id : ids)
		{
			if(id.x >= x) id.x += i;
		}
	}
	void set(CTCIcon i)
	{
		i.comp.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e){}
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(e.getButton()==MouseEvent.BUTTON3)
				{
					if(JOptionPane.showConfirmDialog(null, "¿Eliminar " + i + "?", "Eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
					i.comp.getParent().remove(i.comp);
					CTCIcon.layout.repaint();
					CTCIcon.layout.revalidate();
					CTCIcon.items.remove(i);
					var pack = new ArrayList<Packet>();
					pack.addAll(packets);
					for(Packet p : pack)
					{
						if(p instanceof StatePacket)
						{
							if(((StatePacket) p).id.equals(i.getID())) packets.remove(p);
						}
						if(p instanceof LinkPacket)
						{
							var link = (LinkPacket)p;
							if(link.id1.equals(i.getID()) || link.id2.equals(i.getID())) packets.remove(p);
						}
					}
				}
			}
			@Override
			public void mouseReleased(MouseEvent e){}
			@Override
			public void mouseEntered(MouseEvent e){}
			@Override
			public void mouseExited(MouseEvent e){}
		});
		if(i instanceof SignalIcon)
		{
			var sig = (SignalIcon)i;
			var d = new SignalData((SignalID) i.getID());
			d.SignalAspect = Aspect.Parada;
			if(sig.reg.Fixed && !sig.reg.EoT) d.SignalAspect = Aspect.Anuncio_parada;
			i.load(d);
		}
		if(i instanceof TrackIcon)
		{
			var ti = (TrackIcon)i;
			if(i instanceof JunctionIcon)
			{
				var d = new JunctionData((JunctionID)i.getID());
				d.blockPosition = -1;
				d.BlockState = Orientation.None;
				d.Occupied = Orientation.None;
				d.Locked = -1;
				d.locking = false;
				i.load(d);
			}
			else
			{
				var d = new TrackData((TrackItemID) i.getID());
				d.BlockState = Orientation.None;
				d.Occupied = Orientation.None;
				d.Acknowledged = true;
				d.EvenAxles = d.OddAxles = 0;
				i.load(d);
				ti.comp.addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e){}
					@Override
					public void mousePressed(MouseEvent e)
					{
						if(e.getButton()==MouseEvent.BUTTON1)
						{
							if(e.isShiftDown())
							{
								linkTracks((TrackItemID) ti.getID());
								return;
							}
							if(ti.signal != null) return;
							String n = JOptionPane.showInputDialog("Nombre de la señal");
							if(n == null || n.isEmpty()) return;
							SignalID id = new SignalID(n, ti.getID().stationNumber);
							SignalRegister reg = new SignalRegister(id);
							reg.EoT = false;
							reg.Fixed = false;
							LinkPacket link = new LinkPacket(ti.getID(), id);
							if(CTCIcon.findID(id)!=null)
							{
								System.err.println("Señal ya existente");
								return;
							}
							send(reg);
							send(link);
							set(CTCIcon.findID(id));
						}
						if(e.getButton() == MouseEvent.BUTTON2)
						{
							if(ti.Counter != null) return;
							String val = JOptionPane.showInputDialog("Número de contador");
							if(val == null) return;
							int num = Integer.parseInt(val.startsWith("CV") ? val.substring(2) : val);
							ACID id = new ACID();
							id.dir = num % 2 == 0 ? Orientation.Even : Orientation.Odd;
							id.Num = num;
							var id2 = ti.getID();
							id.stationNumber = id2.stationNumber;
							var link = new LinkPacket(id2, id);
							send(link);
							ti.Counter.addMouseListener(new MouseListener()
							{
								@Override
								public void mouseClicked(MouseEvent e){}
								@Override
								public void mousePressed(MouseEvent e)
								{
									if(e.getButton()==MouseEvent.BUTTON3)
									{
										if(JOptionPane.showConfirmDialog(null, "¿Eliminar contador " + i.comp + "?", "Eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
										i.comp.remove(ti.Counter);
										ti.Counter = null;
										CTCIcon.layout.repaint();
										CTCIcon.layout.revalidate();
										packets.remove(link);
									}
								}
								@Override
								public void mouseReleased(MouseEvent e){}
								@Override
								public void mouseEntered(MouseEvent e){}
								@Override
								public void mouseExited(MouseEvent e){}
							});
						}
					}
					@Override
					public void mouseReleased(MouseEvent e){}
					@Override
					public void mouseEntered(MouseEvent e){}
					@Override
					public void mouseExited(MouseEvent e){}
				});
			}
		}
	}
	void send(Packet p)
	{
		if(p instanceof RegisterPacket && p instanceof StatePacket)
		{
			StatePacket s = (StatePacket)p;
			for(Packet p2 : packets)
			{
				if(p2 instanceof StatePacket)
				{
					if(s.id.equals(((StatePacket) p2).id)) return;
				}
			}
		}
		CTCIcon.PacketManager.handlePacket(p);
		if(!(p instanceof DataPacket)) packets.add(p);
	}
	TrackItemID linktid = null;
	void linkTracks(TrackItemID ltid)
	{
		if(linktid!=null)
		{
			send(new LinkPacket(linktid, ltid));
		}
		else linktid = ltid;
	}
}
