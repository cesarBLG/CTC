package scrt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import scrt.regulation.grp.GRP;
import scrt.regulation.grp.GRPManager;
import scrt.regulation.grp.GRPRule;

public class FAIWindow extends JFrame {
	public FAIWindow(GRPManager grpManager)
	{
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Gestor de rutas y prioridades");
		JTabbedPane tab = new JTabbedPane();
		for(GRP grp : grpManager.GRPs)
		{
			if(!grp.Activated) continue;
			JPanel panel = new JPanel();
			setPanel(panel, grp);
			tab.add(grp.station.Name, panel);
		}
		add(tab);
		pack();
		setVisible(true);
	}
	void setPanel(JPanel panel, GRP grp)
	{
		panel.setLayout(new BorderLayout());
		JList rules = new JList();
		rules.setModel(new FunctionalListModel<GRPRule>(grp.rules));
		rules.setPreferredSize(new Dimension(400, 400));
		JScrollPane scroll = new JScrollPane(rules);
		panel.add(scroll);
		setPriorityChangers(panel, grp, rules);
		setButtons(panel, grp, rules);
	}
	void setPriorityChangers(JPanel panel, GRP grp, JList rules)
	{
		JPanel side = new JPanel();
		side.setLayout(new GridLayout(0,1));
		JButton up = new JButton("Subir");
		JButton down = new JButton("Bajar");
		up.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int num = rules.getSelectedIndex();
						if(num == 0) return;
						GRPRule rule = grp.rules.get(num);
						grp.rules.remove(rule);
						grp.rules.add(num - 1, rule);
					}
				});
		down.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int num = rules.getSelectedIndex();
						if(num == grp.rules.size() - 1) return;
						GRPRule rule = grp.rules.get(num);
						grp.rules.remove(rule);
						grp.rules.add(num + 1, rule);
					}
				});
		side.add(up);
		side.add(down);
		panel.add(side, BorderLayout.EAST);
	}
	void setButtons(JPanel panel, GRP grp, JList rules)
	{
		JPanel buttongrid = new JPanel();
		buttongrid.setLayout(new GridLayout(1,0));
		JButton addRule = new JButton("Añadir regla");
		addRule.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						new FAIRuleCreator(grp, null);
					}
				});
		buttongrid.add(addRule);
		JButton editRule = new JButton("Editar regla");
		editRule.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						new FAIRuleCreator(grp, grp.rules.get(rules.getSelectedIndex()));
					}
				});
		buttongrid.add(editRule);
		JButton deleteRule = new JButton("Eliminar regla");
		deleteRule.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						int index = rules.getSelectedIndex();
						if(index<0) return;
						if(JOptionPane.showConfirmDialog(panel, "¿Desea eliminar la regla de itinerarios?", "Confirmación de eliminación", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						{
							grp.rules.remove(index);
						}
					}
				});
		buttongrid.add(deleteRule);
		panel.add(buttongrid, BorderLayout.SOUTH);
	}
}
