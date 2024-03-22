// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.IDisconnectable;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxDisconnector;
import goryachev.fx.FxMenu;
import goryachev.fx.FxPopupMenu;
import goryachev.fx.FxSplitMenuButton;
import goryachev.mp3player.MainWindow;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.ICharsetDetector;
import goryachev.mp3player.db.ID3_Info;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.db.RussianDetector;
import goryachev.mp3player.util.CoverArtLabel;
import java.awt.Desktop;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;


/**
 * Album Pane.
 */
public class AlbumPane extends CPane
{
	private static final Log log = Log.get("AlbumPane");
	public static final CssStyle CURRENT_TRACK = new CssStyle("AlbumPane_CURRENT_TRACK");
	protected final MusicDB db;
	protected final CoverArtLabel artField;
	protected final TextField titleField;
	protected final TextField albumField;
	protected final TextField artistField;
	protected final TextField yearField;
	protected final TableView<Track> table;
	protected final TextField pathField;
	protected final FxAction copyInfoToAllTracksAction = new FxAction(this::copyInfoToAllTracks);
	private final FxDisconnector disconnector = new FxDisconnector();
	private IDisconnectable titleDisconnectable;
	private Track currentTrack;
	
	
	public AlbumPane(MusicDB db)
	{
		this.db = db;
		
		setHGap(5);
		setVGap(3);
		setPadding(10);
		setBackground(Background.fill(Color.gray(0, 0.05)));
		
		artField = new CoverArtLabel();
		
		titleField = new TextField();
		
		albumField = new TextField();
		createPopupMenu(albumField, (t,s) ->
		{
			t.setAlbum(s);
		});
		
		artistField = new TextField();
		createPopupMenu(artistField, (t,s) ->
		{
			t.setArtist(s);
		});
		
		yearField = new TextField();
		createPopupMenu(yearField, (t,s) ->
		{
			t.setYear(s);
		});
		
		pathField = new TextField();
		pathField.setEditable(false);
		pathField.setStyle("-fx-background-color:transparent; -fx-background-insets:0; -fx-background-radius:0; -fx-effect:none; -fx-padding:2 0 0 0; -fx-text-fill:#444444;");
		
		FxSplitMenuButton editButton = new FxSplitMenuButton("Edit...");
		editButton.item("Open Directory", this::openDirectory);
		editButton.item("Add Cover Art"); // TODO
		editButton.separator();
		editButton.item("Autocorrect: Russian", () -> updateEncoding(() -> new RussianDetector()));
		editButton.item("Autocorrect: Japanese", () -> updateEncoding("x-JISAutoDetect"));
		FxMenu m = editButton.menu("Encoding");
		m.separator();
		m.item("UTF-8", () -> updateEncoding("UTF-8"));
		m.item("ISO-8859-1", () -> updateEncoding("ISO-8859-1"));
		m.separator();
		// cyrillic
		m.item("Cp1251 (Cyrillic)", () -> updateEncoding("Cp1251"));
		m.item("KOI8-R (Cyrillic)", () -> updateEncoding("KOI8-R"));
		m.separator();
		// cn
		m.item("Big5 (Trad. Chinese)", () -> updateEncoding("Big5"));
		m.item("GB2312 (Simp. Chinese)", () -> updateEncoding("GB2312"));
		m.separator();
		// other
		m.item("Other...", this::openCharsetDialog);

		table = new TableView<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<Track,Integer> c = new TableColumn<>("No");
			table.getColumns().add(c);
			c.setMinWidth(30);
			c.setMaxWidth(30);
			c.setSortable(false);
			c.setCellFactory(this::cellFactory);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().trackNumberProperty();
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Title");
			table.getColumns().add(c);
			c.setPrefWidth(300);
			c.setSortable(false);
			c.setCellFactory(this::cellFactory);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().titleProperty();
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Album");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setSortable(false);
			c.setCellFactory(this::cellFactory);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().albumProperty();
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Artist");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setSortable(false);
			c.setCellFactory(this::cellFactory);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().artistProperty();
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Year");
			table.getColumns().add(c);
			c.setPrefWidth(70);
			c.setSortable(false);
			c.setCellFactory(this::cellFactory);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().yearProperty();
			});
		}
		
		addColumns
		(
			CoverArtLabel.SIZE,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		addRows
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		int r = 0;
		add(0, r, 1, 5, artField);
		add(1, r, FX.label("Title:", Pos.CENTER_RIGHT));
		add(2, r, 2, 1, titleField);
		r++;
		add(1, r, FX.label("Album:", Pos.CENTER_RIGHT));
		add(2, r, 2, 1, albumField);
		r++;
		add(1, r, FX.label("Artist:", Pos.CENTER_RIGHT));
		add(2, r, 2, 1, artistField);
		r++;
		add(1, r, FX.label("Year:", Pos.CENTER_RIGHT));
		add(2, r, yearField);
		add(3, r, editButton);
		r++;
		r++;
		add(0, r, 4, 1, table);
		r++;
		add(0, r, 4, 1, pathField);

		FX.addInvalidationListener(table.getSelectionModel().getSelectedItems(), true, this::handleSelection);
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
	}
	
	
	protected TableCell cellFactory(TableColumn<?,?> column)
	{
		return new TableCell()
		{
			private BooleanBinding showPlaying;
			
			
			{
				showPlaying = Bindings.createBooleanBinding
				(
					() ->
					{
						int ix = getIndex();
						if(ix >= 0)
						{
							List<Track> ts = (List<Track>)column.getTableView().getItems();
							if(ix < ts.size())
							{
								Track t = ts.get(ix);
								return t.equals(Track.getCurrentlyPlayingTrack());
							}
						}
						return false;
					},
					indexProperty(),
					Track.currentlyPlayingTrack()
				);
				
				FX.addChangeListener(showPlaying, true, () ->
				{
					boolean on = showPlaying.get();
					FX.style(this, on, CURRENT_TRACK);
				});
			}
			
			
			@Override
			protected void updateItem(Object item, boolean empty)
			{
				super.updateItem(item, empty);
				
				if(empty || item == null)
				{
					setText(null);
				}
				else
				{
					setText(item.toString());
				}
				setGraphic(null);
			}
		};
	}
	
	
	protected void handleSelection()
	{
		List<Track> ts = table.getSelectionModel().getSelectedItems();
		int sz = ts.size();
		switch(sz)
		{
		case 0:
			if(titleDisconnectable != null)
			{
				titleDisconnectable.disconnect();
			}
			titleField.setText(null);
			break;
		case 1:
			Track t = ts.get(0);
			updateTrackInfo(t);
			break;
		default:
		}
		
		titleField.setDisable(sz != 1);
	}

	
	public void updateTrackInfo(Track t)
	{
		//if(currentTrack != t)
		{
			disconnector.disconnect();
			
			currentTrack = t;
			
			artField.setImage(t.getCoverArt());
			titleDisconnectable = disconnector.bindBidirectional(titleField.textProperty(), t.titleProperty());
			disconnector.bindBidirectional(albumField.textProperty(), t.albumProperty());
			disconnector.bindBidirectional(artistField.textProperty(), t.artistProperty());
			disconnector.bindBidirectional(yearField.textProperty(), t.yearProperty());
			pathField.setText(t.getFile().toString());
		}
	}
	

	public void setTrack(Track t)
	{
		updateTrackInfo(t);
		
		// TODO move to Track?
		int sz = t.getAlbumTrackCount();
		CList<Track> ts = new CList<>(sz);
		for(int i=0; i<sz; i++)
		{
			ts.add(t.getTrackAt(i));
		}

		table.getItems().setAll(ts);
		table.getSelectionModel().clearSelection();
		table.getSelectionModel().select(t);
	}


	protected void copyInfoToAllTracks()
	{
		String album = albumField.getText();
		String artist = artistField.getText();
		String year = yearField.getText();

		List<Track> ts = table.getItems();
		for(Track t: ts)
		{
			t.setAlbum(album);
			t.setArtist(artist);
			t.setYear(year);
		}
	}
	
	
	protected void createPopupMenu(TextField t, BiConsumer<Track,String> action)
	{
		boolean sel = (t.getSelection().getLength() > 0);
		boolean clip = Clipboard.getSystemClipboard().hasString();
		
		FxAction undo = new FxAction(t::undo);
		undo.setEnabled(t.isUndoable());
		
		FxAction redo = new FxAction(t::undo);
		redo.setEnabled(t.isRedoable());
		
		FxAction cut = new FxAction(t::cut);
		cut.setEnabled(sel);
		
		FxAction copy = new FxAction(t::cut);
		copy.setEnabled(sel);
		
		FxAction paste = new FxAction(t::paste);
		paste.setEnabled(clip);
		
		FxAction copyToAll = new FxAction(() -> {
			String text = t.getText();
			List<Track> ts = table.getItems();
			for(Track track: ts)
			{
				action.accept(track, text);
			}
		});
		
		String dest;
		if(t == albumField)
		{
			dest = "Album";
		}
		else if(t == artistField)
		{
			dest = "Artist";
		}
		else if(t == yearField)
		{
			dest = "Year";
		}
		else
		{
			dest = "??";
		}
		String msg = String.format("Copy %s to All Tracks", dest);
		
		FX.setPopupMenu(t, () ->
		{
			FxPopupMenu m = new FxPopupMenu();
			m.item(msg, copyToAll);
			m.item("Copy Album, Artist, Year to All Tracks", copyInfoToAllTracksAction);
			m.separator();
			m.item("Undo", undo);
			m.item("Redo", redo);
			m.item("Cut", cut);
			m.item("Copy", copy);
			m.item("Paste", paste);
			m.separator();
			m.item("Select All", t::selectAll);
			return m;
		});
	}
	
	
	protected void updateEncoding(String enc)
	{
		Charset cs = Charset.forName(enc);
		
		updateEncoding(() -> new ICharsetDetector()
		{
			public Charset guessCharset()
			{
				return cs;
			}
			
			
			public void update(byte[] bytes, int off, int len)
			{
			}
		});
	}
	
	
	protected void updateEncoding(Supplier<ICharsetDetector> gen)
	{
		List<Track> ts = table.getItems();
		for(Track t: ts)
		{
			try
			{
				File f = t.getFile();
				ID3_Info d = ID3_Info.parseID3(f, gen);
				if(d != null)
				{
					set(d.getTitle(), t::setTitle);
					set(d.getAlbum(), t::setAlbum);
					set(d.getArtist(), t::setArtist);
					set(d.getYear(), t::setYear);
				}
			}
			catch(Exception e)
			{
				log.error(e);
			}
		}
		
		handleSelection();
	}
	
	
	protected static void set(String text, Consumer<String> c)
	{
		if(CKit.isNotBlank(text))
		{
			c.accept(text);
		}
	}
	
	
	protected void openDirectory()
	{
		File f = currentTrack.getFile().getParentFile();
		try
		{
			Desktop.getDesktop().open(f);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}
	
	
	protected void openCharsetDialog()
	{
		new CharsetDialog(this).open();
	}
	
	
	protected void handleClick(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			Track t = table.getSelectionModel().getSelectedItem();
			if(t != null)
			{
				MainWindow.playTrack(t);
			}
		}
	}


	public File getCurrentDir()
	{
		if(currentTrack != null)
		{
			return currentTrack.getFile().getParentFile();
		}
		return null;
	}
}
