// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.TextTools;
import goryachev.fx.CPane;
import goryachev.mp3player.MainWindow;
import goryachev.mp3player.db.MusicDB;
import java.util.List;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;


/**
 * Search Pane.
 */
public class SearchPane extends CPane
{
	protected final MusicDB db;
	public final TableView<SearchEntry> table;
	
	
	public SearchPane(MusicDB db)
	{
		this.db = db;
		
		setHGap(5);
		setVGap(10);
		setPadding(10);
		
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
		
		setCenter(table);
		
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
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


	public void setResult(List<SearchEntry> result)
	{
		table.getItems().setAll(result);
	}
}
