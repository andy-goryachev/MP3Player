// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;



public class ID3_Info
{
	private static final Log log = Log.get("ID3_Info");
	public static final  Charset UTF_16 = Charset.forName("UTF-16");
	public static final  Charset UTF_16BE = Charset.forName("UTF-16BE");
	public static final  Charset UTF_8 = Charset.forName("UTF-8");
	public static final  Charset ISO_8858_1 = Charset.forName("ISO-8859-1");
	protected String title;
	protected String artist;
	protected String album;
	protected String year;


	protected ID3_Info()
	{
	}


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


	public static ID3_Info parseID3(File file, ICharsetDetector det)
	{
		try (RandomAccessFile in = new RandomAccessFile(file, "r"))
		{
			ID3_Info info = ID3v2Info.readInfo(in, det);
			if(info == null)
			{
				info = ID3v1Info.readInfo(in, det);
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
