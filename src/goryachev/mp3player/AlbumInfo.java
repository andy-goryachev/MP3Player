// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import java.io.File;


/**
 * Album Info.
 */
public class AlbumInfo
{
	private final int albumIndex;
	private final int trackCount;
	private final String name;
	private final File dir;
	
	
	public AlbumInfo(int albumIndex, int trackCount, String name, File dir)
	{
		this.albumIndex = albumIndex;
		this.trackCount = trackCount;
		this.name = name;
		this.dir = dir;
	}
}
