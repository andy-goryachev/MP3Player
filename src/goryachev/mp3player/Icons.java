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
		double p1 = size * 0.5;
		
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
		return album(180);
	}
	
	
	public static IconBase nextAlbum()
	{
		return album(0);
	}
	
	
	public static IconBase prevTrack()
	{
		return track(180);
	}
	
	
	public static IconBase nextTrack()
	{
		return track(0);
	}
	
	
	public static IconBase contentManager()
	{
		// TODO
		return new IconBase(16);
	}
	
	
	private static IconBase track(double angle)
	{
		double sz2 = size / 2;
		double stroke = 1;
		double p0 = size * 0.1;
		double p1 = size * 0.5;
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.gray(0.6));
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(Color.gray(0.3));
		b.setRotateDegrees(45);

		b.newPath();		
		b.closePath();
		
		return b.getIcon();
	}

	
	private static IconBase album(double angle)
	{
		double sz2 = size / 2;
		double stroke = 0.5;
		double p0 = size * 0.1;
		double p1 = size * 0.5;
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setFill(Color.gray(0.4));
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.SQUARE);
		b.setStrokeColor(null); //Color.gray(0.3));
		b.setRotateDegrees(0);
		//b.setScale(0.8);
		
		b.svgPath("M4.65079 5.24076C4.35428 4.9866 3.93694 4.92832 3.58214 5.0915C3.22734 5.25469 3 5.60948 3 6.00001V18C3 18.3905 3.22734 18.7453 3.58214 18.9085C3.93694 19.0717 4.35428 19.0134 4.65079 18.7593L11.6508 12.7593C11.8724 12.5693 12 12.2919 12 12C12 11.7081 11.8724 11.4307 11.6508 11.2408L4.65079 5.24076ZM9.46341 12L5 15.8258V8.17423L9.46341 12ZM14.6508 5.24076C14.3543 4.9866 13.9369 4.92832 13.5821 5.0915C13.2273 5.25469 13 5.60948 13 6.00001V18C13 18.3905 13.2273 18.7453 13.5821 18.9085C13.9369 19.0717 14.3543 19.0134 14.6508 18.7593L21.6508 12.7593C21.8724 12.5693 22 12.2919 22 12C22 11.7081 21.8724 11.4307 21.6508 11.2408L14.6508 5.24076ZM19.4634 12L15 15.8258V8.17423L19.4634 12Z");

//		b.newPath();		
//		b.closePath();
		
		return b.getIcon();
	}
}
