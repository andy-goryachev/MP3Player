// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.FileTools;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxPopupMenu;
import goryachev.mp3player.Dirs;
import java.awt.Desktop;
import java.io.File;
import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;


/**
 * File Tree Pane.
 */
public class FileTreePane extends CPane
{
	private static final Log log = Log.get("FileTreePane");
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
				
				@Override
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
		
		FX.setPopupMenu(tree, this::createTreePopupMenu);
		
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


	protected FxPopupMenu createTreePopupMenu()
	{
		FxPopupMenu m = new FxPopupMenu();
		m.item("Open Folder", this::openDirectory); 
		return m;
	}
	
	
	protected void openDirectory()
	{
		TreeItem<File> item = tree.getSelectionModel().getSelectedItem();
		if(item != null)
		{
			File f = item.getValue();
			if(f != null)
			{
				try
				{
					Desktop.getDesktop().open(f);
				}
				catch(Exception e)
				{
					log.error(e);
				}
			}
		}
	}


	public void setDir(File dir)
	{
		if(dir != null)
		{
			TreeItem<File> root = tree.getRoot();
			if(root != null)
			{
				File rootFile = root.getValue();
				String[] path = FileTools.pathToRoot(rootFile, dir);
				if(path != null)
				{
					TreeItem<File> item = root;
					int sz = path.length;
					for(int i=0; i<sz; i++)
					{
						String name = path[i];
						item = findByName(item, name);
						if(item == null)
						{
							break;
						}
						item.setExpanded(true);
					}
					
					if(item != null)
					{
						tree.getSelectionModel().select(item);
						int ix = tree.getRow(item);
						if(ix >= 0)
						{
							tree.scrollTo(ix);
						}
					}
				}
			}
		}
	}


	protected TreeItem<File> findByName(TreeItem<File> item, String name)
	{
		List<TreeItem<File>> cs = item.getChildren();
		for(TreeItem<File> ch: cs)
		{
			String nm = ch.getValue().getName();
			if(name.equals(nm))
			{
				return ch;
			}
		}
		return null;
	}
}
