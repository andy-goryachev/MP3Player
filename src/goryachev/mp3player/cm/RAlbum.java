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
	@Deprecated
	private final File dir; // TODO remove
	private final int firstTrackIndex; // transient, does not have to be stored
	private final RTrack[] tracks;
	private String path; // TODO to root
	private String name; // dir name? needed?
	private String artist; // needed? this might come from tracks?
	private String year; // needed?
	// TODO hash: sorted track filenames
	
	
	public RAlbum(File dir, int firstTrackIndex, RTrack[] tracks)
	{
		this.dir = dir;
		this.firstTrackIndex = firstTrackIndex;
		this.tracks = tracks;
	}
	
	
	public static RAlbum create(File root, File dir, int firstTrackIndex, RTrack[] tracks)
	{
		return new RAlbum(dir, firstTrackIndex, tracks);
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
	
	
	public int getFirstTrackIndex()
	{
		return firstTrackIndex;
	}
	
	
	public int trackCount()
	{
		return tracks.length;
	}
	
	
	public RTrack getTrack(int ix)
	{
		return tracks[ix];
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
		return artist;
	}


	public String getYear()
	{
		return year;
	}
}