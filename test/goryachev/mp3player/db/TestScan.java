// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;


/**
 * Test Scanning.
 */
public class TestScan
{
	public static void main(String[] args) throws Exception
	{
		File f = new File("d:/music");
		CList<RTrack> tracks = new CList<>();
		scanDir(tracks, f, f);
		
		StringWriter wr = new StringWriter(3_000_000);
		save(tracks, wr);
		String text = wr.toString();
		
		CKit.write(new File("test.out/" + System.currentTimeMillis() + ".txt"), text);
	}
	
	
	protected static void scanDir(CList<RTrack> tracks, File root, File dir) throws Exception
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
						scanDir(tracks, root, f);
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
						ts.add(t);
					}
				}
				
				if(ts != null)
				{
					RTrack[] trs = CKit.toArray(RTrack.class, ts);
					
					String path = Utils.pathToRoot(root, dir);
					long time = dir.lastModified();
					RAlbum a = new RAlbum(path, time, trs.length);
					
					for(RTrack t: trs)
					{
						a.addTrack(t);
					}
					
					tracks.addAll(trs);
					
					//System.out.println(String.format("%s: %d", dir, trs.length));
				}
			}
		}
	}
	
	
	protected static RTrack createTrack(File f)
	{
		ID3_Info t = ID3_Info.parseID3(f, () -> new RussianDetector());
		
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


	public static void save(List<RTrack> tracks, Writer wr)
	{
		try
		{
			RAlbum album = null;
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
