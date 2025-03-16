// Copyright © 2006-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import java.io.File;
import java.util.function.Supplier;



public class ID3_Info
{
	protected final String title;
	protected final String artist;
	protected final String album;
	protected final String year;


	public ID3_Info(String title, String artist, String album, String year)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	
	/** parses ID3v1 or ID3v2 info */
	public static ID3_Info parseID3(File file, Supplier<ICharsetDetector> gen)
	{
		return ID3_ParserBase.parseID3(file, gen);
	}


	@Override
	public String toString()
	{
		return
			"{title=" + title +
			", artist=" + artist +
			", album=" + album +
			", year=" + year +
			"}";
	}
	

	public String getAlbum()
	{
		return album;
	}


	public String getArtist()
	{
		return artist;
	}


	public String getTitle()
	{
		return title;
	}


	public String getYear()
	{
		return year;
	}
}
