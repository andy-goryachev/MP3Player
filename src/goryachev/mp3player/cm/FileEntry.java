// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.FxLong;
import goryachev.fx.FxObject;
import goryachev.fx.FxString;
import java.io.File;
import javafx.util.Duration;


/**
 * File Entry.
 */
public class FileEntry
{
	private final File file;
	private FxString name;
	private FxObject<Long> length;
	private FxObject<Duration> duration;
	
	
	public FileEntry(File f)
	{
		this.file = f;
	}
	
	
	public FxString name()
	{
		if(name == null)
		{
			name = new FxString(file.getName());
		}
		return name;
	}
	
	
	public FxObject<Long> length()
	{
		if(length == null)
		{
			length = new FxObject<>(file.length());
		}
		return length;
	}
	
	
	public FxObject<Duration> duration()
	{
		if(duration == null)
		{
			duration = new FxObject<>();
			// TODO read duration in a bg thread
		}
		return duration;
	}
}
