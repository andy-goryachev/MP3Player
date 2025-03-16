// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.common.log.Log;
import goryachev.common.util.CMap;
import goryachev.common.util.SystemTask;
import java.io.File;
import java.util.function.Consumer;


/**
 * Safe Saver Facility performs:
 * - saving to a temp file
 * - delayed writing
 * - saving in a background task
 */
public class SafeSaver
{
	private static final Log log = Log.get("SafeSaver");
	private static CMap<File,SystemTask> savers;
	private static volatile Consumer<Throwable> errorHandler;
	
	
	public static void submit(File file, Consumer<File> saver)
	{
//		SystemTask old = remove(file);
//		if(old != null)
//		{
//			old.cancel();
//		}
//		
//		SystemTask t = SystemTask.create(() ->
//		{
//			saver.accept(file);
//		});
//		add(file, t);
	}
	
	
	public static void submit(int delay, File file, Consumer<File> saver)
	{
		
	}
	
	
	public static void setErrorHandler(Consumer<Throwable> handler)
	{
		errorHandler = handler;
	}
	
	
	public static void shutdown()
	{
		
	}
	
	
	private static File tempFile(File f) throws Exception
	{
		File dir = f.getParentFile();
		String name = f.getName();
		return File.createTempFile(name, null, dir);
	}
	
	
	private static void err(Throwable e)
	{
		Consumer<Throwable> h = errorHandler;
		if(h != null)
		{
			h.accept(e);
		}
	}
	
	
	private static void save(File file, Consumer<File> saver)
	{
		try
		{
			File f = tempFile(file);

			// save
			saver.accept(f);
			
			// rename old file
			File toDelete = null;
			if(file.exists())
			{
				toDelete = tempFile(file);
				boolean ok = file.renameTo(toDelete);
				if(!ok)
				{
					log.warn("failed to rename existing file " + file + " to " + toDelete);
					return;
				}
			}
			
			boolean ok = f.renameTo(file);
			if(!ok)
			{
				log.warn("failed to rename freshly saved temp file " + f + " to " + file);
				return;
			}
			
			if(toDelete != null)
			{
				ok = toDelete.delete();
				if(!ok)
				{
					log.warn("failed to delete old file " + toDelete);
				}
			}
		}
		catch(Throwable e)
		{
			err(e);
		}
	}
}
