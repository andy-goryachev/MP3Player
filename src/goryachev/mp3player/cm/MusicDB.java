// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.Album;
import goryachev.mp3player.Track;
import goryachev.mp3player.util.ID3_Info;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;
import java.security.SecureRandom;


/**
 * Music Database.
 */
public class MusicDB
{
	private static final Log log = Log.get("MusicDB");
	private final File root;
	private final CList<RAlbum> albums = new CList<>();
	private final SecureRandom random;
	private int trackCount;
	
	
	public MusicDB(File root)
	{
		this.root = root;
		this.random = new SecureRandom();
	}
	
	
	public static MusicDB scan(File dir)
	{
		MusicDB d = new MusicDB(dir);
		long start = System.nanoTime();
		d.scanDir(dir);
		double sec = (System.nanoTime() - start) / 1_000_000_000.0; 
		log.info("%d track(s) loaded in %.1f sec.", d.trackCount, sec);
		return d;
	}
	
	
	protected void scanDir(File dir)
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
						scanDir(f);
					}
				}
				
				CList<RTrack> ts = null;
				for(File f: fs)
				{
					if(Utils.isMP3(f))
					{
						if(ts == null)
						{
							ts = new CList<>();
						}

						RTrack t = extractTrackInfo(f);
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
					RTrack[] tracks = CKit.toArray(RTrack.class, ts);
					albums.add(new RAlbum(dir, trackCount, tracks));
					trackCount += tracks.length;
					log.info("%s: %d", dir, tracks.length);
				}
			}
		}
	}
	
	
	protected RTrack extractTrackInfo(File f)
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
		
		return new RTrack(f, title, artist, album, year);
	}
	
	
	public Track randomJump()
	{
		int ix = random.nextInt(trackCount);
		Track t = getTrackAt(ix);
		return t;
	}
	
	
	protected Track getTrackAt(int index)
	{
		int ix = binarySearch(index);
		RAlbum a = albums.get(ix);
		int tix = index - a.index;
		RTrack t = a.tracks[tix];
		return trackInfo(a, t, ix, tix);
	}


	protected Track trackInfo(RAlbum a, RTrack t, int ix, int tix)
	{
		Album album = new Album(ix, a);
		String name = t.getName();
		return new Track(album, tix, name, t.file.getName());
	}


	protected int binarySearch(int index)
	{
		int low = 0;
		int high = albums.size() - 1;

		while(low <= high)
		{
			int mid = (low + high) >>> 1;
			RAlbum a = albums.get(mid);
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


	protected static int compare(RAlbum a, int index)
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


	public Track nextTrack(Track t)
	{
		Album a = t.getAlbum();
		int tix = t.getIndex();
		++tix;
		if(tix < a.getTrackCount())
		{
			return a.getTrack(tix);
		}
		else
		{
			int ix = a.getIndex() + 1;
			if(ix >= albums.size())
			{
				ix = 0;
			}
			a = new Album(ix, albums.get(ix));
			return a.getTrack(0);
		}
	}


	public static MusicDB loadData(File db)
	{
		// TODO
		return null;
	}


	public void store(File f)
	{
		try
		{
			StringWriter wr = new StringWriter(65536);
			for(RAlbum a: albums)
			{
				a.store(wr);
			}
			
			String s = wr.toString();
			CKit.write(f, s);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}
}
