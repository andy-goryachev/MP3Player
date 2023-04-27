// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


/**
 * Cover Art Label.
 */
public class CoverArtLabel extends Pane
{
	private final ImageView view;
	
	
	public CoverArtLabel()
	{
		view = new ImageView();
		view.setSmooth(true);
		view.setPreserveRatio(true);
		// TODO adjust computationally to clip so it uses 100% of the area
		view.fitWidthProperty().bind(widthProperty());
		view.fitHeightProperty().bind(heightProperty());
		
		getChildren().add(view);
	}
	
	
	public void setImage(Image im)
	{
		view.setImage(im);
	}
}
