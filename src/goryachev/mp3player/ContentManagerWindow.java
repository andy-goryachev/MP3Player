// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.fx.CPane;
import goryachev.fx.FxTabPane;
import goryachev.fx.FxWindow;
import javafx.geometry.Side;


/**
 * Content Manager Window.
 */
public class ContentManagerWindow extends FxWindow
{
	protected final AlbumPane albumPane;
	protected final FxTabPane tabPane;
	
	
	public ContentManagerWindow()
	{
		super("ContentManagerWindow");
		setTitle("MP3 Player - Content Manager");
		setMinSize(500, 500);
		setSize(1000, 600);
		
		albumPane = new AlbumPane();
		
		tabPane = new FxTabPane();
		tabPane.addTab("Search", new CPane());
		tabPane.addTab("Album", albumPane);
		tabPane.addTab("Files", new CPane());
		tabPane.setSide(Side.LEFT);
		
		setCenter(tabPane);
	}
	

	public static void openAlbum(Track t)
	{
		ContentManagerWindow w = new ContentManagerWindow();
		w.tabPane.selectTab(1);
		w.albumPane.setTrack(t);
		w.open();
	}


	public boolean isEssentialWindow()
	{
		return false;
	}
}
