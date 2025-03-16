// Copyright © 2023-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import java.nio.charset.Charset;


/**
 * Charset Detector Interface.
 */
public interface ICharsetDetector
{
	/** supply more data for analysis */
	public void update(byte[] bytes, int off, int len);


	/** returns the best guess, or null */
	public Charset guessCharset();
	
	
	default public void update(byte[] bytes)
	{
		update(bytes, 0, bytes.length);
	}
}
