// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.CPane;
import goryachev.mp3player.Dirs;
import java.io.File;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;


/**
 * File Tree Pane.
 */
public class FileTreePane extends CPane
{
	public final TreeView<File> tree;
	
	
	public FileTreePane()
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
		
		setCenter(tree);
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
