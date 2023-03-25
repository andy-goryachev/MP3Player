// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.SB;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;


/**
 * Repository Track.
 */
public class RTrack
{
	public final File file;
	public final String name;
	public final String artist;
	public final String album;
	public final String year;
	private String filename;
	
	
	public RTrack(File f, String name, String artist, String album, String year)
	{
		this.file = f;
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	
	// "T|name|artist|year|filename"
	public void store(StringWriter wr)
	{
		wr.write("T|");
		wr.write(Utils.encode(name));
		wr.write("|");
		wr.write(Utils.encode(artist));
		wr.write("|");
		wr.write(Utils.encode(year));
		wr.write("|");
		wr.write(Utils.encode(filename));
		wr.write("\n");
	}
	
	
	public String getName()
	{
		if(name == null)
		{
			return Utils.trimExtension(file.getName());
		}
		return name;
	}
	
	
	public File getFile()
	{
		return file;
	}
	
	
	public String toString()
	{
		SB sb = new SB();
		sb.append("{");
		boolean sep = false;
		
		if(name != null)
		{
			sb.append("title=").append(name);
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
		sb.append("file=").append(file);
		
		return sb.toString();
	}
}