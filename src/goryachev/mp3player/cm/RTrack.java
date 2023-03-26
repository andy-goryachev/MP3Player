// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.CKit;
import goryachev.common.util.SB;
import goryachev.mp3player.util.Utils;
import java.io.Writer;


/**
 * Repository Track.
 */
public class RTrack
{
	private transient RAlbum parent;
	private final String title;
	private final String artist;
	private final String album;
	private final String year;
	private final String filename;
	private final String hash;
	
	
	public RTrack(String title, String artist, String album, String year, String filename, String hash)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
		this.filename = filename;
		this.hash = hash;
	}
	
	
	// "T|title|artist|album|year|hash|filename"
	public void write(Writer wr) throws Exception
	{
		wr.write("T|");
		wr.write(Utils.encode(title));
		wr.write("|");
		wr.write(Utils.encode(artist));
		wr.write("|");
		wr.write(Utils.encode(album));
		wr.write("|");
		wr.write(Utils.encode(year));
		wr.write("|");
		wr.write(Utils.encode(hash));
		wr.write("|");
		wr.write(Utils.encode(filename));
		wr.write("\n");
	}
	
	
	public static RTrack parse(String text) throws Exception
	{
		String[] ss = CKit.split(text, '|');
		if(ss.length == 7)
		{
			if("T".equals(ss[0]))
			{
				String title = Utils.decode(ss[1]);
				String artist = Utils.decode(ss[2]);
				String album = Utils.decode(ss[3]);
				String year = Utils.decode(ss[4]);
				String hash = Utils.decode(ss[5]);
				String filename = Utils.decode(ss[6]);

				return new RTrack(title, artist, album, year, filename, hash);
			}
		}
		return null;
	}
	
	
	void setAlbum(RAlbum a)
	{
		parent = a;
	}
	
	
	public RAlbum getAlbum()
	{
		return parent;
	}
	
	
	public int getTrackNumber()
	{
		return parent.trackNumber(this);
	}
	
	
	public String getFileName()
	{
		return filename;
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
	
	
	public String toString()
	{
		SB sb = new SB();
		sb.append("{");
		boolean sep = false;
		
		if(title != null)
		{
			sb.append("title=").append(title);
			sep = true;
		}
		
		if(artist != null)
		{
			if(sep)
			{
				sb.append(", ");
			}
			sb.append("artist=").append(artist);
			sep = true;
		}
		
		if(album != null)
		{
			if(sep)
			{
				sb.append(", ");
			}
			sb.append("album=").append(album);
			sep = true;
		}
		
		if(year != null)
		{
			if(sep)
			{
				sb.append(", ");
			}
			sb.append("year=").append(year);
			sep = true;
		}
		
		if(sep)
		{
			sb.append(", ");
		}
		sb.append("filename=").append(filename);
		
		return sb.toString();
	}
}