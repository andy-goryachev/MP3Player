// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.TextTools;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxTabPane;
import goryachev.fx.FxWindow;
import goryachev.fx.HPane;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.MusicDB;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * Content Manager Window.
 */
public class ContentManagerWindow extends FxWindow
{
	protected final MusicDB db;
	protected final TextField queryField;
	protected final SearchPane searchPane;
	protected final AlbumPane albumPane;
	protected final FileSystemPane fileSystemPane;
	protected final FxTabPane tabPane;
	private static ContentManagerWindow instance;
	
	
	public ContentManagerWindow(MusicDB db)
	{
		super("ContentManagerWindow");
		this.db = db;
		
		setTitle("MP3 Player - Content Manager");
		setMinSize(500, 500);
		setSize(1000, 600);
		
		queryField = new TextField();
		queryField.setPrefColumnCount(30);
		queryField.setOnAction((ev) -> {
			doSearch();	
		});
		// TODO clear search button

		searchPane = new SearchPane(db);
		
		albumPane = new AlbumPane(db);
		
		fileSystemPane = new FileSystemPane();
		
		tabPane = new FxTabPane();
		tabPane.addTab("Search", searchPane);
		tabPane.addTab("Album", albumPane);
		// TODO later
		tabPane.addTab("Files", fileSystemPane);
		tabPane.setSide(Side.LEFT);
		tabPane.getSelectionModel().selectedIndexProperty().addListener((s,p,c) ->
		{
			if((c != null) && (c.intValue() == 2))
			{
				fileSystemPane.init();
			}
		});
		
		FxMenuBar m = new FxMenuBar();
		// app
		m.menu("Application");
		m.item("Preferences");
		m.separator();
		m.item("Quit");
		// file
		m.menu("File");
		m.item("Re-scan File System");
		// help
		m.menu("Help");
		m.item("About");
		
		HPane tb = new HPane();
		tb.setPadding(new Insets(2, 10, 2, 0));
		tb.add(m);
		tb.fill();
		tb.add(new Label("Find: "));
		tb.add(queryField);
		
		setTop(tb);
		setCenter(tabPane);
		setOnHidden((ev) ->
		{
			instance = null;
		});
		
		addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
	}
	
	
	protected void handleKeyPress(KeyEvent ev)
	{
		if(ev.getCode() == KeyCode.ESCAPE)
		{
			close();
			ev.consume();
		}
	}
	

	protected void doSearch()
	{
		String text = queryField.getText();
		CList<String> ss = TextTools.splitWhitespace(text);
		if(ss.size() > 0)
		{
			List<SearchEntry> result = db.search(ss);
			tabPane.selectNode(searchPane);
			searchPane.setResult(result);
		}
	}
	
	
	public boolean isEssentialWindow()
	{
		return false;
	}
	

	public static void openAlbum(Track t)
	{
		if(instance == null)
		{
			instance = new ContentManagerWindow(t.getDB());
			instance.tabPane.selectTab(1);
			instance.albumPane.setTrack(t);
			instance.open();
		}
		else
		{
			instance.tabPane.selectTab(1);
			instance.albumPane.setTrack(t);
			instance.requestFocus();
		}
	}
}
