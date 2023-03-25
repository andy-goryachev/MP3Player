// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import java.io.File;


/**
 * Track Info.
 */
public class TrackInfo
{
	private final int albumIndex; // not needed
	private final int trackIndex;
	private final AlbumInfo album;
	private final String name;
	private final File file; // file name
	
	
	public TrackInfo(int albumIndex, int trackIndex, AlbumInfo album, String name, File file)
	{
		this.albumIndex = albumIndex;
		this.trackIndex = trackIndex;
		this.album = album;
		this.name = name;
		this.file = file;
	}
	
	
	public File getFile()
	{
		return file;
	}
	
	
	public int getIndex()
	{
		return trackIndex;
	}
	
	
	public AlbumInfo getAlbum()
	{
		return album;
	}
}
