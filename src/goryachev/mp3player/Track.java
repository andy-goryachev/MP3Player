// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.mp3player.cm.RTrack;
import java.io.File;


/**
 * Track Info.
 */
public class Track
{
	private final int index;
	private final Album album;
	private final String title;
	private final RTrack track;
	
	
	public Track(Album album, RTrack track, int index, String title)
	{
		this.index = index;
		this.track = track;
		this.album = album;
		this.title = title;
	}
	
	
	public File getFile()
	{
		File dir = album.getDir();
		return new File(dir, getFileName());
	}
	
	
	public String getFileName()
	{
		return track.getFileName();
	}
	
	
	public int getIndex()
	{
		return index;
	}
	
	
	public Album getAlbum()
	{
		return album;
	}


	public String getTitle()
	{
		return title;
	}
}
