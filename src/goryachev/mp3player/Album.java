// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.mp3player.cm.RAlbum;
import java.io.File;


/**
 * Album Info.
 */
public class Album
{
	private final int index;
	private final RAlbum album;
	
	
	public Album(int albumIndex, RAlbum a)
	{
		this.index = albumIndex;
		this.album = a;
	}
	
	
	public int getTrackCount()
	{
		return album.trackCount();
	}


	public File getDir()
	{
		return album.getDir();
	}


	public String getName()
	{
		return album.getName();
	}


	public String getArtist()
	{
		return album.getArtist();
	}


	public String getYear()
	{
		return album.getYear();
	}
}
