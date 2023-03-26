// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.common.log.Log;
import goryachev.common.util.CComparator;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import goryachev.mp3player.Track;
import goryachev.mp3player.util.ID3_Info;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Function;


/**
 * Music Database.
 */
public class MusicDB
{
	private static final Log log = Log.get("MusicDB");
	private final File root;
	private final CList<RTrack> tracks = new CList<>();
	// TODO user-entered track info db
	private final SecureRandom random;
	
	
	public MusicDB(File root)
	{
		this.root = root;
		this.random = new SecureRandom();
	}
	
	
	public static MusicDB scan(File dir)
	{
		MusicDB d = new MusicDB(dir);
		long start = System.nanoTime();
		d.scanDir(dir, dir);
		double sec = (System.nanoTime() - start) / 1_000_000_000.0; 
		log.info("%d track(s) loaded in %.1f sec.", d.trackCount(), sec);
		return d;
	}
	
	
	protected void scanDir(File root, File dir)
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
						scanDir(root, f);
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

						RTrack t = createTrack(f);
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
					RTrack[] trs = CKit.toArray(RTrack.class, ts);
					sort(trs);
					
					String hash = hash(trs);
					String path = Utils.pathToRoot(root, dir);
					String title = getIfSame(trs, (t) -> t.getTitle());
					String artist = getIfSame(trs, (t) -> t.getArtist());
					String year = getIfSame(trs, (t) -> t.getYear());
					RAlbum a = new RAlbum(path, title, artist, year, hash, trs);
					
					for(RTrack t: trs)
					{
						t.setAlbum(a);
					}
					
					tracks.addAll(trs);
					
					log.info("%s: %d", dir, trs.length);
				}
			}
		}
	}


	/** sorts tracks by filename */
	protected void sort(RTrack[] trs)
	{
		new CComparator<RTrack>()
		{
			public int compare(RTrack a, RTrack b)
			{
				return compareAsStrings(a.getFileName(), b.getFileName());
			}
		}.sort(trs);
	}


	/** returns a value which is the same across the tracks, given the getter, or null */
	protected String getIfSame(RTrack[] ts, Function<RTrack,String> getter)
	{
		String rv = null;
		for(RTrack t: ts)
		{
			String s = getter.apply(t);
			if(CKit.isBlank(s))
			{
				if(rv != null)
				{
					return null;
				}
			}
			else
			{
				s = s.trim();
				if(rv == null)
				{
					rv = s;
				}
				else
				{
					if(!CKit.equals(s, rv))
					{
						return null;
					}
				}
			}
		}
		return rv;
	}
	
	
	/** album hash: sorted track filenames */
	protected String hash(RTrack[] ts)
	{
		CList<String> filenames = new CList<>(ts.length);
		for(RTrack t: ts)
		{
			filenames.add(t.getFileName());
		}
		CSorter.sort(filenames);
		return Utils.computeHash(filenames);
	}

	
	protected RTrack createTrack(File f)
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
		
		String filename = f.getName();
		if(CKit.isBlank(title))
		{
			title = Utils.trimExtension(filename);
		}
		
		String hash = Utils.computeHash(f);
		
		return new RTrack(title, artist, album, year, filename, hash);
	}
	
	
	public int trackCount()
	{
		return tracks.size();
	}
	
	
	public Track randomJump()
	{
		int ix = random.nextInt(trackCount());
		Track t = getTrackAt(ix);
		return t;
	}
	
	
	protected Track getTrackAt(int index)
	{
		RTrack t = tracks.get(index);
		return new Track(this, t, index);
	}


	public Track nextTrack(Track track)
	{
		int ix = track.getIndex() + 1;
		if(ix >= trackCount())
		{
			ix = 0;
		}
		RTrack t = tracks.get(ix);
		return new Track(this, t, ix);
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
			RAlbum album = null;
			StringWriter wr = new StringWriter(65536);
			for(RTrack t: tracks)
			{
				RAlbum a = t.getAlbum();
				if(a != album)
				{
					a.store(wr);
					album = a;
				}
				
				t.store(wr);
			}
			
			String s = wr.toString();
			CKit.write(f, s);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}


	public String getTitle(RTrack t)
	{
		// TODO check user-defined title
		return t.getTitle();
	}


	public String getAlbumName(RTrack t)
	{
		// TODO check user-defined album name
		return t.getAlbum().getTitle();
	}
	
	
	public String getArtist(RTrack t)
	{
		// TODO check user-defined artist
		return t.getAlbum().getArtist();
	}
	
	
	public String getYear(RTrack t)
	{
		// TODO check user-defined year
		return t.getAlbum().getYear();
	}


	public File getFile(String path, String filename)
	{
		return new File(root, path + "/" + filename);
	}
}
