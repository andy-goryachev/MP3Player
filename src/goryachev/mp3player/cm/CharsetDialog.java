// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import goryachev.fx.FxDialog;
import goryachev.fx.FxString;
import java.nio.charset.Charset;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


/**
 * Charset Selection Dialog.
 */
public class CharsetDialog extends FxDialog
{
	protected final AlbumPane pane;
	protected final TableView<Charset> table;
	
	
	public CharsetDialog(AlbumPane p)
	{
		super(p, "CharsetDialog");
		this.pane = p;
		
		setTitle("Charset Selector");
		
		CList<Charset> all = new CList<>(Charset.availableCharsets().values());
		CSorter.sort(all);
		
		table = new TableView<>();
		table.getItems().setAll(all);
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<Charset,String> c = new TableColumn<>("Name");
			table.getColumns().add(c);
			c.setPrefWidth(200);
			c.setSortable(false);
			c.setCellValueFactory((d) ->
			{
				return new FxString(d.getValue().displayName());
			});
		}
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<Charset,String> c = new TableColumn<>("Description");
			table.getColumns().add(c);
			c.setPrefWidth(300);
			c.setSortable(false);
			c.setCellValueFactory((d) ->
			{
				return new FxString(description(d.getValue()));
			});
		}
		
		setCenter(table);
		
		table.getSelectionModel().selectedItemProperty().addListener((s,pr,c) ->
		{
			handleSelection(c);
		});
	}
	
	
	protected String description(Charset cs)
	{
		// TODO
		return null;
	}


	protected void handleSelection(Charset cs)
	{
		// TODO update current track instead, use [Apply] and [Apply to Album] buttons
		pane.updateEncoding(cs.toString());
	}
}
