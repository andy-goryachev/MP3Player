// Copyright © 2025-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.tools.randomjump;
import goryachev.common.log.Log;
import goryachev.common.util.CList;
import goryachev.common.util.CSet;
import java.util.Random;


/**
 * This class implements the "random jump" functionality with history.
 * 
 * TODO implement longest jump algorith
 * TODO how to reconcile longest jump with removals?  tombstones?
 */
public class RandomJumpManager
{
	private static final Log log = Log.get("RandomJumpManager");
	private final int size;
	/** contains hit counts */
	private final long[] hits;
	private int index = -1;
	private final Random random;
	private final CSet<Integer> deleted = new CSet<>();
	
	private static final int NUMBER_OF_ATTEMPTS = 32;
	private static final int MAX_CENTERS = 256;
	private static final int HISTORY_BUFFER_SIZE = 100;
	/** most recent entry at index 0 */
	private final CList<Integer> history = new CList<>(HISTORY_BUFFER_SIZE);
	private int historyIndex = -1;
	
	
	public RandomJumpManager(Random random, int size)
	{
		this.random = random;
		this.size = size;
		
		int ct = Math.min(size, MAX_CENTERS);
		hits = new long[ct];
	}
	
	
	/**
	 * Stops navigating history, go to the next available index.
	 * 
	 * @return the next available item index, or -1 if all items have been removed
	 */
	public int breakHistory()
	{
		if(inHistory())
		{
			index = history.get(historyIndex);
			historyIndex = -1;
		}
		return forward();
	}


	/**
	 * Removes the current item index, stops navigating history if needed,
	 * returns the next available index.
	 * 
	 * @return the next available item index, or -1 if all items have been removed
	 */
	public int remove()
	{
		if(index < 0)
		{
			return jump();
		}
		
		historyIndex = -1;
		log.info("scheduled for removal %s", index);
		deleted.add(index);
		return update(1);
	}
	
	
	/**
	 * Returns the removed indexes in an array.
	 * 
	 * @return the removed indexes
	 */
	public int[] getRemoved()
	{
		return deleted.stream()
			.mapToInt(Integer::intValue)
			.toArray();
	}
	

	/**
	 * Randomly jumps to an index that has the longest distance from all previously visited ones.
	 * 
	 * @return the index, or -1 if all items have been removed
	 */
	public int jump()
	{
		historyIndex = -1;
		if(available() == 0)
		{
			return -1;
		}

		// find the bin with the minimum number of hits
		long minDistance = Long.MAX_VALUE;
		int candidate = -1;
		for(int i=0; i<NUMBER_OF_ATTEMPTS; i++)
		{
			int ix = random.nextInt(size);
			if(isDeleted(ix))
			{
				continue;
			}
			
			int cix = binIndex(ix);
			long d = hits[cix];
			if(d == 0)
			{
				candidate = ix;
				break;
			}
			else if(d < minDistance)
			{
				candidate = ix;
				minDistance = d;
			}
		}
		
		return set(candidate);
	}


	public int forward()
	{
		return next(true);
	}
	
	
	public int backward()
	{
		return next(false);
	}
	
	
	public int size()
	{
		return size;
	}
	

	/**
	 * Returns the index of an item at the distance specified by {@code delta}.
	 */
	public int step(int delta)
	{
		if(inHistory())
		{
			index = history.get(historyIndex);
			historyIndex = -1;
		}
		
		return update(delta);
	}


	public void addToHistory(int item)
	{
		if(inHistory())
		{
			return;
		}
		
		int ix = historyIndexOf(item);
		if(ix >= 0)
		{
			history.remove(ix);
		}

		int sz = history.size();
		if(sz >= (HISTORY_BUFFER_SIZE - 1))
		{
			history.remove(sz - 1);
		}

		history.add(0, item);
		historyIndex = -1;
	}
	
	
	private int available()
	{
		return size - deleted.size();
	}
	
	
	private int binIndex(int ix)
	{
		return (int)Math.floor((double)ix * hits.length / size);
	}
	
	
	private int fromHistory(boolean forward)
	{
		if(forward)
		{
			if(historyIndex < 0)
			{
				return -1;
			}
			else if(historyIndex == 0)
			{
				// leave the history mode
				historyIndex = -1;
				return -1;
			}
			else
			{
				--historyIndex;
			}
		}
		else
		{
			// back in history
			if(historyIndex < 0)
			{
				if(history.size() < 2)
				{
					return -1;
				}
				historyIndex = 1;
			}
			else
			{
				historyIndex = Math.min(history.size() - 1, historyIndex + 1);
			}
		}
		
		index = history.get(historyIndex);
		deleted.remove(index);
		log.trace("from history[%d] index=%d", historyIndex, index);
		return index;
	}
	
	
	private int historyIndexOf(int item)
	{
		for(int i=history.size()-1; i>=0; --i)
		{
			Integer ix = history.get(i);
			if(ix.intValue() == item)
			{
				return i;
			}
		}
		return -1;
	}
	
	
	private boolean inHistory()
	{
		return (historyIndex >= 0);
	}
	
	
	private boolean isDeleted(int ix)
	{
		return deleted.contains(ix);
	}
	
	
	private int next(boolean forward)
	{
		if(index < 0)
		{
			return jump();
		}
		
		int item = fromHistory(forward);
		if(item >= 0)
		{
			return item;
		}
		
		return update(forward ? 1 : -1);
	}
	
	
	private int update(int delta)
	{
		// keep moving until hit a non-deleted index
		int ix = index;
		for(;;)
		{
			ix += delta;
			if(ix < 0)
			{
				ix = size() - 1;
			}
			else if(ix >= size())
			{
				ix = 0;
			}
			
			if(isDeleted(ix))
			{
				if(available() == 0)
				{
					log.trace("no more items");
					return -1;
				}
				
				if(delta == 0)
				{
					delta = 1;
				}
			}
			else
			{
				return set(ix);
			}
		}
	}
	
	
	private int set(int ix)
	{
		int cix = binIndex(ix);
		hits[cix]++;
		
		addToHistory(ix);
		deleted.remove(ix);
		
		index = ix;
		log.trace("index=%d", index);
		return index;
	}
}
