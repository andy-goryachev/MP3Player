// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
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
		view.fitWidthProperty().bind(widthProperty());
		view.fitHeightProperty().bind(heightProperty());
		
		getChildren().add(view);
	}
	
	
	public void setImage(Image im)
	{
		view.setImage(im);
	}
}
