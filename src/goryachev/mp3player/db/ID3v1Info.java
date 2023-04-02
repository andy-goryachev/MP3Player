// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import org.mozilla.universalchardet.UniversalDetector;


// Class parses ID3 v1 tag data
/*
 3 (0-2)    Tag identification. Must contain 'TAG' if tag exists and is correct.
30 (3-32)   Title
30 (33-62)  Artist
30 (63-92)  Album
 4 (93-96)  Year
30 (97-126) Comment
 1 (127)    Genre
*/
public class ID3v1Info
	extends ID3_Info
{
	private ID3v1Info(byte[] tag, Charset cs)
	{
		if(tag.length == 128)
		{
			if(cs == null)
			{
				cs = CKit.CHARSET_UTF8;
			}

			title = parse(tag, 3, 30, cs);
			artist = parse(tag, 33, 30, cs);
			album = parse(tag, 63, 30, cs);
			year = parse(tag, 93, 4, cs);
		}
	}
	
	
	private static void handleData(UniversalDetector d, byte[] bytes, int off, int len)
	{
		len = findZero(bytes, off, len);
		if(len > 0)
		{
			d.handleData(bytes, off, len);
		}
	}
	
	
	private static int findZero(byte[] bytes, int off, int len)
	{
		for(int i=0; i<len; i++)
		{
			if(bytes[i] == 0)
			{
				return i;
			}
		}
		return len;
	}


	protected String parse(byte[] bytes, int off, int len, Charset cs)
	{
		len = findZero(bytes, off, len);
		
		try
		{
			return new String(bytes, off, len, cs).trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new String(bytes, off, len, CKit.CHARSET_ASCII).trim();
		}
	}


	public static ID3_Info readInfo(RandomAccessFile in, Charset cs)
	{
		try
		{
			// attempt to read ID3v1 tag
			long length = in.length();
			if(length >= 128)
			{
				in.seek(length - 128);
				byte[] tag = new byte[128];
				in.read(tag);

				if((tag[0] == 'T') && (tag[1] == 'A') && (tag[2] == 'G'))
				{
					// it's a valid IDv1 tag
					return new ID3v1Info(tag, cs);
				}
			}
		}
		catch(Throwable e)
		{
		}

		return null;
	}
}
