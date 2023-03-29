// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.mp3player.util.Utils;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;


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
					
					String hash = null;
					String path = Utils.pathToRoot(root, dir);
					String title = MusicDB.getIfSame(trs, (t) -> t.getTitle());
					String artist = MusicDB.getIfSame(trs, (t) -> t.getArtist());
					String year = MusicDB.getIfSame(trs, (t) -> t.getYear());
					long time = dir.lastModified();
					RAlbum a = new RAlbum(path, title, artist, year, hash, time, trs.length);
					
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
		long time = f.lastModified();
		String hash = Utils.computeHash(f);
		
		return new RTrack(title, artist, album, year, filename, time, hash);
	}


	protected static RTrack createTrack2(File f)
	{
		String title;
		String artist;
		String album;
		String year;

		try
		{
			Mp3File mf = new Mp3File(f);
			if(mf.hasId3v2Tag())
			{
				ID3v2 t = mf.getId3v2Tag();
	//			System.out.println("Track: " + t.getTrack());
	//			System.out.println("Artist: " + t.getArtist());
	//			System.out.println("Title: " + t.getTitle());
	//			System.out.println("Album: " + t.getAlbum());
	//			System.out.println("Year: " + t.getYear());
	//			System.out.println("Genre: " + t.getGenre() + " (" + t.getGenreDescription() + ")");
	//			System.out.println("Comment: " + t.getComment());
	//			System.out.println("Lyrics: " + t.getLyrics());
	//			System.out.println("Composer: " + t.getComposer());
	//			System.out.println("Publisher: " + t.getPublisher());
	//			System.out.println("Original artist: " + t.getOriginalArtist());
	//			System.out.println("Album artist: " + t.getAlbumArtist());
	//			System.out.println("Copyright: " + t.getCopyright());
	//			System.out.println("URL: " + t.getUrl());
	//			System.out.println("Encoder: " + t.getEncoder());
				
				title = t.getTitle();
				artist = t.getArtist();
				album = t.getAlbum();
				year = t.getYear();
			}
			else if(mf.hasId3v1Tag())
			{
				ID3v1 t = mf.getId3v1Tag();
	//			System.out.println("Track: " + t.getTrack());
	//			System.out.println("Artist: " + t.getArtist());
	//			System.out.println("Title: " + t.getTitle());
	//			System.out.println("Album: " + t.getAlbum());
	//			System.out.println("Year: " + t.getYear());
	//			System.out.println("Genre: " + t.getGenre() + " (" + t.getGenreDescription() + ")");
	//			System.out.println("Comment: " + t.getComment());
				
				title = t.getTitle();
				artist = t.getArtist();
				album = t.getAlbum();
				year = t.getYear();
			}
			else
			{
				title = null;
				artist = null;
				album = null;
				year = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("ERROR in file=" + f);
			e.printStackTrace();
			title = null;
			artist = null;
			album = null;
			year = null;
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
