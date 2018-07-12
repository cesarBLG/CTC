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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import scrt.Orientation;
import scrt.ctc.Signal.Aspect;

public class IconDatabase
{
	public static class AspectDiscriminator
	{
		boolean fixedSignal;
		Aspect aspect;
		Orientation direction;
		boolean exitIndicator;
		public AspectDiscriminator(Aspect a, Orientation dir, boolean fixed, boolean ind)
		{
			aspect = a;
			direction = dir;
			fixedSignal = fixed;
			exitIndicator = ind;
		}
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof AspectDiscriminator)
			{
				AspectDiscriminator a = (AspectDiscriminator) obj;
				return fixedSignal == a.fixedSignal && aspect == a.aspect && direction == a.direction && exitIndicator == a.exitIndicator;
			}
			return super.equals(obj);
		}
	}
	static Hashtable<AspectDiscriminator, Icon> aspects = new Hashtable<>();
	public static Icon getAspect(AspectDiscriminator a)
	{
		if(!aspects.containsKey(a))
		{
			Icon ic = new ImageIcon(IconDatabase.class.getResource("/scrt/Images/Signals".concat(a.fixedSignal ? "/Fixed/" : (a.exitIndicator ? "/IS/" : "/")).concat(a.aspect.name().concat(".png"))));
			if(a.direction == Orientation.Odd)
			{
				ic = getRotated(ic);
			}
			aspects.put(a, ic);
		}
		return aspects.get(a);
	}
	public static Icon getRotated(Icon ic)
	{
		Icon i = new Icon()
		{
			@Override
			public int getIconHeight()
			{
				return ic.getIconHeight();
			}
			@Override
			public int getIconWidth()
			{
				return ic.getIconWidth();
			}
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y)
			{
				Graphics2D g2 = (Graphics2D)g.create();
				int cWidth = ic.getIconWidth() / 2;
				int cHeight = ic.getIconHeight() / 2;
				int xAdjustment = (ic.getIconWidth() % 2) == 0 ? 0 : -1;
				int yAdjustment = (ic.getIconHeight() % 2) == 0 ? 0 : -1;
				g2.translate(x + cWidth, y + cHeight);
				g2.rotate( Math.toRadians( 180 ) );
				ic.paintIcon(c, g2, xAdjustment - cWidth, yAdjustment - cHeight);
			}
		};
		return i;
	}
}
