// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import goryachev.common.util.CComparator;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CSorter;
import goryachev.mp3player.Track;
import goryachev.mp3player.util.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.function.Function;


/**
 * Music Database.
 */
public class MusicDB
{
	// TODO timestamps for tracks/albums
	private static final Log log = Log.get("MusicDB");
	private static final String IDv1 = "F|2023.0326.1140";
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
					RAlbum a = new RAlbum(path, title, artist, year, hash, trs.length);
					
					for(RTrack t: trs)
					{
						a.addTrack(t);
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
	protected static String getIfSame(RTrack[] ts, Function<RTrack,String> getter)
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
		String hash = Utils.computeHash(f);
		
		return new RTrack(title, artist, album, year, filename, hash);
	}
	
	
	public int trackCount()
	{
		return tracks.size();
	}
	
	
	void addTrack(RTrack t)
	{
		tracks.add(t);
	}
	
	
	public Track randomJump()
	{
		int ix = random.nextInt(trackCount());
		Track t = getTrack(ix);
		return t;
	}
	
	
	public Track getTrack(int index)
	{
		RTrack t = tracks.get(index);
		return new Track(this, t, index);
	}


	public Track nextTrack(Track track, boolean forward)
	{
		int ix = track.getIndex();
		if(forward)
		{
			ix++;
			if(ix >= trackCount())
			{
				ix = 0;
			}
		}
		else
		{
			ix--;
			if(ix < 0)
			{
				ix = trackCount() - 1;
			}
		}

		RTrack t = tracks.get(ix);
		return new Track(this, t, ix);
	}


	public void save(File f)
	{
		try
		{
			RAlbum album = null;
			try(BufferedWriter wr = new BufferedWriter(new FileWriter(f, CKit.CHARSET_UTF8)))
			{
				wr.write(IDv1);
				wr.write("\n");
				
				for(RTrack t: tracks)
				{
					RAlbum a = t.getAlbum();
					if(a != album)
					{
						a.write(wr);
						album = a;
					}
					
					t.write(wr);
				}
			}
			catch(Exception e)
			{
				log.error(e);
			}
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}


	public static MusicDB load(File root, File f)
	{
		try(BufferedReader rd = new BufferedReader(new FileReader(f, CKit.CHARSET_UTF8)))
		{
			String s = rd.readLine();
			if(CKit.notEquals(IDv1, s))
			{
				throw new Exception("Unknown file format"); 
			}
			
			MusicDB db = new MusicDB(root);
			int line = 2;
			RAlbum album = null;
			while((s = rd.readLine()) != null)
			{
				RAlbum a = RAlbum.parse(s);
				if(a == null)
				{
					RTrack t = RTrack.parse(s);
					if(t == null)
					{
						// this is an error, but let's ignore
					}
					else
					{
						if(album == null)
						{
							throw new Exception("No album before track on line " + line);
						}
						album.addTrack(t);
						db.addTrack(t);
					}
				}
				else
				{
					album = a;
				}
				
				line++;
			}
			
			// TODO validate track numbers?
			return db;
		}
		catch(FileNotFoundException ignore)
		{
		}
		catch(Exception e)
		{
			log.error(e);
		}
		return null;
	}


	public String getTitle(RTrack t)
	{
		// TODO check user-defined title
		String s = t.getTitle();
		if(CKit.isBlank(s))
		{
			s = Utils.trimExtension(t.getFileName());
		}
		return s;
	}


	public String getAlbumName(RTrack t)
	{
		// TODO check user-defined album name
		String s = t.getAlbum().getTitle();
		if(CKit.isBlank(s))
		{
			s = t.getAlbum().getPath();
			if(s != null)
			{
				int ix = s.lastIndexOf('/');
				if(ix >= 0)
				{
					s = s.substring(ix + 1);
				}
			}
		}
		return s;
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
