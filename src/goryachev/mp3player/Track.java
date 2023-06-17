// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.CKit;
import goryachev.common.util.FH;
import goryachev.common.util.SB;
import goryachev.fx.FxObject;
import goryachev.fx.FxString;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.db.RTrack;
import java.io.File;
import javafx.beans.InvalidationListener;
import javafx.scene.image.Image;


/**
 * Track Info.
 */
public class Track
{
	private final MusicDB db;
	private final RTrack track;
	private final InvalidationListener listener;
	private FxString title;
	private FxString album;
	private FxString artist;
	private FxString year;
	private FxObject<Integer> trackNum;
	
	
	public Track(MusicDB db, RTrack track)
	{
		this.db = db;
		this.track = track;
		this.listener = (x) ->
		{
			db.updateTrack(this);
		};
	}
	
	
	public MusicDB getDB()
	{
		return db;
	}
	
	
	public RTrack getRTrack()
	{
		return track;
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
		return titleProperty().get();
	}
	
	
	public void setTitle(String s)
	{
		titleProperty().set(s);
	}
	
	
	public FxString titleProperty()
	{
		if(title == null)
		{
			String v = db.getTitle(track);
			
			title = new FxString();
			title.set(v);
			title.addListener(listener);
		}
		return title;
	}
	
	
	public String getAlbum()
	{
		return albumProperty().get();
	}
	
	
	public void setAlbum(String s)
	{
		albumProperty().set(s);
	}
	
	
	public FxString albumProperty()
	{
		if(album == null)
		{
			String v = db.getAlbum(track);
			
			album = new FxString();
			album.set(v);
			album.addListener(listener);
		}
		return album;
	}


	public String getArtist()
	{
		return artistProperty().get();
	}
	
	
	public void setArtist(String s)
	{
		artistProperty().set(s);
	}
	
	
	public FxString artistProperty()
	{
		if(artist == null)
		{
			String v = db.getArtist(track);
			
			artist = new FxString();
			artist.set(v);
			artist.addListener(listener);
		}
		return artist;
	}
	
	
	public String getYear()
	{
		return yearProperty().get();
	}
	
	
	public void setYear(String s)
	{
		yearProperty().set(s);
	}
	
	
	public FxString yearProperty()
	{
		if(year == null)
		{
			String v = db.getYear(track);
			
			year = new FxString();
			year.set(v);
			year.addListener(listener);
		}
		return year;
	}


	/** track number in the album, 1-based */
	public int getNumber()
	{
		return trackNumberProperty().get();
	}
	
	
	public void setNumber(int n)
	{
		trackNumberProperty().set(n);
	}
	
	
	public FxObject<Integer> trackNumberProperty()
	{
		if(trackNum == null)
		{
			int n = db.getTrackNumber(track);
			
			trackNum = new FxObject<Integer>();
			trackNum.setValue(Integer.valueOf(n));
			trackNum.addListener(listener);
		}
		return trackNum;
	}
	
	
	/** track number in the album, starting with 0 */
	public int getNumber0()
	{
		return getNumber() - 1;
	}
	
	
	private static boolean a(SB sb, String name, String val, boolean sep)
	{
		if(CKit.isNotBlank(val))
		{
			sb.append(name);
			sb.append(val);
			return true;
		}
		return sep;
	}
	
	
	public String toString()
	{
		SB sb = new SB();
		sb.append("{");
		boolean sep = false;
		sep = a(sb, "title=", getTitle(), sep);
		sep = a(sb, "artist=", getArtist(), sep);
		sep = a(sb, "album=", getAlbum(), sep);
		sep = a(sb, "year=", getYear(), sep);
		sep = a(sb, "filename=", getFileName(), sep);
		sb.append("}");
		return sb.toString();
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
