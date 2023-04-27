// Copyright © 2006-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.function.Supplier;


/*
Header:10
{
	tag:3 = "ID3"
	version:2
	{
		major:1
		minor:1 - never 0xff
	}
	flags:1 = 'abcd0000', illegal if last 4 bits != 0
	{
		a - Unsynchronisation applied to all frames
		b - header followed by an Extended header
		c - Experimental indicator
		d - Footer present
	}
	size:4 - syncsafe int, effectively 28 bits (MSB is always zero)
}

Extended Header, variable length, optional
{
	size:4 - syncsafe int
	...
}

Frames: variable length
{
	frame ID:4
	size:4 - syncsafe int
	flags:2

	text frames
	{
		text information frame id:4
		text encoding:1
		...
	}
}

Padding: variable length
{
}

Footer: 10, optional
{
}


----------------------------------------------------------------------
https://www.id3.org/id3v2.3.0.html

encoding
     $00   ISO-8859-1 [ISO-8859-1]. Terminated with $00.
     $01   UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. All
           strings in the same frame SHALL have the same byteorder.
           Terminated with $00 00.
     $02   UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM.
           Terminated with $00 00.
     $03   UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.


4.20	AENC	Audio encryption
4.15	APIC	Attached picture
4.11	COMM	Comments
4.25	COMR	Commercial frame
4.26	ENCR	Encryption method registration
4.13	EQUA	Equalization
4.6 	ETCO	Event timing codes
4.16	GEOB	General encapsulated object
4.27	GRID	Group identification registration
4.4 	IPLS	Involved people list
4.21	LINK	Linked information
4.5 	MCDI	Music CD identifier
4.7 	MLLT	MPEG location lookup table
4.24	OWNE	Ownership frame
4.28	PRIV	Private frame
4.17	PCNT	Play counter
4.18	POPM	Popularimeter
4.22	POSS	Position synchronisation frame
4.19	RBUF	Recommended buffer size
4.12	RVAD	Relative volume adjustment
4.14	RVRB	Reverb
4.10	SYLT	Synchronized lyric/text
4.8 	SYTC	Synchronized tempo codes
4.2.1	TALB	Album/Movie/Show title
4.2.1	TBPM	BPM (beats per minute)
4.2.1	TCOM	Composer
4.2.1	TCON	Content type
4.2.1	TCOP	Copyright message
4.2.1	TDAT	Date
4.2.1	TDLY	Playlist delay
4.2.1	TENC	Encoded by
4.2.1	TEXT	Lyricist/Text writer
4.2.1	TFLT	File type
4.2.1	TIME	Time
4.2.1	TIT1	Content group description
4.2.1	TIT2	Title/songname/content description
4.2.1	TIT3	Subtitle/Description refinement
4.2.1	TKEY	Initial key
4.2.1	TLAN	Language(s)
4.2.1	TLEN	Length
4.2.1	TMED	Media type
4.2.1	TOAL	Original album/movie/show title
4.2.1	TOFN	Original filename
4.2.1	TOLY	Original lyricist(s)/text writer(s)
4.2.1	TOPE	Original artist(s)/performer(s)
4.2.1	TORY	Original release year
4.2.1	TOWN	File owner/licensee
4.2.1	TPE1	Lead performer(s)/Soloist(s)
4.2.1	TPE2	Band/orchestra/accompaniment
4.2.1	TPE3	Conductor/performer refinement
4.2.1	TPE4	Interpreted, remixed, or otherwise modified by
4.2.1	TPOS	Part of a set
4.2.1	TPUB	Publisher
4.2.1	TRCK	Track number/Position in set
4.2.1	TRDA	Recording dates
4.2.1	TRSN	Internet radio station name
4.2.1	TRSO	Internet radio station owner
4.2.1	TSIZ	Size
4.2.1	TSRC	ISRC (international standard recording code)
4.2.1	TSSE	Software/Hardware and settings used for encoding
4.2.1	TYER	Year
4.2.2	TXXX	User defined text information frame
4.1 	UFID	Unique file identifier
4.23	USER	Terms of use
4.9 	USLT	Unsychronized lyric/text transcription
4.3.1	WCOM	Commercial information
4.3.1	WCOP	Copyright/Legal information
4.3.1	WOAF	Official audio file webpage
4.3.1	WOAR	Official artist/performer webpage
4.3.1	WOAS	Official audio source webpage
4.3.1	WORS	Official internet radio station homepage
4.3.1	WPAY	Payment
4.3.1	WPUB	Publishers official webpage
4.3.2	WXXX	User defined URL link frame
*/
public class ID3v2_Parser extends ID3_ParserBase
{
	private static final Log log = Log.get("ID3v2_Parser");
	private static final int HEADER_OVERHEAD = 10;
	
	
	public ID3v2_Parser(Supplier<ICharsetDetector> gen)
	{
		super(gen);
	}
	
	
	public ID3_Info parse(RandomAccessFile in) throws Exception
	{
		// An ID3v2 tag can be detected with the following pattern:
		// $49 44 33 yy yy xx zz zz zz zz
		// Where yy is less than $FF, xx is the 'flags' byte and zz is less than $80.
		if(in.read() == 'I')
		{
			if(in.read() == 'D')
			{
				if(in.read() == '3')
				{
					int major = (in.read() & 0xff);
					int minor = (in.read() & 0xff);
					
					if((minor < 0xff) && (major < 0xff))
					{
						return parsePrivate(in);
					}
				}
			}
		}

		return null;
	}
	
	
	private ID3_Info parsePrivate(RandomAccessFile in) throws Exception
	{
		int flags = in.read();
		int size = syncSafeInt(in);
		
		long offset = HEADER_OVERHEAD;
		
		if((flags & 0x40) != 0)
		{
			// extended header
			int sz = syncSafeInt(in);
			offset += sz;	
		}
		
		byte[] al = null;
		byte[] ar = null;
		byte[] ti = null;
		byte[] yr = null;
		byte[] cm = null;
		
		StringBuffer sb = new StringBuffer();
		int maxOffset = size + HEADER_OVERHEAD;
		do
		{
			sb.setLength(0);
			sb.append((char)in.read());
			sb.append((char)in.read());
			sb.append((char)in.read());
			sb.append((char)in.read());
			
			int fsz = syncSafeInt(in);
			// flags
			in.read();
			in.read();

			String type = sb.toString();
			log.debug(type);
			if(type.equals("TPE1"))
			{
				// artist
				ar = readBytes(in, fsz);
				update(ar);
			}
			else if(type.equals("TIT2"))
			{
				// title
				ti = readBytes(in, fsz);
				update(ti);
			}
			else if(type.equals("TALB"))
			{
				// album
				al = readBytes(in, fsz);
				update(al);
			}
			else if(type.equals("TYER"))
			{
				// year
				yr = readBytes(in, fsz);
			}
			else if(type.equals("COMM"))
			{
				/*
				going to skip comments because they seem to contain leading 0x00 bytes
				in addition to the encoding byte
				// comment
				cm = readBytes(in, fsz);
				update(cm);
				*/
			}
			
			if(fsz == 0)
			{
				break;
			}
			
			offset += (fsz + 10);
			in.seek(offset);
		} while(offset < maxOffset);
		
		Charset cs = (detector == null) ? null : detector.guessCharset();
		String album = toString(al, cs);
		String artist = toString(ar, cs);
		String title = toString(ti, cs);
		String year = toString(yr, cs);
		
		return new ID3_Info(title, artist, album, year);
	}
	
	
	private void update(byte[] bytes)
	{
		if(detector != null)
		{
			detector.update(bytes, 1, bytes.length - 1);
		}
	}


	private static byte[] readBytes(RandomAccessFile in, int size) throws Exception
	{
		byte[] b = new byte[size];
		in.read(b);
		// TODO trim null bytes
		return b;
	}
	
	
	/*
	 * Encoding:
	 * 
	 * $00   ISO-8859-1 [ISO-8859-1]. Terminated with $00.
	 * $01   UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. All
	 *       strings in the same frame SHALL have the same byteorder.
  	 *       Terminated with $00 00.
	 * $02   UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM.
	 *       Terminated with $00 00.
	 * $03   UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
	 */
	private static String toString(byte[] bytes, Charset cs)
	{
		if(bytes == null)
		{
			return null;
		}

		if(cs == null)
		{
			switch(bytes[0])
			{
			case 1:
				cs = UTF_16;
				break;
			case 2:
				cs = UTF_16BE;
				break;
			case 3:
				cs = UTF_8;
				break;
			case 0:
			default:
				cs = ISO_8858_1;
				break;
			}
		}
		
		return new String(bytes, 1, bytes.length - 1, cs).trim();
	}


	private static int syncSafeInt(RandomAccessFile in) throws Exception
	{
		int d = (in.read() & 0x7f) << 21;
		d |= ((in.read() & 0x7f) << 14);
		d |= ((in.read() & 0x7f) << 7);
		d |= (in.read() & 0x7f);
		return d;
	}
}
