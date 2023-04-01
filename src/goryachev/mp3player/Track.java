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
	private final RTrack track;
	
	
	public Track(MusicDB db, RTrack track)
	{
		this.db = db;
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
		return track.getIndex();
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


	/** track number in the album, as scanned, starting with 0 */
	public int getNumber0()
	{
		// TODO property
		return track.getNumber0();
	}
	
	
	/** track number in the album, starting with 1 */
	public int getNumber()
	{
		return getNumber0() + 1;
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
		return db.getTrack(t.getIndex());
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
		h = FH.hash(h, getIndex());
		return h;
	}
}
