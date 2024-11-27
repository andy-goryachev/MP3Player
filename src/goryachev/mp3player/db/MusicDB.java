// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import goryachev.common.util.CComparator;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.FileTools;
import goryachev.fx.FxTimer;
import goryachev.mp3player.Track;
import goryachev.mp3player.cm.SearchEntry;
import goryachev.mp3player.util.Utils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import javafx.scene.image.Image;
import javafx.util.Duration;


/**
 * Music Database.
 */
public class MusicDB
{
	private static final Log log = Log.get("MusicDB");
	protected static final String DATA_FILE = "tracks.dat";
	protected static final String INFO_FILE = "info.dat";
	protected static final String IDv1 = "F|2023.0424.2319";
	protected static final Duration SAVE_DELAY = Duration.millis(1000);
	protected final File root;
	protected final File dbDir;
	protected final CList<RTrack> tracks = new CList<>();
	protected final SmallImageCache imageCache = new SmallImageCache(8);
	protected final SecureRandom random;
	protected final CMap<Integer,WeakReference<Track>> cache = new CMap<>();
	protected final History history = new History(32);
	private InfoDB infoDB;
	private FxTimer saveTimer;
	
	
	public MusicDB(File musicDir, File dbDir)
	{
		this.root = musicDir;
		this.dbDir = dbDir;
		this.random = new SecureRandom();
		this.infoDB = new InfoDB();
	}
	
	
	public static MusicDB scan(File dir, File dbDir)
	{
		MusicDB db = new MusicDB(dir, dbDir);
		long start = System.nanoTime();
		db.scanDir(dir, dir);
		double sec = (System.nanoTime() - start) / 1_000_000_000.0; 
		log.info("%d track(s) loaded in %.1f sec.", db.trackCount(), sec);
		
		File infoFile = new File(dbDir, INFO_FILE);
		db.infoDB = InfoDB.load(infoFile);
		
		return db;
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
					
					String path = Utils.pathToRoot(root, dir);
					long time = dir.lastModified();
					RAlbum a = new RAlbum(path, time, trs.length);
					
					for(RTrack t: trs)
					{
						a.addTrack(t);
						addTrack(t);
					}
					
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
			@Override
			public int compare(RTrack a, RTrack b)
			{
				return compareAsStrings(a.getFileName(), b.getFileName());
			}
		}.sort(trs);
	}

	
	protected RTrack createTrack(File f)
	{
		ID3_Info t = ID3_Info.parseID3(f, null);
		
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
		long time = f.lastModified();
		String hash = Utils.computeHash(f);
		
		return new RTrack(title, artist, album, year, filename, time, hash);
	}
	
	
	public int trackCount()
	{
		return tracks.size();
	}
	
	
	void addTrack(RTrack t)
	{
		int ix = tracks.size();
		t.setIndex(ix);
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
		if((index < 0) || (index >= tracks.size()))
		{
			return null;
		}
		
		Integer k = Integer.valueOf(index);
		WeakReference<Track> ref = cache.get(k);
		if(ref != null)
		{
			Track tr = ref.get();
			if(tr != null)
			{
				return tr;
			}
		}
		
		RTrack t = tracks.get(index);
		Track tr = new Track(this, t);
		cache.put(k, new WeakReference<>(tr));
		return tr;
	}
	
	
	public void updateTrack(Track t)
	{
		infoDB.updateTrack(t);
		
		if(saveTimer == null)
		{
			saveTimer = new FxTimer(SAVE_DELAY, this::saveInfoDB);
			saveTimer.start();
		}
		else
		{
			saveTimer.restart();
		}
	}
	
	
	public Track firstTrack(Track t)
	{
		int ix = t.getNumber0();
		return changeTrack(t, -ix);
	}
	
	
	public Track prevTrack(Track t)
	{
		return changeTrack(t, -1);
	}
	
	
	public Track nextTrack(Track t)
	{
		return changeTrack(t, 1);
	}
	
	
	public Track prevAlbum(Track t)
	{
		int delta = - t.getNumber0() - 1;
		return changeTrack(t, delta);
	}
	
	
	public Track nextAlbum(Track t)
	{
		int delta = t.getAlbumTrackCount() - t.getNumber0();
		return changeTrack(t, delta);
	}
	
	
	/** from history, or previous album? */
	public Track fromHistory(Track t)
	{
		int ix = history.previous();
		if(ix < 0)
		{
			return prevAlbum(t);
		}
		return getTrack(ix);
	}


	protected Track changeTrack(Track track, int delta)
	{
		int ix = track.getIndex() + delta;
		if(delta > 0)
		{
			if(ix >= trackCount())
			{
				ix = 0;
			}
		}
		else
		{
			if(ix < 0)
			{
				ix = trackCount() - 1;
			}
		}

		return getTrack(ix);
	}


	public void save()
	{
		try
		{
			File f = new File(dbDir, DATA_FILE);
			FileTools.ensureParentFolder(f);
			
			RAlbum album = null;
			try(BufferedWriter wr = new BufferedWriter(new FileWriter(f, CKit.CHARSET_UTF8)))
			{
				wr.write(IDv1);
				wr.write("\n");
				
				for(RTrack t: tracks)
				{
					RAlbum a = t.getRAlbum();
					if(a != album)
					{
						a.write(wr);
						album = a;
					}
					
					t.write(wr);
				}
			}
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}
	
	
	public void saveInfoDB()
	{
		try
		{
			if(infoDB.isModified())
			{
				File f = new File(dbDir, INFO_FILE);
				FileTools.ensureParentFolder(f);

				infoDB.save(f);
			}
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}


	public static MusicDB load(File root, File dbDir)
	{
		File dbFile = new File(dbDir, DATA_FILE);
		File infoFile = new File(dbDir, INFO_FILE);

		try(BufferedReader rd = new BufferedReader(new FileReader(dbFile, CKit.CHARSET_UTF8)))
		{
			String s = rd.readLine();
			if(CKit.notEquals(IDv1, s))
			{
				throw new Exception("Unknown file format"); 
			}
			
			MusicDB db = new MusicDB(root, dbDir);
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
			
			db.infoDB = InfoDB.load(infoFile);
			
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
		InfoDB.Entry en = infoDB.get(t);
		if(en != null)
		{
			return en.getTitle();
		}
		
		String s = t.getTitle();
		if(CKit.isBlank(s))
		{
			s = Utils.trimExtension(t.getFileName());
		}
		return s;
	}


	public String getAlbum(RTrack t)
	{
		InfoDB.Entry en = infoDB.get(t);
		if(en != null)
		{
			return en.getAlbum();
		}
		
		String s = t.getTitle();
		if(CKit.isBlank(s))
		{
			s = t.getRAlbum().getPath();
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
		InfoDB.Entry en = infoDB.get(t);
		if(en != null)
		{
			return en.getArtist();
		}
		return t.getArtist();
	}
	
	
	public String getYear(RTrack t)
	{
		InfoDB.Entry en = infoDB.get(t);
		if(en != null)
		{
			return en.getYear();
		}
		return t.getYear();
	}
	
	
	/** track number, 1-based */
	public int getTrackNumber(RTrack t)
	{
		InfoDB.Entry en = infoDB.get(t);
		if(en != null)
		{
			return en.getTrackNumber();
		}
		return t.getTrackNumber0() + 1;
	}
	
	
	public void addToHistory(Track t)
	{
		history.add(t.getIndex());
	}


	public File getFile(RTrack t)
	{
		String path = t.getRAlbum().getPath();
		String filename = t.getFileName();
		return new File(root, path + "/" + filename);
	}
	
	
	public Image getCoverArt(File dir)
	{
		Image im = imageCache.get(dir);
		if(im == null)
		{
			im = loadCoverArt(dir);
			if(im != null)
			{
				imageCache.add(dir, im);
			}
		}
		return im;
	}


	protected Image loadCoverArt(File dir)
	{
		FilenameFilter ff = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				int ix = name.lastIndexOf('.');
				if(ix > 0)
				{
					String ext = name.substring(ix + 1).toLowerCase();
					switch(ext)
					{
					case "jpg":
					case "jpeg":
					case "gif":
					case "png":
						return true;
					}
				}
				return false;
			}
		};

		File[] fs = dir.listFiles(ff);
		if(fs != null)
		{
			for(File f: fs)
			{
				try
				{
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
					try
					{
						return new Image(in);
					}
					finally
					{
						CKit.close(in);
					}
				}
				catch(Exception ignore)
				{
				}
			}
		}
		return null;
	}


	public List<SearchEntry> search(List<String> query)
	{
		String[] q = CKit.toArray(query);
		for(int i=0; i<q.length; i++)
		{
			String s = q[i];
			q[i]= s.toLowerCase(Locale.ROOT);
		}
		
		List<SearchEntry> rv = new CList<>();
		for(RTrack t: tracks)
		{
			if(isMatch(t, q))
			{
				rv.add(new SearchEntry(() ->
				{
					return getTrack(t.getIndex());
				}));
			}
		}
		return rv;
	}
	
	
	protected boolean isMatch(RTrack t, String[] query)
	{
		for(String pattern: query)
		{
			if(!isMatch(t, pattern))
			{
				return false;
			}
		}
		return true;
	}
	
	
	protected boolean isMatch(RTrack t, String pattern)
	{
		String path = getFile(t).toString();

		return
			m(pattern, getAlbum(t)) ||
			m(pattern, getArtist(t)) ||
			m(pattern, getTitle(t)) ||
			m(pattern, getYear(t)) ||
			m(pattern, path);
	}
	
	
	private static boolean m(String pattern, String text)
	{
		if(text != null)
		{
			return text.toLowerCase(Locale.ROOT).contains(pattern);
		}
		return false;
	}


	public Track findFirstTrack(File dir)
	{
		RAlbum a = findAlbum(dir);
		if(a != null)
		{
			if(a.getTrackCount() > 0)
			{
				RTrack t = a.getTrack(0);
				int ix = t.getIndex();
				return getTrack(ix);
			}
		}
		return null;
	}
	
	
	private RAlbum findAlbum(File dir)
	{
		RAlbum prev = null;
		for(RTrack t: tracks)
		{
			RAlbum a = t.getRAlbum();
			if(a != prev)
			{
				prev = a;
				File f = getFile(t);
				if(dir.equals(f.getParentFile()))
				{
					return a;
				}
			}
		}
		return null;
	}
}
