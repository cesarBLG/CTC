package scrt.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import scrt.Orientation;
import scrt.ctc.Junction;
import scrt.ctc.Position;
import scrt.ctc.TrackItem;
import scrt.ctc.packet.Packet;
import scrt.ctc.Signal.SignalType;
import scrt.event.SRCTEvent;

public class JunctionIcon extends TrackIcon {

	Junction junction;
	public JLabel Locking = new JLabel();
	JLabel Direct = new JLabel();
	JLabel Desv = new JLabel();
	public JunctionIcon(Junction item) {
		this.item = item;
		junction = item;
		comp.addMouseListener(new MouseListener()
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
				if(arg0.isPopupTrigger())
				{
				}
				if(arg0.getButton()==MouseEvent.BUTTON1)
				{
					item.userChangeSwitch();
				}
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.isPopupTrigger())
				{
				}
			}
	
		});
		((Container)comp).setLayout(new GridBagLayout());
		comp.setBackground(Color.black);
		setIcon();
	}
	void setIcon()
	{
		GridBagConstraints g = new GridBagConstraints();
		boolean Upwise = (junction.Direction==Orientation.Even && junction.Class == Position.Right) || (junction.Direction==Orientation.Odd && junction.Class == Position.Left); 
		g.fill = GridBagConstraints.BOTH;
		g.insets = new Insets(0, 1, 0, 1);
		g.anchor = GridBagConstraints.CENTER;
		g.gridx = junction.Direction == Orientation.Even ? 0 : 2;
		g.gridy = 1;
		TrackIcon.setOpaque(true);
		TrackIcon.setMinimumSize(new Dimension(18, 3));
		TrackIcon.setPreferredSize(new Dimension(18, 3));
		TrackIcon.setMaximumSize(new Dimension(18, 3));
		((Container)comp).add(TrackIcon, g);
		g.gridy++;
		g.insets = new Insets(0,0,0,0);
		JLabel j = new JLabel();
		j.setVerticalAlignment(JLabel.TOP);
		j.setForeground(Color.yellow);
		j.setText("A".concat(Integer.toString(junction.Number)));
		j.setFont(new Font("Tahoma", 0, 10));
		((Container)comp).add(j, g);
		g.gridy--;
		if(junction.Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(0, 0, 0, 0);
		Locking.setOpaque(true);
		Locking.setMinimumSize(new Dimension(4, 3));
		Locking.setPreferredSize(new Dimension(4, 3));
		Locking.setMaximumSize(new Dimension(4, 3));
		((Container)comp).add(Locking, g);
		if(junction.Direction == Orientation.Even) g.gridx++;
		else g.gridx--;
		g.insets = new Insets(0, 1, 0, 1);
		Direct.setOpaque(true);
		Direct.setMinimumSize(new Dimension(4, 3));
		Direct.setPreferredSize(new Dimension(4, 3));
		Direct.setMaximumSize(new Dimension(4, 3));
		Direct.setBackground(Color.yellow);
		((Container)comp).add(Direct, g);
		g.insets = new Insets(Upwise ? 1 : 0, 0, Upwise ? 0 : 1, 0);
		g.ipady = 2;
		if(Upwise) g.gridy++;
		else g.gridy--;
		if(junction.Direction == Orientation.Even) g.gridx--;
		g.gridwidth = 2;
		g.fill = GridBagConstraints.VERTICAL;
		Desv.setVerticalAlignment(JLabel.TOP);
		Desv.setHorizontalAlignment(junction.Direction==Orientation.Even ? JLabel.LEFT : JLabel.RIGHT);
		Desv.setBackground(Color.yellow);
		Desv.setOpaque(true);
		Desv.setIcon(new ImageIcon(getClass().getResource("/scrt/Images/Junction/".concat(junction.Class.name()).concat(".png"))));
		Desv.setHorizontalAlignment(JLabel.CENTER);
		Desv.setPreferredSize(new Dimension(7, 33));
		Desv.setMaximumSize(new Dimension(7, 33));
		Desv.setMinimumSize(new Dimension(7, 33));
		((Container)comp).add(Desv, g);
		g.ipady = 0;
		JPanel jp = new JPanel();
		g.gridx = 4;
		g.gridy = 2;
		g.fill = GridBagConstraints.BOTH;
		jp.setMinimumSize(new Dimension(0,35));
		jp.setPreferredSize(new Dimension(0,35));
		jp.setMaximumSize(new Dimension(0,35));
		((Container)comp).add(jp,g);
		jp = new JPanel();
		g.gridy = 0;
		jp.setMinimumSize(new Dimension(0,35));
		jp.setPreferredSize(new Dimension(0,35));
		jp.setMaximumSize(new Dimension(0,35));
		((Container)comp).add(jp,g);
		comp.validate();
	}
	Timer FlashingTimer = new Timer(500, new ActionListener()
	{
		boolean a = false;
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Locking.setBackground(a ? Color.blue : Color.yellow);
			a = !a;
		}
	});
	@Override
	public void update()
	{
		TrackIcon.setBackground(junction.Occupied != Orientation.None ? (item.Occupied == Orientation.Unknown ? Color.white : Color.red) : junction.BlockState != Orientation.None ? Color.green : Color.yellow);
		if(junction.BlockState != Orientation.None && junction.Locked == -1)
		{
			FlashingTimer.setRepeats(true);
			FlashingTimer.start();
			FlashingTimer.setInitialDelay(0);
			Locking.setBackground(Color.blue);
		}
		else
		{
			FlashingTimer.setRepeats(false);
			FlashingTimer.stop();
			Locking.setBackground(junction.Locked != -1 ? Color.blue : Color.yellow);
		}
		if(junction.Switch==Position.Straight)
		{
			Direct.setBackground(junction.Locked==0 ? (junction.Occupied != Orientation.None ? Color.red : Color.green) : Color.yellow);
			Desv.setBackground(Color.black);
		}
		else
		{
			Direct.setBackground(Color.black);
			Desv.setBackground(junction.Locked==1 ? (junction.Occupied != Orientation.None ? Color.red : Color.green) : Color.yellow);
		}
	}
}
