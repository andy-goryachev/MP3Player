// Copyright © 2006-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.function.Supplier;


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
public class ID3v1_Parser extends ID3_ParserBase
{
	public ID3v1_Parser(Supplier<ICharsetDetector> gen)
	{
		super(gen);
	}
	
	
	public ID3_Info parse(RandomAccessFile in) throws Exception
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
				return parse(tag);
			}
		}

		return null;
	}
	
	
	private ID3_Info parse(byte[] tag)
	{
		byte[] ti = trim(tag, 3, 30); // title
		byte[] ar = trim(tag, 33, 30); // artist
		byte[] al = trim(tag, 63, 30); // album
		byte[] yr = trim(tag, 93, 4); // year
		byte[] co = trim(tag, 97, 30); // comment		
		
		Charset cs;
		if(detector == null)
		{
			cs = null;
		}
		else
		{
			update(ti);
			update(ar);
			update(al);
			update(co);
			
			cs = detector.guessCharset();
		}
		
		if(cs == null)
		{
			cs = ISO_8858_1;
		}
		
		String title = toString(ti, cs);
		String artist = toString(ar, cs);
		String album = toString(al, cs);
		String year = toString(yr, cs);
		
		return new ID3_Info(title, artist, album, year);
	}
	
	
	private static byte[] trim(byte[] b, int off, int len)
	{
		len = findZero(b, off, len);
		if(len == 0)
		{
			return null;
		}
		
		return CKit.copy(b, off, len);
	}
	
	
	private void update(byte[] b)
	{
		if(b != null)
		{
			detector.update(b);
		}
	}
	
	
	// FIX
	private static int findZero(byte[] bytes, int off, int len)
	{
		for(int i=0; i<len; i++)
		{
			if(bytes[off + i] == 0)
			{
				return i;
			}
		}
		return len;
	}


	protected static String toString(byte[] bytes, Charset cs)
	{
		if(bytes == null)
		{
			return null;
		}

		try
		{
			return new String(bytes, cs).trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new String(bytes, CKit.CHARSET_ASCII).trim();
		}
	}
}
