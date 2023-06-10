// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


/**
 * Cover Art Label.
 */
public class CoverArtLabel extends Pane
{
	public final static double SIZE = 105;
	private final ImageView view;
	
	
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
		
		getChildren().add(view);
	}
	
	
	public void setImage(Image im)
	{
		view.setImage(im);
	}
}
