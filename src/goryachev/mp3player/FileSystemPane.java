// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.CComparator;
import goryachev.common.util.CList;
import goryachev.fx.CPane;
import java.io.File;
import java.io.FileFilter;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Duration;


/**
 * File System Pane.
 */
public class FileSystemPane extends CPane
{
	protected final TreeView<File> tree;
	protected final TableView<FileEntry> table;
	
	
	public FileSystemPane()
	{
		tree = new TreeView<>();
		tree.setShowRoot(true);
		tree.setCellFactory((x) ->
		{
			return new TextFieldTreeCell<File>()
			{
				protected String getString()
				{
					return getItem().getName();
				}
				
				public void updateItem(File item, boolean empty)
				{
					super.updateItem(item, empty);

					if(empty)
					{
						setText(null);
						setGraphic(null);
					}
					else
					{
						setText(getString());
						setGraphic(getTreeItem().getGraphic());
					}
				}
			};
		});
		tree.getSelectionModel().selectedItemProperty().addListener((s,p,c) ->
		{
			handleSelection(c == null ? null : c.getValue());
		});
		
		table = new TableView<>();
		table.setPlaceholder(new Label("Empty Folder"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		{
			TableColumn<FileEntry,String> c = new TableColumn<>("Name");
			table.getColumns().add(c);
			c.setPrefWidth(300);
			c.setCellValueFactory((f) ->
			{
				return f.getValue().name();
			});
		}
		{
			TableColumn<FileEntry,Long> c = new TableColumn<>("Length");
			table.getColumns().add(c);
			c.setPrefWidth(50);
			// TODO right alignment
			c.setCellValueFactory((f) ->
			{
				return f.getValue().length();
			});
		}
		{
			TableColumn<FileEntry,Duration> c = new TableColumn<>("Duration");
			c.setPrefWidth(50);
			// TODO right alignment
			table.getColumns().add(c);
			c.setCellValueFactory((f) ->
			{
				return f.getValue().duration();
			});
		}
		
		CPane detail = new CPane();
		detail.setCenter(table);
		
		SplitPane split = new SplitPane(tree, detail);
		split.setDividerPositions(0.25);
		setCenter(split);
	}


	protected void handleSelection(File dir)
	{
		CList<FileEntry> rv = null;
		if(dir != null)
		{
			FileFilter ff = new FileFilter()
			{
				public boolean accept(File f)
				{
					return !f.isDirectory();
				}
			};
				
			File[] fs = dir.listFiles(ff);
			if(fs != null)
			{
				new CComparator<File>()
				{
					public int compare(File a, File b)
					{
						return collate(a.getName(), b.getName());
					}
				}.sort(fs);
				
				for(File f: fs)
				{
					if(rv == null)
					{
						rv = new CList<>();
					}
					rv.add(new FileEntry(f));
				}
			}
		}

		if(rv == null)
		{
			table.getItems().clear();
		}
		else
		{
			table.getItems().setAll(rv);
		}
	}


	public void init()
	{
		if(tree.getRoot() == null)
		{
			File f = Dirs.getMusicDirectory();
			tree.setRoot(new DirTreeItem(f));
			tree.getRoot().setExpanded(true);
		}
	}
}
