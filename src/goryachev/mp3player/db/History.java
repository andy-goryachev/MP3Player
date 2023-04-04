// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CList;
import goryachev.mp3player.Track;


/**
 * History.
 */
public class History
{
	private final int size;
	private final CList<Integer> list;
	private int index;
	
	
	public History(int size)
	{
		this.size = size;
		this.list = new CList<>(size);
	}
	
	
	public void add(int ix)
	{
		System.out.println("add: " + ix);
		if(index > 0)
		{
			int ix2 = list.get(index);
			if(ix == ix2)
			{
				return;
			}
		}
		
		list.add(0, ix);
		index = 0;
	}
	
	
	public int previous()
	{
		int ix;
		if(index + 1 < list.size())
		{
			ix = list.get(++index);
		}
		else
		{
			ix = -1;
		}
		
		System.out.println("previous: " + ix);
		return  ix;
	}
}
