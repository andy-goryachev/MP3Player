// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.FxTabPane;
import goryachev.fx.FxWindow;
import goryachev.mp3player.Track;
import javafx.geometry.Side;


/**
 * Content Manager Window.
 */
public class ContentManagerWindow extends FxWindow
{
	protected final SearchPane searchPane;
	protected final AlbumPane albumPane;
	protected final FileSystemPane fileSystemPane;
	protected final FxTabPane tabPane;
	static ContentManagerWindow instance;
	
	
	public ContentManagerWindow()
	{
		super("ContentManagerWindow");
		setTitle("MP3 Player - Content Manager");
		setMinSize(500, 500);
		setSize(1000, 600);
		
		searchPane = new SearchPane();
		
		albumPane = new AlbumPane();
		
		fileSystemPane = new FileSystemPane();
		
		tabPane = new FxTabPane();
		tabPane.addTab("Search", searchPane);
		tabPane.addTab("Album", albumPane);
		tabPane.addTab("Files", fileSystemPane);
		tabPane.setSide(Side.LEFT);
		tabPane.getSelectionModel().selectedIndexProperty().addListener((s,p,c) ->
		{
			if((c != null) && (c.intValue() == 2))
			{
				fileSystemPane.init();
			}
		});
		
		setCenter(tabPane);
		setOnHidden((ev) ->
		{
			instance = null;
		});
	}
	

	public static void openAlbum(Track t)
	{
		ContentManagerWindow w = instance();
		w.tabPane.selectTab(1);
		w.albumPane.setTrack(t);
		w.open();
	}


	public boolean isEssentialWindow()
	{
		return false;
	}
	
	
	protected static ContentManagerWindow instance()
	{
		if(instance == null)
		{
			instance = new ContentManagerWindow();
		}
		return instance;
	}
}
