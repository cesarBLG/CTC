package scrt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class TimetableWindow extends JDialog
{
	public TimetableWindow()
	{
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lblTrenes = new JLabel("Trenes");
		lblTrenes.setVerticalAlignment(SwingConstants.TOP);
		panel.add(lblTrenes);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollPane);
		
		JPanel panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		
		JList list = new JList();
		panel_2.add(list);
		list.setModel(new FunctionalListModel(scrt.regulation.Regulation.services));
		list.setPreferredSize(new Dimension(40, 100));
		
		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, BorderLayout.CENTER);
		panel_3.setBorder(new EmptyBorder(0, 10, 0, 10));
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{374, 0};
		gbl_panel_3.rowHeights = new int[]{98, 35, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel_3.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblTren = new JLabel("TREN");
		GridBagConstraints gbc_lblTren = new GridBagConstraints();
		gbc_lblTren.anchor = GridBagConstraints.WEST;
		gbc_lblTren.insets = new Insets(0, 0, 5, 5);
		gbc_lblTren.gridx = 0;
		gbc_lblTren.gridy = 0;
		panel_1.add(lblTren, gbc_lblTren);
		
		JLabel lblNmero = new JLabel("N\u00FAmero");
		GridBagConstraints gbc_lblNmero = new GridBagConstraints();
		gbc_lblNmero.insets = new Insets(0, 0, 5, 5);
		gbc_lblNmero.gridx = 0;
		gbc_lblNmero.gridy = 1;
		panel_1.add(lblNmero, gbc_lblNmero);
		
		JLabel lblClase = new JLabel("Clase");
		GridBagConstraints gbc_lblClase = new GridBagConstraints();
		gbc_lblClase.insets = new Insets(0, 0, 5, 5);
		gbc_lblClase.gridx = 1;
		gbc_lblClase.gridy = 1;
		panel_1.add(lblClase, gbc_lblClase);
		
		JLabel lblTipo = new JLabel("Tipo");
		GridBagConstraints gbc_lblTipo = new GridBagConstraints();
		gbc_lblTipo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTipo.gridx = 2;
		gbc_lblTipo.gridy = 1;
		panel_1.add(lblTipo, gbc_lblTipo);
		
		JLabel lblLongitud = new JLabel("Longitud");
		GridBagConstraints gbc_lblLongitud = new GridBagConstraints();
		gbc_lblLongitud.insets = new Insets(0, 0, 5, 5);
		gbc_lblLongitud.gridx = 3;
		gbc_lblLongitud.gridy = 1;
		panel_1.add(lblLongitud, gbc_lblLongitud);
		
		JLabel lblOrigen = new JLabel("Origen");
		GridBagConstraints gbc_lblOrigen = new GridBagConstraints();
		gbc_lblOrigen.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrigen.gridx = 4;
		gbc_lblOrigen.gridy = 1;
		panel_1.add(lblOrigen, gbc_lblOrigen);
		
		JLabel lblDestino = new JLabel("Destino");
		GridBagConstraints gbc_lblDestino = new GridBagConstraints();
		gbc_lblDestino.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestino.gridx = 5;
		gbc_lblDestino.gridy = 1;
		panel_1.add(lblDestino, gbc_lblDestino);
		
		JLabel lblSalida = new JLabel("Salida");
		GridBagConstraints gbc_lblSalida = new GridBagConstraints();
		gbc_lblSalida.insets = new Insets(0, 0, 5, 0);
		gbc_lblSalida.gridx = 6;
		gbc_lblSalida.gridy = 1;
		panel_1.add(lblSalida, gbc_lblSalida);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 1;
		panel_3.add(scrollPane_1, gbc_scrollPane_1);
		
		JPanel panel_4 = new JPanel();
		scrollPane_1.setViewportView(panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_4.rowHeights = new int[]{0, 0, 0};
		gbl_panel_4.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_4.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_4.setLayout(gbl_panel_4);
		
		JLabel lblHorario = new JLabel("HORARIO");
		GridBagConstraints gbc_lblHorario = new GridBagConstraints();
		gbc_lblHorario.fill = GridBagConstraints.BOTH;
		gbc_lblHorario.gridwidth = 7;
		gbc_lblHorario.insets = new Insets(0, 0, 5, 5);
		gbc_lblHorario.gridx = 0;
		gbc_lblHorario.gridy = 0;
		panel_4.add(lblHorario, gbc_lblHorario);
		
		JLabel lblPk = new JLabel("PK");
		GridBagConstraints gbc_lblPk = new GridBagConstraints();
		gbc_lblPk.insets = new Insets(0, 0, 0, 5);
		gbc_lblPk.gridx = 0;
		gbc_lblPk.gridy = 1;
		panel_4.add(lblPk, gbc_lblPk);
		
		JLabel lblVmax = new JLabel("Vel. m\u00E1x.");
		GridBagConstraints gbc_lblVmax = new GridBagConstraints();
		gbc_lblVmax.insets = new Insets(0, 0, 0, 5);
		gbc_lblVmax.gridx = 1;
		gbc_lblVmax.gridy = 1;
		panel_4.add(lblVmax, gbc_lblVmax);
		
		JLabel lblDependencia = new JLabel("Dependencia");
		GridBagConstraints gbc_lblDependencia = new GridBagConstraints();
		gbc_lblDependencia.insets = new Insets(0, 0, 0, 5);
		gbc_lblDependencia.gridx = 2;
		gbc_lblDependencia.gridy = 1;
		panel_4.add(lblDependencia, gbc_lblDependencia);
		
		JLabel lblHoraLlegada = new JLabel("Hora llegada");
		GridBagConstraints gbc_lblHoraLlegada = new GridBagConstraints();
		gbc_lblHoraLlegada.insets = new Insets(0, 0, 0, 5);
		gbc_lblHoraLlegada.gridx = 3;
		gbc_lblHoraLlegada.gridy = 1;
		panel_4.add(lblHoraLlegada, gbc_lblHoraLlegada);
		
		JLabel lblTParada = new JLabel("T. Parada");
		GridBagConstraints gbc_lblTParada = new GridBagConstraints();
		gbc_lblTParada.insets = new Insets(0, 0, 0, 5);
		gbc_lblTParada.gridx = 4;
		gbc_lblTParada.gridy = 1;
		panel_4.add(lblTParada, gbc_lblTParada);
		
		JLabel lblHoraSalida = new JLabel("Hora salida");
		GridBagConstraints gbc_lblHoraSalida = new GridBagConstraints();
		gbc_lblHoraSalida.insets = new Insets(0, 0, 0, 5);
		gbc_lblHoraSalida.gridx = 5;
		gbc_lblHoraSalida.gridy = 1;
		panel_4.add(lblHoraSalida, gbc_lblHoraSalida);
		
		JLabel lblTiempoConc = new JLabel("Tiempo conc.");
		GridBagConstraints gbc_lblTiempoConc = new GridBagConstraints();
		gbc_lblTiempoConc.gridx = 6;
		gbc_lblTiempoConc.gridy = 1;
		panel_4.add(lblTiempoConc, gbc_lblTiempoConc);
		
		pack();
		setVisible(true);
	}
}
