// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.common.log.Log;
import goryachev.common.util.CDigest;
import goryachev.common.util.CKit;
import goryachev.common.util.FileTools;
import goryachev.common.util.Hex;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;
import javafx.util.Duration;


/**
 * Utils.
 */
public class Utils
{
	private static final String HEX = "0123456789abcdef";
	private static final Log log = Log.get("Utils");
	
	
	public static boolean isMP3(File f)
	{
		if(f.isFile())
		{
			String name = f.getName().toLowerCase(Locale.US);
			if(name.endsWith(".mp3"))
			{
				return true;
			}
		}
		return false;
	}


	public static String trimExtension(String name)
	{
		int ix = name.lastIndexOf('.');
		if(ix >= 0)
		{
			return name.substring(0, ix);
		}
		return name;
	}


	public static String formatTime(Duration d)
	{
		int s = (int)Math.round(d.toSeconds() % 60);
		int m = (int)Math.round(d.toMinutes());
		if(m > 60)
		{
			int h = m / 60;
			m %= 60;
			return String.format("%d:%02d:%02d", h, m, s);
		}
		return String.format("%02d:%02d", m, s);
	}
	
	
	/** |,\,(<0x20) -> \HH */
	public static String encode(String text)
	{
		if(text == null)
		{
			return "";
		}
		
		int ix = indexOfSpecialChar(text);
		if(ix < 0)
		{
			return text;
		}
		
		int sz = text.length();
		StringBuilder sb = new StringBuilder(sz + 32);
		for(int i=0; i<sz; i++)
		{
			char c = text.charAt(i);
			if(isSpecialChar(c))
			{
				sb.append('\\');
				sb.append(hex(c >> 4));
				sb.append(hex(c));
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	
	public static String decode(String text)
	{
		if(CKit.isBlank(text))
		{
			return null;
		}

		int ix = text.indexOf('\\');
		if(ix < 0)
		{
			return text;
		}
		
		int sz = text.length();
		StringBuilder sb = new StringBuilder(sz);
		for(int i=0; i<sz; i++)
		{
			char c = text.charAt(i);
			if(c == '\\')
			{
				int v = nibble(text.charAt(++i));
				v = (v << 4) + nibble(text.charAt(++i));
				sb.append((char)v);
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	
	private static int indexOfSpecialChar(String text)
	{
		int sz = text.length();
		for(int i=0; i<sz; i++)
		{
			char c = text.charAt(i);
			if(isSpecialChar(c))
			{
				return i;
			}
		}
		return -1;
	}
	
	
	private static boolean isSpecialChar(char c)
	{
		if(c < 0x20)
		{
			return true;
		}
		
		switch(c)
		{
		case '|':
		case '\\':
			return true;
		}
		return false;
	}
	
	
	private static char hex(int c)
	{
		return HEX.charAt(c & 0x0f);
	}
	
	
	private static int nibble(char c)
	{
		int ix = HEX.indexOf(Character.toLowerCase(c));
		if(ix < 0)
		{
			throw new RuntimeException();
		}
		return ix;
	}


	public static String computeHash(File f)
	{
		try
		{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
			try
			{
				byte[] hash = CDigest.sha256().compute(in);
				return Hex.toHexString(hash);
			}
			finally
			{
				CKit.close(in);
			}
		}
		catch(Exception e)
		{
			log.error(e);
			return null;
		}
	}
	
	
	public static String computeHash(List<String> names)
	{
		CDigest d = CDigest.sha256();
		for(String s: names)
		{
			d.updateChar(',');
			d.updateString(s);
		}
		byte[] hash = d.digest();
		return Hex.toHexString(hash);
	}


	public static String pathToRoot(File root, File dir)
	{
		return FileTools.getPathToRootWithName(root, dir);
	}
}
