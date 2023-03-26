// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.mp3player.db.RAlbum;


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
	
	
	public int getIndex()
	{
		return index;
	}
	
	
	public int getTrackCount()
	{
		return album.getTrackCount();
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
