// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.FH;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.db.RTrack;
import java.io.File;
import javafx.scene.image.Image;


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
	
	
	public MusicDB getDB()
	{
		return db;
	}
	
	
	public File getFile()
	{
		String path = track.getRAlbum().getPath();
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
		return track.getRAlbum().getTrackCount();
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


	public int getTrackIndex()
	{
		return track.getTrackIndex();
	}
	
	
	public String toString()
	{
		return track.toString();
	}


	public Image getCoverArt()
	{
		File dir = getFile().getParentFile();
		return db.getCoverArt(dir);
	}


	public Track getTrackAt(int ix)
	{
		RTrack t = track.getRAlbum().getTrack(ix);
		// FIX track index is incorrect!
		return new Track(db, t, ix);
	}
	
	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof Track t)
		{
			return track == t.track;
		}
		return false;
	}
	
	
	public int hashCode()
	{
		int h = FH.hash(Track.class);
		h = FH.hash(h, index);
		return h;
	}
}
