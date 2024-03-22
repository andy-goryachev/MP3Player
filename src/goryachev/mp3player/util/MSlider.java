// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxDouble;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;


/**
 * Slider Control for MP3 Player.
 */
public class MSlider extends Region
{
	public static final CssStyle TRACK = new CssStyle("MSlider_TRACK");
	public static final CssStyle THUMB = new CssStyle("MSlider_THUMB");
	private final FxDouble value = new FxDouble();
	private final Region thumb;
	private static final double ASPECT = 0.25;
	
	
	public MSlider()
	{
		FX.style(this, TRACK);
		
		thumb = new Region();
		thumb.setMouseTransparent(true);
		FX.style(thumb, THUMB);
		getChildren().add(thumb);

		FX.addInvalidationListener(value, true, this::positionThumb);
	}
	
	
	public double getValueForX(double x)
	{
		double tw = thumb.getWidth();
		double w = getWidth() - tw;
		if(w <= 0.0)
		{
			return 0.0;
		}
		
		double v = (x - (tw / 2.0)) / w;
		if(v < 0.0)
		{
			return 0.0;
		}
		else if(v > 1.0)
		{
			return 1.0;
		}
		return v;
	}
	
	
	protected double thumbX()
	{
		double w = getHeight() * ASPECT;
		double x = (getWidth() - w) * getValue();
		return x;
	}


	protected void positionThumb()
	{
		double x = thumbX();
		thumb.setLayoutX(x);
	}


	protected void layoutChildren()
	{
		double x = thumbX();
		double w = getHeight() * ASPECT;
		double h = getHeight();
		layoutInArea(thumb, x, 0, w, h, 0.0, HPos.CENTER, VPos.CENTER);
	}
	
	
	public final double getValue()
	{
		return value.get();
	}
	
	
	public final void setValue(double x)
	{
//		D.trace();
//		D.print(x);
		value.set(x);
	}
	
	
	public final DoubleProperty valueProperty()
	{
		return value;
	}
}
