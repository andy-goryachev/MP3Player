// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;


/**
 * Repository Album.
 */
public class RAlbum
{
	private final File dir; // TODO remove
	public final int index; // TODO remove
	public final RTrack[] tracks;
	private String path;
	private String name;
	private String artist;
	private String year;
	
	
	public RAlbum(File dir, int index, RTrack[] tracks)
	{
		this.dir = dir;
		this.index = index;
		this.tracks = tracks;
	}
	

	// "A|trackCount|name|artist|year|path"
	public void store(StringWriter wr)
	{
		wr.write("A|");
		wr.write(String.valueOf(trackCount()));
		wr.write("|");
		wr.write(Utils.encode(name));
		wr.write("|");
		wr.write(Utils.encode(artist));
		wr.write("|");
		wr.write(Utils.encode(year));
		wr.write("|");
		wr.write(Utils.encode(path));
		wr.write("\n");
		
		for(RTrack t: tracks)
		{
			t.store(wr);
		}
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