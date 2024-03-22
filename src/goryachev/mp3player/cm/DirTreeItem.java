// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import javafx.scene.control.TreeItem;


/**
 * Directory Tree Item.
 */
public class DirTreeItem extends TreeItem<File>
{
	private Boolean leaf;
	
	
	public DirTreeItem(File f)
	{
		super(f);
		
		expandedProperty().addListener((x) ->
		{
			updateChildren();	
		});
	}
	
	
	public boolean isLeaf()
	{
		if(leaf == null)
		{
			leaf = (listFiles() == null);
		}
		return leaf;
	}
	
	
	public String toString()
	{
		return getValue().getName();
	}
	
	
	protected void updateChildren()
	{
		if(isExpanded())
		{
			List<DirTreeItem> cs = loadChildren();
			getChildren().setAll(cs);
		}
		else
		{
			leaf = null;
			getChildren().clear();
		}
	}
	
	
	protected File[] listFiles()
	{
		FileFilter ff = new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.isDirectory();
			}
		};
		
		File[] fs = getValue().listFiles(ff);
		if(fs != null)
		{
			if(fs.length == 0)
			{
				return null;
			}
			CSorter.collate(fs);
		}
		return fs;
	}


	protected List<DirTreeItem> loadChildren()
	{
		CList<DirTreeItem> rv = new CList<>();
		File[] files = listFiles();
		if(files != null)
		{
			for(File f: files)
			{
				rv.add(new DirTreeItem(f));
			}
		}
		return rv;
	}
}
