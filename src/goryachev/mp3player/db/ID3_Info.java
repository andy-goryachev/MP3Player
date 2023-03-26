// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import java.io.File;
import java.io.RandomAccessFile;



public class ID3_Info
{
	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	private static final Log log = Log.get("ID3_Info");
	protected String title;
	protected String artist;
	protected String album;
	protected String year;


	protected ID3_Info()
	{ }
	
	
	public ID3_Info(String title, String artist, String album, String year)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}


	public String toString()
	{
		return
			getClass().getName() + 
			"\n title=" + title +
			"\n artist=" + artist +
			"\n album=" + album +
			"\n year=" + year +
			"\n";
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


	public static ID3_Info parseID3(File file)
	{
		try (RandomAccessFile in = new RandomAccessFile(file, "r"))
		{
			ID3_Info info = ID3v2Info.readInfo(in);
			if(info == null)
			{
				info = ID3v1Info.readInfo(in);
			}
			return info;
		}
		catch(Throwable t)
		{
			// ignore
		}

		return null;
	}
}
