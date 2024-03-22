// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.CPlatform;
import goryachev.common.util.GlobalSettings;
import java.io.File;


/**
 * Directories.
 */
public class Dirs
{
	protected static final String MUSIC_DIR = "MUSIC_DIR";
	protected static final String DATA_DIR = "MP3Player";

	
	public static File getMusicDirectory()
	{
		return GlobalSettings.getFile(MUSIC_DIR);
	}
	
	
	public static void setMusicDirectory(File f)
	{
		GlobalSettings.setFile(MUSIC_DIR, f);
	}


	public static File getDatabaseDirectory()
	{
		File d1 = CPlatform.getSettingsFolder();
		return new File(d1, DATA_DIR);
	}
}
