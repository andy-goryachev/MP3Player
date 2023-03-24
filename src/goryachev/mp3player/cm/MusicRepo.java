// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.SB;
import goryachev.mp3player.TrackInfo;
import goryachev.mp3player.util.ID3_Info;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Music Repository.
 */
public class MusicRepo
{
	private static final Log log = Log.get("MusicRepo");
	private final File root;
	private final CList<Album> albums = new CList<>();
	private final SecureRandom random;
	private int trackCount;
	
	
	public MusicRepo(File root)
	{
		this.root = root;
		this.random = new SecureRandom();
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
					albums.add(new Album(trackCount, tracks));
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


	protected static class Album
	{
		public final int index;
		public final Track[] tracks;
		
		
		public Album(int index, Track[] tracks)
		{
			this.index = index;
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
			SB sb = new SB();
			sb.append("{");
			boolean sep = false;
			
			if(title != null)
			{
				sb.append("title=").append(title);
				sep = true;
			}
			
			if(artist != null)
			{
				if(sep)
				{
					sb.append(", ");
				}
				sb.append("artist=").append(artist);
				sep = true;
			}
			
			if(album != null)
			{
				if(sep)
				{
					sb.append(", ");
				}
				sb.append("album=").append(album);
				sep = true;
			}
			
			if(year != null)
			{
				if(sep)
				{
					sb.append(", ");
				}
				sb.append("year=").append(year);
				sep = true;
			}
			
			if(sep)
			{
				sb.append(", ");
			}
			sb.append("file=").append(file);
			
			return sb.toString();
		}
	}


	public TrackInfo randomJump()
	{
		int ix = random.nextInt(trackCount);
		TrackInfo t = getTrackAt(ix);
		return t;
	}
	
	
	protected TrackInfo getTrackAt(int index)
	{
		int ix = binarySearch(index);
		Album a = albums.get(ix);
		int tix = index - a.index;
		Track t = a.tracks[tix];
		return trackInfo(t, ix, tix);
	}


	protected TrackInfo trackInfo(Track t, int ix, int tix)
	{
		return new TrackInfo()
		{
			public File getFile()
			{
				return t.file;
			}
		};
	}


	protected int binarySearch(int index)
	{
		int low = 0;
		int high = albums.size() - 1;

		while(low <= high)
		{
			int mid = (low + high) >>> 1;
			Album a = albums.get(mid);
			int cmp = compare(a, index);
			if(cmp < 0)
			{
				low = mid + 1;
			}
			else if(cmp > 0)
			{
				high = mid - 1;
			}
			else
			{
				return mid;
			}
		}
		return -(low + 1);
	}


	protected static int compare(Album a, int index)
	{
		int ix = index - a.index; 
		if(ix >= a.tracks.length)
		{
			return -1;
		}
		else if(ix < 0)
		{
			return 1;
		}
		return 0;
	}


	public TrackInfo nextTrack(TrackInfo t)
	{
		// FIX
		return randomJump();
	}
}
