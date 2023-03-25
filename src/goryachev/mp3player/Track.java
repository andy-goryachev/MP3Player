// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import java.io.File;


/**
 * Track Info.
 */
public class Track
{
	private final int index;
	private final Album album;
	private final String name;
	private final String filename;
	// TODO hash
	// TODO RTrack?
	
	
	public Track(Album album, int index, String name, String filename)
	{
		this.index = index;
		this.album = album;
		this.name = name;
		this.filename = filename;
	}
	
	
	public File getFile()
	{
		File dir = album.getDir();
		return new File(dir, filename);
	}
	
	
	public int getIndex()
	{
		return index;
	}
	
	
	public Album getAlbum()
	{
		return album;
	}


	public String getName()
	{
		return name;
	}
}
