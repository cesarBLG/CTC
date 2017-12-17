package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ctc.*;

public class StationManager extends JDialog {
	public StationManager()
	{
		super();
		setTitle("Estaciones");
		setLayout(new BorderLayout());
		JPanel list = new JPanel();
		list.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = g.gridy = 0;
		g.insets = new Insets(5, 5, 5, 5);
		list.add(new JLabel("Estación"), g);
		g.gridx++;
		list.add(new JLabel("Estado"), g);
		g.gridx++;
		list.add(new JLabel("Mando"), g);
		g.gridx++;
		list.add(new JLabel("GRP"), g);
		g.gridy++;
		Hashtable<Station, JComboBox<String>[]>  state = new Hashtable<Station, JComboBox<String>[]>();
		for(Station s : Main.l.stations)
		{
			if(s.AssociatedNumber==0) continue;
			g.gridx = 0;
			list.add(new JLabel(s.FullName), g);
			g.gridx++;
			JComboBox<String> estado = new JComboBox<String>();
			estado.addItem("Abierta");
			estado.addItem("Cerrada");
			estado.setSelectedItem(s.Opened ? "Abierta" : "Cerrada");
			list.add(estado, g);
			g.gridx++;
			JComboBox<String> mando = new JComboBox<String>();
			mando.addItem("Mando Local");
			mando.addItem("Telemando");
			mando.setSelectedItem(s.ML ? "Mando Local" : "Telemando");
			list.add(mando, g);
			g.gridx++;
			JComboBox<String> grp = new JComboBox<String>();
			if(s.grp!=null) grp.addItem("Activado");
			grp.addItem("Desactivado");
			grp.setSelectedItem(s.grp != null && s.grp.Activated ? "Activado" : "Desactivado");
			list.add(grp, g);
			g.gridy++;
			state.put(s, new JComboBox[] {estado, mando, grp});
		}
		add(list, BorderLayout.CENTER);
		JButton ok = new JButton("Aceptar");
		ok.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						state.forEach((s, combo) ->
						{
							if(combo[0].getSelectedIndex() == 0) s.Open();
							else s.Close();
							if(combo[1].getSelectedIndex() == 0) s.MandoLocal();
							else s.Telemando();
							if(s.grp != null)
							{
								s.grp.Activated = combo[2].getSelectedIndex() == 0;
								//if(s.grp.Activated) s.grp.Activate();
							}
						});
						dispose();
					}
			
				});
		add(ok, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
}
