// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.fx.FxIconBuilder;
import goryachev.fx.IconBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;


/**
 * Icons.
 */
public class Icons
{
	public static IconBase pauseIcon(double size)
	{
		double sz2 = size / 2;
		double w = 1;
		double x0 = sz2 * 0.5;
		double x1 = sz2 * 0.25;
		double y0 = sz2 * 0.5;
		double y1 = -sz2 * 0.5;
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.YELLOW);
		b.setStrokeWidth(w);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(Color.DARKGRAY);
		
		b.newPath();
		
		b.moveTo(x0, y0);
		b.lineTo(x0, y1);
		b.lineTo(x1, y1);
		b.lineTo(x1, y0);
		b.closePath();
		
		b.moveTo(-x0, y0);
		b.lineTo(-x0, y1);
		b.lineTo(-x1, y1);
		b.lineTo(-x1, y0);
		b.closePath();
		
		return b.getIcon();
	}
}
