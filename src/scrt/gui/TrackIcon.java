package scrt.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scrt.Orientation;
import scrt.ctc.FixedSignal;
import scrt.ctc.TrackItem;

public class TrackIcon extends JPanel implements CTCIcon {
	TrackItem item;
	JLabel TrackIcon = new JLabel();
	JLabel NumAxles = new JLabel();
	TrackIcon()
	{
		
	}
	public TrackIcon(TrackItem item) 
	{
		this.item = item;
		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getButton()==MouseEvent.BUTTON1)
				{
					if(item.CounterLinked!=null) item.CounterLinked.OddPassed();
				}
				if(arg0.getButton()==MouseEvent.BUTTON2)
				{
					
				}
				if(arg0.getButton()==MouseEvent.BUTTON3)
				{
					if(item.CounterLinked!=null) item.CounterLinked.EvenPassed();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
	
		});
		this.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.insets = new Insets(0,0,0,0);
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		this.setBackground(Color.black);
		TrackIcon.setOpaque(true);
		if(item.OddRotation==item.EvenRotation&&item.EvenRotation==-1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/Right.png")));
			TrackIcon.setMinimumSize(new Dimension(30, 73));
			TrackIcon.setPreferredSize(new Dimension(30, 73));
			TrackIcon.setMaximumSize(new Dimension(30, 73));
		}
		else if(item.OddRotation==item.EvenRotation&&item.EvenRotation==1)
		{
			TrackIcon.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/Left.png")));
			TrackIcon.setMinimumSize(new Dimension(41, 36));
			TrackIcon.setPreferredSize(new Dimension(41, 36));
			TrackIcon.setMaximumSize(new Dimension(41, 36));
		}
		else
		{
			TrackIcon.setMinimumSize(new Dimension(30, 3));
			TrackIcon.setPreferredSize(new Dimension(30, 3));
			TrackIcon.setMaximumSize(new Dimension(30, 3));
		}
		g.gridy++;
		add(TrackIcon, g);
		if(item.Name.length()>=1)
		{
			JLabel j = new JLabel(item.Name.length()== 0 ? " " : item.Name);
			j.setHorizontalAlignment(JLabel.CENTER);
			j.setVerticalAlignment(JLabel.TOP);
			j.setForeground(Color.yellow);
			j.setFont(new Font("Tahoma", 0, 10));
			g.gridy++;
			add(j, g);
			g.gridy++;
			NumAxles.setFont(new Font("Tahoma", 0, 10));
			NumAxles.setHorizontalAlignment(JLabel.CENTER);
			NumAxles.setVerticalAlignment(JLabel.TOP);
			NumAxles.setHorizontalTextPosition(JLabel.CENTER);
			add(NumAxles, g);
		}
		if(item.OddRotation!=item.EvenRotation||item.OddRotation==0)
		{
			JPanel jp = new JPanel();
			g.insets = new Insets(0,0,0,0);
			g.gridx++;
			g.gridy = 0;
			g.fill = GridBagConstraints.BOTH;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			add(jp,g);
			jp = new JPanel();
			g.gridy = 2;
			if(item.Name.length()>=1) g.gridheight = 2;
			jp.setMinimumSize(new Dimension(0,35));
			jp.setPreferredSize(new Dimension(0,35));
			jp.setMaximumSize(new Dimension(0,35));
			add(jp,g);
		}
	}
	public void setSignal()
	{
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		if(item.SignalLinked!=null)
		{
			g.insets = new Insets(5, 0, 3, 0);
			g.anchor = item.SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHEAST : GridBagConstraints.SOUTHWEST;
			if(item.SignalLinked instanceof FixedSignal && (item.EvenItem == null || item.OddItem == null) ) g.anchor = item.SignalLinked.Direction == Orientation.Odd ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
			add((Component) item.SignalLinked.icon, g);
		}
	}
	@Override
	public void update()
	{
		if(item.Acknowledged) TrackIcon.setBackground(item.Occupied != Orientation.None ? Color.red : item.BlockState != Orientation.None ? Color.green : Color.yellow);
		else TrackIcon.setBackground(Color.MAGENTA);
		if(item.Name.length()>=1)
		{
			String n = item.Occupied.name();
			if(item.Occupied == Orientation.None && item.BlockState==Orientation.Odd && item.BlockState==Orientation.Even)
			{
				n = "Block".concat(item.BlockState.name());
			}
			NumAxles.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Track/".concat(n).concat(".png"))));
			NumAxles.setForeground(item.OddAxles + item.EvenAxles == 0 ? Color.YELLOW : Color.red);
			NumAxles.setText(Integer.toString(item.EvenAxles + item.OddAxles));
		}
	}
}
