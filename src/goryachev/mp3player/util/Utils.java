// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import java.io.File;
import java.util.Locale;
import javafx.util.Duration;


/**
 * Utils.
 */
public class Utils
{
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
}
