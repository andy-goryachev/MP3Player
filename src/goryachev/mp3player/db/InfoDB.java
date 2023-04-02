// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import goryachev.common.util.CKit;
import goryachev.common.util.CList;
import goryachev.common.util.CMap;
import goryachev.common.util.CSorter;
import goryachev.mp3player.Track;
import goryachev.mp3player.util.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * User-Entered Track Info Database.
 */
public class InfoDB
{
	private static final Log log = Log.get("InfoDB");
	private static final String IDv1 = "InfoDB|2023.0330.2037";
	private final CMap<String,Entry> entries = new CMap<>();
	private boolean modified;
	
	
	public InfoDB()
	{
	}
	
	
	public static InfoDB load(File f)
	{
		InfoDB db = new InfoDB();

		try(BufferedReader rd = new BufferedReader(new FileReader(f, CKit.CHARSET_UTF8)))
		{
			String s = rd.readLine();
			if(CKit.notEquals(IDv1, s))
			{
				throw new Exception("Unknown file format"); 
			}
			
			int line = 2;
			RAlbum album = null;
			while((s = rd.readLine()) != null)
			{
				Entry en = parseEntry(s);
				if(en != null)
				{
					db.put(en);
				}
			}
		}
		catch(FileNotFoundException ignore)
		{
		}
		catch(Exception e)
		{
			log.error(e);
		}

		db.modified = false;
		return db;
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
				
				CList<String> keys = entries.keys();
				CSorter.sort(keys);
				
				for(String k: keys)
				{
					Entry en = entries.get(k);
					writeEntry(en, wr);
				}
				
				modified = false;
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
	

	protected static Entry parseEntry(String text)
	{
		String[] ss = CKit.split(text, '|');
		if(ss.length == 5)
		{
			// TODO track number
			// TODO consistent order
			String key = Utils.decode(ss[1]);
			String title = Utils.decode(ss[2]);
			String album = Utils.decode(ss[3]);
			String artist = Utils.decode(ss[4]);
			String year = Utils.decode(ss[5]);

			return new Entry(key, title, album, artist, year);
		}
		return null;
	}


	protected static void writeEntry(Entry en, Writer wr) throws Exception
	{
		wr.write(Utils.encode(en.getKey()));
		wr.write("|");
		wr.write(Utils.encode(en.getTitle()));
		wr.write("|");
		wr.write(Utils.encode(en.getAlbum()));
		wr.write("|");
		wr.write(Utils.encode(en.getArtist()));
		wr.write("|");
		wr.write(Utils.encode(en.getYear()));
		wr.write("\n");
	}


	protected void put(Entry en)
	{
		entries.put(en.getKey(), en);
		modified = true;
	}
	
	
	public Entry get(RTrack t)
	{
		String k = t.getHash();
		return entries.get(k);
	}
	
	
	public void updateTrack(Track t)
	{
		RTrack r = t.getRTrack();
		String key = r.getHash();
		String title = t.getTitle();
		String album = t.getAlbum();
		String artist = t.getArtist();
		String year = t.getYear();
		
		Entry en = new Entry(key, title, album, artist, year);
		put(en);
		
		// TODO trigger save after short time
		// or, append new entry at the end?
		StringWriter wr = new StringWriter();
		try
		{
			writeEntry(en, wr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(wr.toString());
	}
	
	
	public boolean isModified()
	{
		return modified;
	}
	
	
	public int size()
	{
		return entries.size();
	}
	
	
	//
	
	
	protected static class Entry
	{
		private final String key;
		private final String title;
		private final String album;
		private final String artist;
		private final String year;
		
		
		public Entry(String key, String title, String album, String artist, String year)
		{
			this.key = key;
			this.title = title;
			this.album = album;
			this.artist = artist;
			this.year = year;
		}
		

		public String getAlbum()
		{
			return album;
		}


		public String getArtist()
		{
			return artist;
		}


		public String getTitle()
		{
			return title;
		}


		public String getKey()
		{
			return key;
		}

		
		public String getYear()
		{
			return year;
		}
	}
}
