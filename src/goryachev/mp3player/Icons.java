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
	private static final double size = 25;
	private static final double smSize = 20;

	
	// FIX pause/play
	public static IconBase play()
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
	
	
	public static IconBase jump()
	{
		double sz2 = size / 2;
		double stroke = 1;
		double p0 = size * 0.1;
		double p1 = size * 0.45;
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.gray(0.6));
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(Color.gray(0.3));
		b.setRotateDegrees(45);

		b.newPath();		
		b.moveTo(p0, p1);
		b.lineTo(p0, p0);
		b.lineTo(p1, p0);
		b.lineTo(p1, -p0);
		b.lineTo(p0, -p0);
		b.lineTo(p0, -p1);
		b.lineTo(-p0, -p1);
		b.lineTo(-p0, -p0);
		b.lineTo(-p1, -p0);
		b.lineTo(-p1, p0);
		b.lineTo(-p0, p0);
		b.lineTo(-p0, p1);
		b.closePath();
		
		return b.getIcon();
	}
	
	
	public static IconBase prevAlbum()
	{
		return album(true);
	}
	
	
	public static IconBase nextAlbum()
	{
		return album(false);
	}
	
	
	public static IconBase prevTrack()
	{
		return track(true);
	}
	
	
	public static IconBase nextTrack()
	{
		return track(false);
	}
	
	
	public static IconBase contentManager()
	{
		// TODO
		return new IconBase(16);
	}
	
	
	private static IconBase track(boolean flip)
	{
		double sz2 = size / 2;
		double scale = 1.25;
		double stroke = 1.0 / scale;		
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.gray(0.6));
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(Color.gray(0.3));
		b.setTranslate(-3.5, 0);
		b.setScale(scale);
		b.setRotateDegrees(flip ? 180 : 0);
		
		b.newPath();
		b.moveTo(0, 3);
		b.lineTo(5, 0);
		b.lineTo(0, -3);
		b.closePath();
		
		b.moveTo(7, 3);
		b.lineTo(7, -3);
		
		return b.getIcon();
	}

	
	private static IconBase album(boolean flip)
	{
		double sz2 = size / 2;
		double scale = 1.25;
		double stroke = 1.0 / scale;		
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.gray(0.6));
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(Color.gray(0.3));
		b.setTranslate(-6.5, 0);
		b.setScale(scale);
		b.setRotateDegrees(flip ? 180 : 0);
		
		b.newPath();
		b.moveTo(0, 3);
		b.lineTo(5, 0);
		b.lineTo(0, -3);
		b.closePath();
		
		b.moveTo(7, 3);
		b.lineTo(12, 0);
		b.lineTo(7, -3);
		b.closePath();
		
		b.moveTo(14, 3);
		b.lineTo(14, -3);
		
		return b.getIcon();
	}
}
