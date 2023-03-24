// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import java.io.RandomAccessFile;


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
			title = parse(tag, 3, 30);
			artist = parse(tag, 33, 30);
			album = parse(tag, 63, 30);
			year = parse(tag, 93, 4);
			//comment = parse(tag,97,30);
		}
	}


	protected String parse(byte[] tag, int offset, int length)
	{
		try
		{
			return new String(tag, offset, length, DEFAULT_ENCODING).trim();
		}
		catch(Exception e)
		{
			return null;
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
