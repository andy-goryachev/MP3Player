// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.mp3player.util.Utils;
import java.io.StringWriter;


/**
 * Repository Album.
 */
public class RAlbum
{
	/** path to root */
	private final String path;
	/** album title from metadata, or directory name */
	private final String title;
	private final String artist;
	private final String year;
	private final String hash;
	private final RTrack[] tracks;
	
	
	public RAlbum(String path, String title, String artist, String year, String hash, RTrack[] tracks)
	{
		this.path = path;
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.hash = hash;
		this.tracks = tracks;
	}
	

	// "A|title|artist|year|trackCount|hash|path"
	public void store(StringWriter wr)
	{
		wr.write("A|");
		wr.write(Utils.encode(title));
		wr.write("|");
		wr.write(Utils.encode(artist));
		wr.write("|");
		wr.write(Utils.encode(year));
		wr.write("|");
		wr.write(String.valueOf(getTrackCount()));
		wr.write("|");
		wr.write(Utils.encode(hash));
		wr.write("|");
		wr.write(Utils.encode(path));
		wr.write("\n");
	}
	
	
	public int getTrackCount()
	{
		return tracks.length;
	}
	
	
	public RTrack getTrack(int ix)
	{
		return tracks[ix];
	}
	
	
	public String getTitle()
	{
		return title;
	}
	
	
	public String getArtist()
	{
		return artist;
	}


	public String getYear()
	{
		return year;
	}


	public String getPath()
	{
		return path;
	}
}