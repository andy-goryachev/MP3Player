// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import java.nio.charset.Charset;


/**
 * Russian Legacy Encoding Detector.
 */
public class RussianDetector
{
	private final Charset[] charsets;
	private final String letters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
	
	
	public RussianDetector()
	{
		charsets = new Charset[]
		{
			Charset.forName("Cp1251"),
			Charset.forName("KOI8-R"),
			Charset.forName("cp866"),
			Charset.forName("ISO-8859-5"),
			CKit.CHARSET_UTF8,
			CKit.CHARSET_ASCII
		};
	}


	public String convert(byte[] bytes, int offset, int length) throws Exception
	{
		for(Charset cs: charsets)
		{
			try
			{
				String s = new String(bytes, offset, length);
				if(!isBad(s))
				{
					return s;
				}
			}
			catch(Exception e)
			{ }
		}
		
		throw new Exception();
	}


	public String fix(String s)
	{
		// TODO try to detect malformed strings, e.g. Ìàëåíüêàÿ õîçÿéêà
		// and perhaps also convert to lowercase. e.g. éÎÓÔÒÕÍÅÎÔÁÌØÎÙÅ
		if(isBad(s))
		{
			byte[] b = s.getBytes(CKit.CHARSET_8859_1);
			try
			{
				return convert(b, 0, b.length);
			}
			catch(Exception e)
			{ }
		}
		return s;
	}
	
	
	protected boolean isBad(String s)
	{
		// TODO use statistics, illegal transitions, 3 consecutive vowels etc.
		int sz = s.length();
		for(int i=0; i<sz; i++)
		{
			char c = Character.toLowerCase(s.charAt(i));
			if(c >= 0x80)
			{
				int ix = letters.indexOf(c);
				if(ix < 0)
				{
					return true;
				}
			}
		}
		return false;
	}
}
