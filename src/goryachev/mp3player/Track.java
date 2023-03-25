// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import java.io.File;


/**
 * Track Info.
 */
public class Track
{
	private final int albumIndex; // not needed
	private final int trackIndex;
	private final Album album;
	private final String name;
	private final File file; // file name
	
	
	public Track(int albumIndex, int trackIndex, Album album, String name, File file)
	{
		this.albumIndex = albumIndex;
		this.trackIndex = trackIndex;
		this.album = album;
		this.name = name;
		this.file = file;
	}
	
	
	public File getFile()
	{
		return file;
	}
	
	
	public int getIndex()
	{
		return trackIndex;
	}
	
	
	public Album getAlbum()
	{
		return album;
	}
}
