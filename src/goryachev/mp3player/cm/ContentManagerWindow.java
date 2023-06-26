// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.GlobalSettings;
import goryachev.common.util.TextTools;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxMenuBar;
import goryachev.fx.FxWindow;
import goryachev.fx.GlobalBooleanProperty;
import goryachev.fx.HPane;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.MusicDB;
import java.io.File;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
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
	protected final FileTreePane fileTreePane;
	protected final CPane mainPane;
	private static ContentManagerWindow instance;
	// FIX GlobalSettings.getBooleanProperty(key, initialValue)
	private static final GlobalBooleanProperty showFileSystemProperty = new GlobalBooleanProperty("showFileSystem");
	
	
	public ContentManagerWindow(MusicDB db)
	{
		super("ContentManagerWindow");
		this.db = db;
		
		setTitle("MP3 Player - Content Manager");
		setMinSize(500, 500);
		setSize(1000, 600);
		
		queryField = new TextField();
		queryField.setPrefColumnCount(30);
		queryField.setOnAction((ev) ->
		{
			doSearch();	
		});
		// TODO clear search button

		searchPane = new SearchPane(db);
		
		albumPane = new AlbumPane(db);
		
		fileTreePane = new FileTreePane();
		fileTreePane.visibleProperty().bind(showFileSystemProperty);
		fileTreePane.visibleProperty().addListener((p) ->
		{
			updateFileSystemPane();
		});
		fileTreePane.tree.getSelectionModel().selectedItemProperty().addListener((s,p,c) ->
		{
			handleFileTreeSelection(c == null ? null : c.getValue());
		});
		
		mainPane = new CPane();
		mainPane.setCenter(albumPane);
		
		FxMenuBar m = new FxMenuBar();
		// app
		m.menu("Application");
		m.item("Preferences");
		m.separator();
		m.item("Quit");
		// file
		m.menu("File");
		m.item("Re-scan File System");
		// view
		m.menu("View");
		m.checkItem("Show File System", showFileSystemProperty);
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
		setOnHidden((ev) ->
		{
			instance = null;
		});
		
		addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
		updateFileSystemPane();
	}


	protected void handleFileTreeSelection(File dir)
	{
		Track t = db.findFirstTrack(dir);
		if(t == null)
		{
			mainPane.setCenter(new CPane());
		}
		else
		{
			albumPane.setTrack(t);
			mainPane.setCenter(albumPane);
		}
	}


	protected void updateFileSystemPane()
	{
		FX.storeSettings(this);
		
		if(fileTreePane.isVisible())
		{
			fileTreePane.init();
			SplitPane sp = new SplitPane(fileTreePane, mainPane);
			setCenter(sp);
			FX.restoreSettings(this);
		}
		else
		{
			setCenter(mainPane);
			FX.restoreSettings(this);
		}
	}


	protected void handleKeyPress(KeyEvent ev)
	{
		if(ev.getCode() == KeyCode.ESCAPE)
		{
			//close();
			mainPane.setCenter(albumPane);
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
			mainPane.setCenter(searchPane);
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
//			instance.tabPane.selectTab(1);
			instance.albumPane.setTrack(t);
			instance.open();
		}
		else
		{
//			instance.tabPane.selectTab(1);
			instance.albumPane.setTrack(t);
			instance.requestFocus();
		}
	}
}
