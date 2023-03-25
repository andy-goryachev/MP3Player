// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.util.SB;
import goryachev.mp3player.util.Utils;
import java.io.File;


/**
 * Repository Track.
 */
public class RTrack
{
	public final File file;
	public final String title;
	public final String artist;
	public final String album;
	public final String year;
	
	
	public RTrack(File f, String title, String artist, String album, String year)
	{
		this.file = f;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
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