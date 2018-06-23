/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scrt.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import scrt.Orientation;
import scrt.regulation.Place;
import scrt.regulation.timetable.Timetable;
import scrt.regulation.timetable.TimetableEntry;

public class TrafficGraph extends JFrame
{
	static long timeFrom(int h, int min)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), h, min, 0);
		return cal.getTime().getTime();
	}
	boolean showPlans = false;
	void itemToPoint(XYSeries series, TimetableEntry t)
	{
		if(t.item.isPP)
		{
			double pk = t.item.getPK();
			if(t.getNext()!=null && (t.getNext().item.getPK() < pk ^ t.timetable.direction == Orientation.Even)) pk = t.item.secondPK;
			if(t.getPrev()!=null && (t.getPrev().item.getPK() > pk ^ t.timetable.direction == Orientation.Even)) pk = t.item.secondPK;
			series.add(t.getEntry().getTime(), pk);
			if(t.timetable.entries.indexOf(t) != t.timetable.entries.size() - 1)
			{
				long time = new Date().getTime();
				if(t.getEntry().getTime() < time && t.getExit().getTime() > time) series.add(time, pk);
				series.add(t.getExit().getTime(), pk);
			}
		}
	}
	XYSeries construct(Timetable t)
	{
		XYSeries series = new XYSeries(t.number);
		for(TimetableEntry entry : t.entries)
		{
			itemToPoint(series, entry);
		}
		return series;
	}
	XYSeriesCollection dataset = new XYSeriesCollection();
	List<Timetable> timetables;
	public void updateData(List<Timetable> timetables)
	{
		this.timetables = timetables;
		dataset.removeAllSeries();
		for(Timetable t : timetables)
		{
			if(showPlans || t.valid) dataset.addSeries(construct(t));
		}
	}
	XYLineAndShapeRenderer rend = new XYLineAndShapeRenderer()
			{
				Stroke linear = new BasicStroke(2.0f);
				Stroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {6.0f, 6.0f}, 0.0f);
				@Override
				public Stroke getItemStroke(int row, int column)
				{
					if(dataset.getSeries(row).getDataItem(column).getX().longValue() > new Date().getTime()) return dashed;
					else return linear;
				}
			};
	public TrafficGraph(List<Place> p)
	{
		this.setLocation(650, 0);
		setLayout(new FlowLayout());
		setBackground(Color.black);
		ValueAxis xAxis = new DateAxis("Horas");
		ValueAxis yAxis = new StationAxis("Estación", p);
		rend.setBaseShapesVisible(false);
		rend.setBaseShapesFilled(false);
		rend.setAutoPopulateSeriesStroke(false);
		rend.setBaseToolTipGenerator(new XYToolTipGenerator()
				{
					@Override
					public String generateToolTip(XYDataset set, int series, int arg2)
					{
						return set.getSeriesKey(series).toString();
					}
				});
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, rend);
		double min = -1;
		double max = -1;
		for(Place place : p)
		{
			if(min == -1 || place.getPK() < min) min = place.getPK();
			if(max == -1 || place.getPK() > max) max = place.getPK();
			if(place.secondPK != 0 && place.secondPK < min) min = place.secondPK;
			if(place.secondPK != 0 && place.secondPK > max) max = place.secondPK;
		}
		yAxis.setRange(min - (max-min)*0.02, max + (max-min)*0.02);
		ValueMarker m = new ValueMarker(new Date().getTime(), Color.white, new BasicStroke(1.5f));
		m.setLabel(Integer.toString(new Date().getHours()) + ":" + new Date().getMinutes());
		plot.addDomainMarker(m);
		Timer t = new Timer(2000, new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						m.setValue(new Date().getTime());
						updateData(timetables);
					}
				});
		t.setRepeats(true);
		t.start();
		JFreeChart chart = new JFreeChart("Malla gráfica", plot);
		chart.getTitle().setPaint(Color.white);
		chart.setBackgroundPaint(Color.black);
		chart.getLegend().setBackgroundPaint(Color.black);
		m.setLabelPaint(Color.white);
		plot.setBackgroundPaint(Color.black);
		xAxis.setLabelPaint(Color.white);
		xAxis.setTickLabelPaint(Color.white);
		yAxis.setLabelPaint(Color.white);
		yAxis.setTickLabelPaint(Color.white);
		rend.setBaseItemLabelPaint(Color.black);
		rend.setBaseLegendTextPaint(Color.white);
		rend.setBaseItemLabelPaint(Color.black);
		ChartPanel panel = new ChartPanel(chart);
		panel.createToolTip();
		panel.setPopupMenu(null);
		add(panel);
		JCheckBox prevision = new JCheckBox("Mostrar trenes planificados");
		prevision.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						showPlans = prevision.isSelected();
						updateData(timetables);
					}
				});
		add(prevision);
		this.setTitle("MALLAS");
		pack();
		setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
