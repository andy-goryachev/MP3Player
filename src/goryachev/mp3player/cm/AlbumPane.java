// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxObject;
import goryachev.fx.FxString;
import goryachev.mp3player.CoverArtLabel;
import goryachev.mp3player.Track;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;


/**
 * Album Pane.
 */
public class AlbumPane extends CPane
{
	public final CoverArtLabel artField;
	public final TextField titleField;
	public final TextField albumField;
	public final TextField artistField;
	public final TextField yearField;
	public final TableView<Track> table;
	
	
	public AlbumPane()
	{
		setHGap(5);
		setVGap(3);
		setPadding(10);
		
		artField = new CoverArtLabel();
		
		// TODO remove track title?
		titleField = new TextField();
		
		albumField = new TextField();
		
		artistField = new TextField();
		
		yearField = new TextField();
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<Track,Integer> c = new TableColumn<>("No");
			table.getColumns().add(c);
			c.setPrefWidth(30);
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
		{
			TableColumn<Track,Duration> c = new TableColumn<>("Duration");
			table.getColumns().add(c);
			c.setPrefWidth(70);
			c.setCellValueFactory((d) ->
			{
				return new FxObject<Duration>(null); // TODO
			});
		}
		
		addColumns
		(
			100,
			CPane.PREF,
			CPane.FILL
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
		add(2, r, titleField);
		r++;
		add(1, r, FX.label("Album:", Pos.CENTER_RIGHT));
		add(2, r, albumField);
		r++;
		add(1, r, FX.label("Artist:", Pos.CENTER_RIGHT));
		add(2, r, artistField);
		r++;
		add(1, r, FX.label("Year:", Pos.CENTER_RIGHT));
		add(2, r, yearField);
		r++;
		r++;
		add(0, r, 3, 1, table);
	}


	public void setTrack(Track t)
	{
		artField.setImage(t.getCoverArt());
		titleField.setText(t.getTitle());
		albumField.setText(t.getAlbumName());
		artistField.setText(t.getArtist());
		yearField.setText(t.getYear());
		
		// TODO move to Track?
		int sz = t.getAlbumTrackCount();
		CList<Track> ts = new CList<>(sz);
		for(int i=0; i<sz; i++)
		{
			ts.add(t.getTrackAt(i));
		}
		table.getItems().setAll(ts);
	}
}
