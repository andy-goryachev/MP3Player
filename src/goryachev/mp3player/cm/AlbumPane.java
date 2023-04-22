// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxButton;
import goryachev.fx.FxMenu;
import goryachev.fx.FxSplitMenuButton;
import goryachev.mp3player.CoverArtLabel;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.ICharsetDetector;
import goryachev.mp3player.db.ID3_Info;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.db.RussianDetector;
import java.awt.Desktop;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


/**
 * Album Pane.
 */
public class AlbumPane extends CPane
{
	protected static final Log log = Log.get("AlbumPane");
	protected final MusicDB db;
	protected final CoverArtLabel artField;
	protected final TextField titleField;
	protected final TextField albumField;
	protected final TextField artistField;
	protected final TextField yearField;
	protected final TableView<Track> table;
	protected final FxAction updateAction = new FxAction(this::update);
	protected final FxAction updateAlbumAction = new FxAction(this::updateAlbum);
	private Track currentTrack;
	
	
	public AlbumPane(MusicDB db)
	{
		this.db = db;
		
		setHGap(5);
		setVGap(3);
		setPadding(10);
		
		artField = new CoverArtLabel();
		
		titleField = new TextField();
		
		albumField = new TextField();
		
		artistField = new TextField();
		
		yearField = new TextField();
		
		FxSplitMenuButton moreButton = new FxSplitMenuButton("More...");
		moreButton.item("Open Directory", this::openDirectory);
		FxMenu m = moreButton.menu("Encoding");
		m.item("UTF-8", () -> updateEncoding("UTF-8"));
		m.item("ISO-8859-1", () -> updateEncoding("ISO-8859-1"));
		m.separator();
		// cyrillic
		m.item("Autocorrect: Russian", () -> updateEncoding(() -> new RussianDetector()));
		m.item("Cp1251 (Cyrillic)", () -> updateEncoding("Cp1251"));
		m.item("KOI8-R (Cyrillic)", () -> updateEncoding("KOI8-R"));
		m.separator();
		// jp
		m.item("Autocorrect: Japanese", () -> updateEncoding("x-JISAutoDetect"));
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
			c.setCellValueFactory((d) ->
			{
				return d.getValue().yearProperty();
			});
		}
		
		addColumns
		(
			120,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF
		);
		addRows
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL
		);
		int r = 0;
		add(0, r, 1, 5, artField);
		add(1, r, FX.label("Title:", Pos.CENTER_RIGHT));
		add(2, r, 4, 1, titleField);
		r++;
		add(1, r, FX.label("Album:", Pos.CENTER_RIGHT));
		add(2, r, 4, 1, albumField);
		r++;
		add(1, r, FX.label("Artist:", Pos.CENTER_RIGHT));
		add(2, r, 4, 1, artistField);
		r++;
		add(1, r, FX.label("Year:", Pos.CENTER_RIGHT));
		add(2, r, yearField);
		add(3, r, moreButton);
		add(4, r, new FxButton("Update Album", updateAlbumAction));
		add(5, r, new FxButton("Update", updateAction, FxButton.AFFIRM));
		r++;
		r++;
		add(0, r, 6, 1, table);

		FX.addInvalidationListener(table.getSelectionModel().getSelectedItems(), true, this::handleSelection);
	}
	
	
	protected void handleSelection()
	{
		List<Track> ts = table.getSelectionModel().getSelectedItems();
		int sz = ts.size();
		switch(sz)
		{
		case 0:
			titleField.setText(null);
			break;
		case 1:
			Track t = ts.get(0);
			updateTrackInfo(t);
			break;
		default:
		}
		
		titleField.setDisable(sz == 0);
		
//		updateAlbumAction.setEnabled(
	}

	
	public void updateTrackInfo(Track t)
	{
		artField.setImage(t.getCoverArt());
		titleField.setText(t.getTitle());
		albumField.setText(t.getAlbum());
		artistField.setText(t.getArtist());
		yearField.setText(t.getYear());
	}
	

	public void setTrack(Track t)
	{
		currentTrack = t;
		
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


	protected void update()
	{
		String title = titleField.getText();
		String album = albumField.getText();
		String artist = artistField.getText();
		String year = yearField.getText();

		currentTrack.setTitle(title);

		List<Track> ts = table.getSelectionModel().getSelectedItems();
		for(Track t: ts)
		{
			t.setAlbum(album);
			t.setArtist(artist);
			t.setYear(year);
		}
	}


	protected void updateAlbum()
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
				ICharsetDetector det = (gen == null) ? null : gen.get();
				ID3_Info d = ID3_Info.parseID3(f, det);
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
		if(Desktop.isDesktopSupported())
		{
			try
			{
				Desktop.getDesktop().open(f);
			}
			catch(Exception e)
			{
				log.error(e);
			}
		}
	}
	
	
	protected void openCharsetDialog()
	{
		new CharsetDialog(this).open();
	}
}
