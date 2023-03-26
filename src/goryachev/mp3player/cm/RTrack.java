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
	@Deprecated // TODO remove
	private final File file;
	private final String filename;
	private final String title; // title extracted from metadata
	private final String artist;
	private final String album;
	private final String year;
	private String hash; // TODO hash
	
	
	public RTrack(File f, String name, String artist, String album, String year)
	{
		this.file = f;
		this.filename = f.getName();
		this.title = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	
	// "T|name|artist|year|filename"
	public void store(StringWriter wr)
	{
		wr.write("T|");
		wr.write(Utils.encode(title));
		wr.write("|");
		wr.write(Utils.encode(artist));
		wr.write("|");
		wr.write(Utils.encode(year));
		wr.write("|");
		wr.write(Utils.encode(filename));
		wr.write("\n");
	}
	
	
	public String getFileName()
	{
		return filename;
	}
	
	
	public String getName()
	{
		if(title == null)
		{
			return Utils.trimExtension(file.getName());
		}
		return title;
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
		sb.append("file=").append(file);
		
		return sb.toString();
	}
}