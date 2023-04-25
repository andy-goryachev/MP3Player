// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.common.util.D;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxDouble;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
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
	private boolean pressed;
	private static final double ASPECT = 0.6;
	
	
	public MSlider()
	{
		FX.style(this, TRACK);
		
		thumb = new Region();
		thumb.setMouseTransparent(true);
		FX.style(thumb, THUMB);
		getChildren().add(thumb);

		FX.addInvalidationListener(value, true, this::positionThumb);
		addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
		addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
	}
	
	
	// FIX audio clicks on mouse press and hold??
	protected void handleMousePressed(MouseEvent ev)
	{
		if(pressed)
		{
			return;
		}
		
		pressed = true;
		
		double w = getHeight() * ASPECT;
		double v = (ev.getX()  - w/2) / (getWidth() - w);
		if(v < 0.0)
		{
			v = 0.0;
		}
		else if(v > 1.0)
		{
			v = 1.0;
		}
		setValue(v);
		ev.consume();
	}
	
	
	protected void handleMouseReleased(MouseEvent ev)
	{
		pressed = false;
		ev.consume();
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
		double aspect = 0.6;
		double x = thumbX();
		double w = getHeight() * aspect;
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
