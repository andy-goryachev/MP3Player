// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.TextTools;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.mp3player.MainWindow;
import goryachev.mp3player.db.MusicDB;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


/**
 * Search Pane.
 */
public class SearchPane extends CPane
{
	protected final MusicDB db;
	public final TextField queryField;
	public final TableView<SearchEntry> table;
	
	
	public SearchPane(MusicDB db)
	{
		this.db = db;
		
		setHGap(5);
		setVGap(10);
		setPadding(10);
		
		queryField = new TextField();
		queryField.setOnAction((ev) -> doSearch());
		
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

		{
			TableColumn<SearchEntry,String> c = new TableColumn<>("Title");
			table.getColumns().add(c);
			c.setPrefWidth(300);
			c.setSortable(false);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().titleProperty();
			});
		}
		{
			TableColumn<SearchEntry,String> c = new TableColumn<>("Album");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setSortable(false);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().albumProperty();
			});
		}
		{
			TableColumn<SearchEntry,String> c = new TableColumn<>("Artist");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setSortable(false);
			c.setCellValueFactory((d) ->
			{
				return d.getValue().artistProperty();
			});
		}
		{
			TableColumn<SearchEntry,String> c = new TableColumn<>("Year");
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
		
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
	}
	

	protected void doSearch()
	{
		String text = queryField.getText();
		CList<String> ss = TextTools.splitWhitespace(text);
		if(ss.size() > 0)
		{
			List<SearchEntry> result = db.search(ss);
			table.getItems().setAll(result);
		}
	}
	
	
	protected void handleClick(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			SearchEntry en = table.getSelectionModel().getSelectedItem();
			if(en != null)
			{
				MainWindow.playTrack(en.getTrack());
			}
		}
	}
}
