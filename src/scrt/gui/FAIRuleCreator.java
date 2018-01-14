package scrt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import scrt.Orientation;
import scrt.ctc.TrackItem;
import scrt.regulation.grp.GRP;
import scrt.regulation.grp.GRPRule;
import scrt.regulation.grp.TrainCondition;
import scrt.regulation.train.Train;
import scrt.regulation.train.Train.TrainClass;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import java.awt.Rectangle;
import java.awt.ComponentOrientation;

public class FAIRuleCreator extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtX;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField txtY;
	private JRadioButton rdbtnPar;
	private JRadioButton rdbtnImpar;
	private JList list;
	private JCheckBox chckbxVapor;
	private JCheckBox chckbxTrmicas;
	private JCheckBox chckbxElctricas;
	private JCheckBox chckbxTrenesDirectos;
	private JCheckBox chckbxTrenesConParada;
	private JCheckBox chckbxViajeros;
	private JCheckBox chckbxMercancas;
	private DefaultListModel<String> lm;
	/**
	 * Create the dialog.
	 * @param rule 
	 */
	public FAIRuleCreator(GRP grp, GRPRule rule) {
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton btnAceptar = new JButton("Aceptar");
				btnAceptar.setActionCommand("Aceptar");
				btnAceptar.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						GRPRule newRule = new GRPRule();
						if(chckbxMercancas.isSelected()) newRule.trainClasses.add(TrainClass.Freight);
						if(chckbxViajeros.isSelected()) newRule.trainClasses.add(TrainClass.Passengers);
						if(rdbtnPar.isSelected()) newRule.trainParity = Orientation.Even;
						else newRule.trainParity = Orientation.Odd;
						if(!lm.isEmpty())
						{
							for(int i=0; i<lm.size(); i++)
							{
								String o = lm.getElementAt(i);
								for(TrackItem t : grp.station.Items)
								{
									if(t.toString().equals(o)) newRule.trainsAt.add(t);
								}
							}
						}
						if(rule != null)
						{
							int index = grp.rules.indexOf(rule);
							grp.rules.remove(rule);
							grp.rules.add(index, newRule);
						}
						else grp.rules.add(newRule);
						dispose();
					}
				});
				panel.add(btnAceptar);
			}
			{
				JButton btnCancelar = new JButton("Cancelar");
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				panel.add(btnCancelar);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				panel_1.setLayout(new GridLayout(0, 1, 0, 0));
				{
					JLabel lblEnSentido = new JLabel("En sentido");
					panel_1.add(lblEnSentido);
				}
				{
					rdbtnPar = new JRadioButton("Par");
					buttonGroup.add(rdbtnPar);
					panel_1.add(rdbtnPar);
				}
				{
					rdbtnImpar = new JRadioButton("Impar");
					buttonGroup.add(rdbtnImpar);
					panel_1.add(rdbtnImpar);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
				{
					JLabel lblTrenesSituadosEn = new JLabel("Trenes situados en v\u00EDa:");
					panel_1.add(lblTrenesSituadosEn);
				}
				lm = new DefaultListModel<String>();
				list = new JList();
				list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setPreferredSize(new Dimension(1,60));
					panel_1.add(scrollPane);
					{
						JPanel panel_2 = new JPanel();
						scrollPane.setViewportView(panel_2);
						{
							list.setVisibleRowCount(2);
							list.setModel(lm);
							panel_2.add(list);
						}
					}
				}
				{
					JPanel panel_2 = new JPanel();
					panel_1.add(panel_2);
					panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
					{
						txtX = new JTextField("x");
						panel_2.add(txtX);
						txtX.setColumns(3);
					}
					{
						txtY = new JTextField("y");
						panel_2.add(txtY);
						txtY.setColumns(3);
					}
					{
						JButton btnAadir = new JButton("A\u00F1adir");
						btnAadir.addActionListener(new ActionListener()
								{
									@Override
									public void actionPerformed(ActionEvent arg0) 
									{
										lm.addElement(txtX.getText().concat(", ").concat(txtY.getText()));
									}
								});
						panel_2.add(btnAadir);
					}
					{
						JButton btnEliminar = new JButton("Eliminar");
						btnEliminar.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent arg0) 
							{
								if(lm.isEmpty()||list.getSelectedIndex()<0) return;
								lm.removeElementAt(list.getSelectedIndex());
							}
						});
						panel_2.add(btnEliminar);
					}
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				panel_1.setLayout(new GridLayout(0, 1, 0, 0));
				{
					chckbxVapor = new JCheckBox("Vapor");
					panel_1.add(chckbxVapor);
				}
				{
					chckbxTrmicas = new JCheckBox("T\u00E9rmicas");
					panel_1.add(chckbxTrmicas);
				}
				{
					chckbxElctricas = new JCheckBox("El\u00E9ctricas");
					panel_1.add(chckbxElctricas);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				panel_1.setLayout(new GridLayout(0, 1, 0, 0));
				{
					chckbxTrenesDirectos = new JCheckBox("Trenes directos");
					panel_1.add(chckbxTrenesDirectos);
				}
				{
					chckbxTrenesConParada = new JCheckBox("Trenes con parada");
					panel_1.add(chckbxTrenesConParada);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				panel_1.setLayout(new GridLayout(0, 1, 0, 0));
				{
					chckbxViajeros = new JCheckBox("Viajeros");
					panel_1.add(chckbxViajeros);
				}
				{
					chckbxMercancas = new JCheckBox("Mercanc\u00EDas");
					panel_1.add(chckbxMercancas);
				}
			}
		}
		setState(rule);
		pack();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	void setState(GRPRule rule)
	{
		if(rule!=null)
		{
			if(rule.trainClasses.contains(TrainClass.Passengers)) chckbxViajeros.setSelected(true);
			if(rule.trainClasses.contains(TrainClass.Freight)) chckbxMercancas.setSelected(true);
			if(rule.trainParity == Orientation.Even) rdbtnPar.setSelected(true);
			else rdbtnImpar.setSelected(true);
			for(TrackItem t : rule.trainsAt)
			{
				lm.addElement(t.toString());
			}
		}
	}
}
