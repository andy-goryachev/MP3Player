// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import java.io.File;


/**
 * Repository Album.
 */
public class RAlbum
{
	private final File dir;
	public final int index;
	public final RTrack[] tracks;
	
	
	public RAlbum(File dir, int index, RTrack[] tracks)
	{
		this.dir = dir;
		this.index = index;
		this.tracks = tracks;
	}
	
	
	public int trackCount()
	{
		return tracks.length;
	}
	
	
	public String getName()
	{
		// TODO
		return dir.getName();
	}
	
	
	public File getDir()
	{
		return dir;
	}
	
	
	public String getArtist()
	{
		return null; // TODO
	}


	public String getYear()
	{
		return null; // TODO
	}
}