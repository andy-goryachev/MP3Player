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
	private static final Color GRAY_FILL = Color.gray(0.6);
	private static final Color GRAY_OUTLINE = Color.gray(0.3);

	
	public static IconBase play(boolean play, boolean blink)
	{
		double sz2 = size / 2;
		double scale = 1.5;
		double stroke = 1.0 / scale;		
		
		FxIconBuilder b = new FxIconBuilder(size, sz2, sz2);
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(GRAY_OUTLINE);
		b.setTranslate(-15, 0);
		b.setScale(scale);
		
		b.setFill(play ? Color.LIGHTGREEN : GRAY_FILL);
		b.newPath();
		b.moveTo(0, 6);
		b.lineTo(10, 0);
		b.lineTo(0, -6);
		b.closePath();

		b.setFill(!play && blink ? Color.YELLOW : GRAY_FILL);
		b.newPath();
		b.moveTo(20, 6);
		b.lineTo(24, 6);
		b.lineTo(24, -6);
		b.lineTo(20, -6);
		b.closePath();
		
		b.moveTo(26, 6);
		b.lineTo(30, 6);
		b.lineTo(30, -6);
		b.lineTo(26, -6);
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
		b.setFill(GRAY_FILL);
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(GRAY_OUTLINE);
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
		b.setFill(GRAY_FILL);
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(GRAY_OUTLINE);
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
		b.setFill(GRAY_FILL);
		b.setStrokeWidth(stroke);
		b.setStrokeLineCap(StrokeLineCap.ROUND);
		b.setStrokeColor(GRAY_OUTLINE);
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
