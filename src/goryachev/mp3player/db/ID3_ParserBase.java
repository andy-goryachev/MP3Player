// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.function.Supplier;


/**
 * ID3 Parser Base Class.
 */
public class ID3_ParserBase
{
	private static final Log log = Log.get("ID3ParserBase");
	public static final Charset UTF_16 = Charset.forName("UTF-16");
	public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final Charset ISO_8858_1 = Charset.forName("ISO-8859-1");
	protected final ICharsetDetector detector;
	
	
	public ID3_ParserBase(Supplier<ICharsetDetector> gen)
	{
		detector = (gen == null) ? null : gen.get();
	}

	
	public static ID3_Info parseID3(File file, Supplier<ICharsetDetector> gen)
	{
		try(RandomAccessFile in = new RandomAccessFile(file, "r"))
		{
			ID3_Info rv;
			try
			{
				rv = new ID3v2_Parser(gen).parse(in);
				if(rv != null)
				{
					return rv;
				}
			}
			catch(Exception e)
			{
				log.error(e);
			}
			
			try
			{
				return new ID3v1_Parser(gen).parse(in);
			}
			catch(Exception e)
			{
				log.error(e);
			}
		}
		catch(Throwable e)
		{
			log.error(e);
		}

		return null;
	}
}
