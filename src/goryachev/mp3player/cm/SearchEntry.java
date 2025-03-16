// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.FxString;
import goryachev.mp3player.Track;
import goryachev.mp3player.db.RTrack;
import java.util.function.Supplier;


/**
 * Search Entry.
 */
public class SearchEntry
{
	private Supplier<Track> gen;
	private Track track;
	
	
	public SearchEntry(Supplier<Track> gen)
	{
		this.gen = gen;
	}
	
	
	public Track getTrack()
	{
		if(track == null)
		{
			track = gen.get();
		}
		return track;
	}
	
	
	public FxString albumProperty()
	{
		return getTrack().albumProperty();
	}
	
	
	public FxString artistProperty()
	{
		return getTrack().artistProperty();
	}
	
	
	public FxString titleProperty()
	{
		return getTrack().titleProperty();
	}
	
	
	public FxString yearProperty()
	{
		return getTrack().yearProperty();
	}
}
