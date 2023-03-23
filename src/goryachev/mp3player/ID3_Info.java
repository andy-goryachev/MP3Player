// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
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
		RandomAccessFile in = null;
		
		try
		{
			in = new RandomAccessFile(file,"r");
		}
		catch(Throwable t)
		{
			return null;
		}

		ID3_Info info = ID3v2Info.readInfo(in);
		if(info == null)
		{
			info = ID3v1Info.readInfo(in);
		}

		try
		{
			in.close();
		}
		catch(Exception e)
		{ 
		}
	
		// debug
		//Log.print(info);
		return info;
	}
	
	
	// function detects incorrectly encoded Russian string -
	// i.e. a lot of characters in the range [0x80..0xff], and none >0x100 
	public static boolean isRussian(String s)
	{
		int ascii = 0;
		int nonascii = 0;
		
		for(int i=0; i<s.length(); i++)
		{
			int c = s.charAt(i);
			if(c > 0x100)
			{
				return false;
			}
			else if(c >= 0x80)
			{
				++nonascii;
			}
			else if(c > 0x40)
			{
				++ascii;
			}
		}
		
		// russian if non-ascii > 1/4 of ascii
		if(4*nonascii > ascii)
		{
			//Log.print(" isRussian");
			return true;
		}
		else
		{
			return false;
		} 
	}
	


	private static String cyrillic = "........................................\u0401...............\u0451.......\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042A\u042B\u042C\u042D\u042E\u042F\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044A\u044B\u044C\u044D\u044E\u044F";
	
	
	public static String toRussian(String s)
	{
		// convert array to string
		StringBuffer sb = new StringBuffer(s);

		for(int i=0; i<s.length(); i++)
		{
			int c = s.charAt(i);
			if(c > 0x80)
			{
				sb.setCharAt(i,cyrillic.charAt(c & 0x7f));
			}
		}

		return sb.toString();
	}
	
	
	

	// convert byte array to string
	// detecting the language at the same time
	protected String bytes2string(byte[] tag, int offset, int length)
	{
		// count number of non-ascii characters
		int nonascii = 0;
		int chars = 0;

		for(int i=0; i<length; i++)
		{
			byte b = tag[offset+i];

			if((b != 0) && (b != 0x20))
			{
				chars++;
			}

			if(b < 0)
			{
				nonascii++;
			}
		}

		// if there is more nonascii chars than a certain (arbitrary)
		// threshold, it's definitely a cyrillic string
		boolean isRussian = false;
		if(chars != 0)
		{
			isRussian = ((float)nonascii / (float)chars) > 0.3f;
		}

		// convert array to string
		StringBuffer sb = new StringBuffer();
		try
		{
			for(int i=0; i<length; i++)
			{
				if(isRussian)
				{
					if(tag[offset+i] < 0)
					{
						sb.append(cyrillic.charAt(tag[offset + i] & 0x7f));
					}
					else
					{
						sb.append((char)(tag[offset+i] & 0xff));
					}
				}
				else
				{
					sb.append((char)(tag[offset+i] & 0xff));
				}
			}
		}
		catch(Throwable r)
		{
			// debug only
			log.error(r);
		}

		return sb.toString();
	}
}
