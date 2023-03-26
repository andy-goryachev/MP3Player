// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.mp3player.cm.MusicDB;
import goryachev.mp3player.cm.RTrack;
import java.io.File;


/**
 * Track Info.
 */
public class Track
{
	private final MusicDB db;
	private final int index;
	private final RTrack track;
	
	
	public Track(MusicDB db, RTrack track, int index)
	{
		this.db = db;
		this.index = index;
		this.track = track;
	}
	
	
	public File getFile()
	{
		String path = track.getAlbum().getPath();
		String filename = track.getFileName();
		return db.getFile(path, filename);
	}
	
	
	public String getFileName()
	{
		return track.getFileName();
	}
	
	
	public int getIndex()
	{
		return index;
	}


	public int getAlbumTrackCount()
	{
		return track.getAlbum().getTrackCount();
	}


	public String getTitle()
	{
		return db.getTitle(track);
	}
	
	
	public String getAlbumName()
	{
		return db.getAlbumName(track);
	}


	public String getArtist()
	{
		return db.getArtist(track);
	}


	public String getYear()
	{
		return db.getYear(track);
	}


	public int getTrackNumber()
	{
		return track.getTrackNumber();
	}
}
