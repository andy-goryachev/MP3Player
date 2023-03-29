// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import java.io.File;
import javafx.scene.image.Image;


/**
 * Small Image Cache.
 */
public class SmallImageCache
{
	private File dir;
	private Image image;


	public SmallImageCache(int size)
	{
	}


	public Image get(File f)
	{
		if(CKit.equals(dir, f))
		{
			return image;
		}
		return null;
	}


	public void add(File f, Image im)
	{
		dir = f;
		image = im;
	}
}
