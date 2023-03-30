// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
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
	private final File[] files;
	
	
	public DirTreeItem(File f)
	{
		super(f);
		
		this.files = listFiles();
		
		// TODO list files here and set leaf property
		expandedProperty().addListener((x) -> updateChildren());
	}
	
	
	public boolean isLeaf()
	{
		return files == null;
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
		CList<DirTreeItem> rv = new CList<>(files.length);
		for(File f: files)
		{
			rv.add(new DirTreeItem(f));
		}
		return rv;
	}
}
