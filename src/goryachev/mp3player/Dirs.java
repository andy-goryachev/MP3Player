// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
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
	protected static final String DATA_FILE = "tracks.dat";
	protected static final String INFO_FILE = "info.dat";

	
	public static File getMusicDirectory()
	{
		return GlobalSettings.getFile(MUSIC_DIR);
	}
	
	
	public static void setMusicDirectory(File f)
	{
		GlobalSettings.setFile(MUSIC_DIR, f);
	}


	public static File getDataFile()
	{
		File d1 = CPlatform.getSettingsFolder();
		File d2 = new File(d1, DATA_DIR);
		File f = new File(d2, DATA_FILE);
		return f;
	}
	
	
	public static File getInfoFile()
	{
		File d1 = CPlatform.getSettingsFolder();
		File d2 = new File(d1, DATA_DIR);
		File f = new File(d2, INFO_FILE);
		return f;
	}
}
