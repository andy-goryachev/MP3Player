// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


/**
 * Search Pane.
 */
public class SearchPane extends CPane
{
	public final TextField queryField;
	public final TableView<SearchEntry> table;
	
	
	public SearchPane()
	{
		setHGap(5);
		setVGap(10);
		setPadding(10);
		
		queryField = new TextField();
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		table.getColumns().addAll
		(
			new TableColumn<SearchEntry,String>("Title"),
			new TableColumn<SearchEntry,String>("Album"),
			new TableColumn<SearchEntry,String>("Artist"),
			new TableColumn<SearchEntry,String>("Year"),
			new TableColumn<SearchEntry,String>("File")
		);
		
		addColumns
		(
			CPane.PREF,
			CPane.FILL
		);
		addRows
		(
			CPane.PREF,
			CPane.FILL
		);
		add(0, 0, FX.label("Find:", Pos.CENTER_RIGHT));
		add(1, 0, queryField);
		add(0, 1, 2, 1, table);
	}
}
