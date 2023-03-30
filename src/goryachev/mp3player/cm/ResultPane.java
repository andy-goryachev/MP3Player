// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.CPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


/**
 * Search Result Pane.
 */
public class ResultPane extends CPane
{
	protected final TableView<SearchEntry> table;
	
	
	public ResultPane()
	{
		setHGap(5);
		setVGap(10);
		setPadding(10);
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		table.getColumns().addAll
		(
			new TableColumn<SearchEntry,String>("No"),
			new TableColumn<SearchEntry,String>("Title"),
			new TableColumn<SearchEntry,String>("Album"),
			new TableColumn<SearchEntry,String>("Artist"),
			new TableColumn<SearchEntry,String>("Year")
		);
		
		setCenter(table);
	}
}
