// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import java.io.File;
import java.util.Locale;


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
}
