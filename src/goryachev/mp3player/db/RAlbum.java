// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.util.Utils;
import java.io.Writer;


/**
 * Repository Album.
 */
public class RAlbum
{
	/** path to root */
	private final String path;
	private final long timestamp;
	private final int trackCount; // why is this needed?
	private CList<RTrack> tracks;
	
	
	public RAlbum(String path, long timestamp, int trackCount)
	{
		this.path = path;
		this.timestamp = timestamp;
		this.trackCount = trackCount;
		this.tracks = new CList<>(trackCount);
	}
	

	// "A|title|artist|year|trackCount|hash|timestamp|path"
	public void write(Writer wr) throws Exception
	{
		wr.write("A|");
		wr.write(String.valueOf(getTrackCount()));
		wr.write("|");
		wr.write(String.valueOf(timestamp));
		wr.write("|");
		wr.write(Utils.encode(path));
		wr.write("\n");
	}
	
	
	public static RAlbum parse(String text) throws Exception
	{
		String[] ss = CKit.split(text, '|');
		if(ss.length == 4)
		{
			if("A".equals(ss[0]))
			{
				int trackCount = Integer.parseInt(Utils.decode(ss[1]));
				long time = Long.parseLong(ss[2]);
				String path = Utils.decode(ss[3]);

				return new RAlbum(path, time, trackCount);
			}
		}
		return null;
	}
	
	
	public void addTrack(RTrack t)
	{
		t.setAlbum(this);
		tracks.add(t);
	}
	
	
	public int getTrackCount()
	{
		return tracks.size();
	}
	
	
	public RTrack getTrack(int ix)
	{
		return tracks.get(ix);
	}


	public String getPath()
	{
		return path;
	}


	public int trackIndex(RTrack t)
	{
		return tracks.indexOf(t);
	}
}