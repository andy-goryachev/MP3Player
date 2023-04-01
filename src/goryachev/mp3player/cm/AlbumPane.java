// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxButton;
import goryachev.fx.FxMenu;
import goryachev.fx.FxObject;
import goryachev.fx.FxSplitMenuButton;
import goryachev.fx.FxString;
import goryachev.mp3player.CoverArtLabel;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.MusicDB;
import java.util.List;
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
	protected final MusicDB db;
	protected final CoverArtLabel artField;
	protected final TextField titleField;
	protected final TextField albumField;
	protected final TextField artistField;
	protected final TextField yearField;
	protected final TableView<Track> table;
	protected final FxAction updateAction = new FxAction(this::update);
	protected final FxAction updateAlbumAction = new FxAction(this::updateAlbum);
	
	
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
		moreButton.item("Reset");
		FxMenu m = moreButton.menu("Encoding");
		m.item("KOI-8");
		// cyrillic windows
		// jp
		// cn
		
		table = new TableView<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<Track,Integer> c = new TableColumn<>("No");
			table.getColumns().add(c);
			c.setMinWidth(30);
			c.setMaxWidth(30);
			c.setCellValueFactory((d) ->
			{
				return new FxObject<Integer>(d.getValue().getIndex() + 1);
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Title");
			table.getColumns().add(c);
			c.setPrefWidth(300);
			c.setCellValueFactory((d) ->
			{
				return new FxString(d.getValue().getTitle());
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Album");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setCellValueFactory((d) ->
			{
				return new FxString(d.getValue().getAlbumName());
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Artist");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setCellValueFactory((d) ->
			{
				return new FxString(d.getValue().getArtist());
			});
		}
		{
			TableColumn<Track,String> c = new TableColumn<>("Year");
			table.getColumns().add(c);
			c.setPrefWidth(70);
			c.setCellValueFactory((d) ->
			{
				return new FxString(d.getValue().getYear());
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
		albumField.setText(t.getAlbumName());
		artistField.setText(t.getArtist());
		yearField.setText(t.getYear());
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
	
	
	protected void update()
	{
		table.getSelectionModel().getSelectedItems();
		// TODO
	}
	
	
	protected void updateAlbum()
	{
		// TODO
	}
}
