// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.GlobalSettings;
import java.io.File;


/**
 * Directories.
 */
public class Dirs
{
	protected static final String MUSIC_DIR = "MUSIC_DIR";

	
	public static File getMusicDirectory()
	{
		return GlobalSettings.getFile(MUSIC_DIR);
	}
	
	
	public static void setMusicDirectory(File f)
	{
		GlobalSettings.setFile(MUSIC_DIR, f);
	}
}
