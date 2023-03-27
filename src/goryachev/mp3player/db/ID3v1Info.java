// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import java.io.RandomAccessFile;
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
	public ID3v1Info(byte[] tag)
	{
		if(tag.length == 128)
		{
			UniversalDetector d = new UniversalDetector();
			handleData(d, tag, 3, 30);
			handleData(d, tag, 33, 30);
			handleData(d, tag, 63, 30);
			handleData(d, tag, 93, 4);
			handleData(d, tag, 97, 30);
			d.dataEnd();
			String enc = d.getDetectedCharset();
			if(enc == null)
			{
				enc = "UTF-8";
			}

			title = parse(tag, 3, 30, enc);
			artist = parse(tag, 33, 30, enc);
			album = parse(tag, 63, 30, enc);
			year = parse(tag, 93, 4, enc);
			//comment = parse(tag, 97, 30, enc);
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


	protected String parse(byte[] bytes, int off, int len, String enc)
	{
		len = findZero(bytes, off, len);
		
		try
		{
			return new String(bytes, off, len, enc).trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new String(bytes, off, len, CKit.CHARSET_ASCII).trim();
		}
	}


	public static ID3_Info readInfo(RandomAccessFile in)
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
					return new ID3v1Info(tag);
				}
			}
		}
		catch(Throwable e)
		{
		}

		return null;
	}
}
