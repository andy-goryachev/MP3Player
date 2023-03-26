// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.util.Utils;
import java.io.Writer;


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
	private final int trackCount;
	private CList<RTrack> tracks;
	
	
	public RAlbum(String path, String title, String artist, String year, String hash, int trackCount)
	{
		this.path = path;
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.hash = hash;
		this.trackCount = trackCount;
		this.tracks = new CList<>(trackCount);
	}
	

	// "A|title|artist|year|trackCount|hash|path"
	public void write(Writer wr) throws Exception
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
	
	
	public static RAlbum parse(String text) throws Exception
	{
		String[] ss = CKit.split(text, '|');
		if(ss.length == 7)
		{
			if("A".equals(ss[0]))
			{
				String title = Utils.decode(ss[1]);
				String artist = Utils.decode(ss[2]);
				String year = Utils.decode(ss[3]);
				int trackCount = Integer.parseInt(Utils.decode(ss[4]));
				String hash = Utils.decode(ss[5]);
				String path = Utils.decode(ss[6]);

				return new RAlbum(path, title, artist, year, hash, trackCount);
			}
		}
		return null;
	}
	
	
	public void addTrack(RTrack t)
	{
		t.setAlbum(this);
		tracks.add(t);
	}
	
	
	public int getTrackCount()
	{
		return tracks.size();
	}
	
	
	public RTrack getTrack(int ix)
	{
		return tracks.get(ix);
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