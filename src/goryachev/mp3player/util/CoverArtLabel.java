// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.fx.CssStyle;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


/**
 * Cover Art Label.
 */
public class CoverArtLabel extends Pane
{
	public final static int SIZE = 105;
	public static final CssStyle ART = new CssStyle("CoverArtLabel_ART");
	private final ImageView view;
	private static Image blank;
	
	
	public CoverArtLabel()
	{
		view = new ImageView();
		view.setSmooth(true);
		view.setPreserveRatio(true);
		view.setFitHeight(SIZE);
		view.setFitWidth(SIZE);
		setPrefWidth(SIZE);
		setPrefHeight(SIZE);
		setPadding(Insets.EMPTY);
		ART.set(view);
		
		getChildren().add(view);
	}
	
	
	public void setImage(Image im)
	{
		if(im == null)
		{
			im = blank();
		}
		view.setImage(im);
	}
	
	
	protected Image blank()
	{
		if(blank == null)
		{
			int w = SIZE;
			int h = SIZE;
			Canvas c = new Canvas(w, h);
			GraphicsContext g = c.getGraphicsContext2D();
			g.setStroke(Color.GRAY);
			g.setLineWidth(1);
			g.strokeRect(0.5, 0.5, w - 1.0, h - 1.0);
			WritableImage im = new WritableImage(w, h);
			c.snapshot(null, im);
			blank = im;
		}
		return blank;
	}
}
