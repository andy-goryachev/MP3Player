// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.util.ID3_Info;
import goryachev.mp3player.util.Utils;
import java.io.File;


/**
 * Music Repository.
 */
public class MusicRepo
{
	private static final Log log = Log.get("MusicRepo");
	private final File root;
	private final CList<Entry> entries = new CList<>();
	private int trackCount;
	
	
	public MusicRepo(File root)
	{
		this.root = root;
	}
	
	
	public static MusicRepo load(String dir)
	{
		File root = new File(dir);
		MusicRepo r = new MusicRepo(root);
		r.load();
		return r;
	}
	
	
	protected void load()
	{
		long start = System.nanoTime();
		scan(root);
		double sec = (System.nanoTime() - start) / 1_000_000_000.0; 
		log.info("%d track(s) loaded in %.1f sec.", trackCount, sec);
	}
	
	
	protected void scan(File dir)
	{
		if(dir.isDirectory())
		{
			File[] fs = dir.listFiles();
			if(fs != null)
			{
				for(File f: fs)
				{
					if(f.isDirectory())
					{
						scan(f);
					}
				}
				
				CList<Track> ts = null;
				for(File f: fs)
				{
					if(Utils.isMP3(f))
					{
						if(ts == null)
						{
							ts = new CList<>();
						}

						Track t = extractTrackInfo(f);
						if(t == null)
						{
							log.warn("NO TAG " + f);
						}
						else
						{
							log.info(t);
							ts.add(t);
						}
					}
				}
				
				if(ts != null)
				{
					Track[] tracks = CKit.toArray(Track.class, ts);
					entries.add(new Entry(tracks));
					trackCount += tracks.length;
					log.info("%s: %d", dir, tracks.length);
				}
			}
		}
	}
	
	
	protected Track extractTrackInfo(File f)
	{
		ID3_Info t = ID3_Info.parseID3(f);
		
		String title;
		String artist;
		String album;
		String year;
		
		if(t == null)
		{
			title = null;
			artist = null;
			album = null;
			year = null;
		}
		else
		{
			title = t.getTitle();
			artist = t.getArtist();
			album = t.getAlbum();
			year = t.getYear();
		}
		
		return new Track(f, title, artist, album, year);
	}
	
	
	//


	protected static class Entry
	{
		public final Track[] tracks;
		
		
		public Entry(Track[] tracks)
		{
			this.tracks = tracks;
		}
	}
	
	
	//
	
	protected static class Track
	{
		public final File file;
		public final String title;
		public final String artist;
		public final String album;
		public final String year;
		
		
		public Track(File f, String title, String artist, String album, String year)
		{
			this.file = f;
			this.title = title;
			this.artist = artist;
			this.album = album;
			this.year = year;
		}
		
		
		public String toString()
		{
			return
				"{title=" + title +
				", artist=" + artist +
				", album=" + album +
				", year=" + year +
				", file=" + file +
				"}";
		}
	}
}
