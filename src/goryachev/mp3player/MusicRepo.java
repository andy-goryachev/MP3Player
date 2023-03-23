// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
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
				
				CList<File> ts = null;
				for(File f: fs)
				{
					if(Utils.isMP3(f))
					{
						if(ts == null)
						{
							ts = new CList<>();
						}
						ts.add(f);
						// TODO scan tags
					}
				}
				
				if(ts != null)
				{
					File[] tracks = CKit.toArray(File.class, ts);
					entries.add(new Entry(tracks));
					trackCount += tracks.length;
					log.info("%s: %d", dir, tracks.length);
				}
			}
		}
	}
	
	
	//
	
	
	protected static class Entry
	{
		public final File[] tracks;
		
		
		public Entry(File[] tracks)
		{
			this.tracks = tracks;
		}
	}
}
