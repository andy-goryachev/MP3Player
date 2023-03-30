// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.mp3player.CoverArtLabel;
import goryachev.mp3player.Track;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


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
		
		titleField = new TextField();
		
		albumField = new TextField();
		
		artistField = new TextField();
		
		yearField = new TextField();
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		table.getColumns().addAll
		(
			new TableColumn<Track,String>("No"),
			new TableColumn<Track,String>("Title"),
			new TableColumn<Track,String>("Album"),
			new TableColumn<Track,String>("Artist"),
			new TableColumn<Track,String>("Year"),
			new TableColumn<Track,String>("Duration")
		);
		
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
		add(0, r, 3, 1, table);
	}


	public void setTrack(Track t)
	{
		artField.setImage(t.getCoverArt());
		titleField.setText(t.getTitle());
	}
}
