package scrt.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import scrt.regulation.Place;

public class StationAxis extends SymbolAxis
{
	List<Place> places = new ArrayList<Place>();
	public StationAxis(String label, List<Place> places)
	{
		super(label, new String[]{});
		for(Place p : places)
		{
			if(p.isPP) this.places.add(p);
		}
	}
	@Override
    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) 
	{
        List ticks = new java.util.ArrayList();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        int count = places.size();
        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) 
        {
        	boolean second = false;
            for (int i = 0; i < count; i++) 
            {
                double currentTickValue = places.get(i).getPK();
                if(places.get(i).secondPK != 0)
                {
                	if(!second) second = true;
                	else
                	{
                		currentTickValue = places.get(i).secondPK;
                		second = false;
                	}
                }
                String tickLabel;
                tickLabel = places.get(i).toString();
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) 
                {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT)  angle = -Math.PI / 2.0;
                    else angle = Math.PI / 2.0;
                }
                else 
                {
                    if (edge == RectangleEdge.LEFT) 
                    {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else 
                    {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }
                Tick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
                ticks.add(tick);
                if(second) i--;
            }
        }
        return ticks;
    }
}
